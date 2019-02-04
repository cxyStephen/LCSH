package webparser;

import data.Champion;
import data.Player;
import propertymanager.PropertyManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

public class OpggParser {

    public static Player getPlayerInfo(String name) throws IOException {
        URL opgg = new URL(getOpggUrl(name));
        Document doc = Jsoup.parse(opgg, 10000);

        Player p = new Player(name);

        getRankInfo(p, doc);
        getChampInfo(p, doc);
        getRecentInfo(p, doc);
        getRecentTeammates(p,doc);

        return p;
    }

    static void getRankInfo(Player p, Document doc) {
        //display hierarchy: solo > flex > previous season > unranked
        Elements info = doc.select("div.TierRankInfo");
        Element position = info.select("div.Position").first();
        String queueType = info.select("div.RankType").text();
        String rank = info.select("div.TierRank").text();
        String lp = info.select("span.LeaguePoints").text();
        String wins = info.select("span.wins").text();
        String losses = info.select("span.losses").text();

        //if unranked in soloq, check flex rank
        if(rank.equals("Unranked") || rank.isEmpty())
            getFlexRankInfo(p, doc);
        else {
            if(position != null) {
                String pos = position.text();
                pos = pos.substring(0, pos.indexOf(" "));
                p.setMainPosition(pos);
            }
            setPlayerInfo(p, queueType, rank, lp, wins, losses);
        }
    }

    static void getFlexRankInfo(Player p, Document doc) {
        Elements flexInfo = doc.select("div.sub-tier__info");
        String flexType = flexInfo.select("div.sub-tier__rank-type").text();
        String flexRank = flexInfo.select("div.sub-tier__rank-tier").text();
        String flexStats = flexInfo.select("div.sub-tier__league-point").text();

        //if unranked in flex, check previous seasons
        if(flexRank.equals("Unranked") || flexRank.isEmpty())
            getPrevRankInfo(p, doc);
        else {
            int divIndex = flexStats.indexOf("/");
            String flexLp = flexStats.substring(0, divIndex - 2) + " LP";
            String flexWins = flexStats.substring(divIndex+1, flexStats.indexOf("W")+1);
            String flexLosses = flexStats.substring(flexStats.lastIndexOf(" "));

            setPlayerInfo(p, flexType.substring(0, flexType.lastIndexOf(" ")),
                    flexRank, flexLp, flexWins, flexLosses);
        }
    }

    private static void getPrevRankInfo(Player p, Document doc) {
        Elements prevInfo = doc.select("li.Item.tip");

        //if unranked in previous seasons, display unranked
        if(prevInfo.isEmpty())
          getUnrankedInfo(p, doc);
        else {
            Element prevSeasonElement = prevInfo.last();
            String prevSeason = prevSeasonElement.text();
            String prevRank = prevSeasonElement.attr("title");

            setPlayerInfo(p, prevSeason.substring(0, prevSeason.indexOf(" ")),
                    prevRank.substring(0, prevRank.lastIndexOf(" ")),
                    "-1 LP", "-1W", "-1L");
        }
    }

    private static void getUnrankedInfo(Player p, Document doc) {
        String level = "Level " + doc.select("span.Level.tip").text();

        setPlayerInfo(p, "Unranked", level, "-1 LP", "-1W", "-1L");
    }

    private static void setPlayerInfo(Player p, String queueType, String rank,
                                      String lp, String wins, String losses) {
        lp = lp.trim();
        wins = wins.trim();
        losses = losses.trim();

        p.setQueueType(queueType);
        p.setRank(rank);
        p.setLp(Integer.parseInt(lp.substring(0,lp.indexOf(" LP"))));
        p.setWins(Integer.parseInt(wins.substring(0,wins.indexOf("W"))));
        p.setLosses(Integer.parseInt(losses.substring(0,losses.indexOf("L"))));
    }

    static void getChampInfo(Player p, Document doc) {
        Champion[] champs = new Champion[3];
        Elements champInfo = doc.select("div.ChampionBox.Ranked");

        for(int i = 0; i < champInfo.size() && i < champs.length; i++) {
            Element e = champInfo.get(i);

            Element nameElement = e.select("div.ChampionName").first();
            Element playedElement = e.select("div.Played").first();
            Element kdaElement = e.select("div.KDAEach").first();

            String played = playedElement.select("div.Title").text();
            int numGames = Integer.parseInt(played.substring(0, played.indexOf(" ")));
            String winRatio = playedElement.text().substring(0, playedElement.text().indexOf(" "));

            double wr = Integer.parseInt(winRatio.substring(0,winRatio.indexOf("%")))/100.0;
            double avgK = Double.parseDouble(kdaElement.select("span.Kill").text());
            double avgD = Double.parseDouble(kdaElement.select("span.Death").text());
            double avgA = Double.parseDouble(kdaElement.select("span.Assist").text());

            String name = nameElement.text();
            int wins = (int)Math.round(wr*numGames);
            int losses = numGames - wins;
            int kills = (int)(Math.round(avgK * numGames));
            int deaths = (int)(Math.round(avgD * numGames));
            int assists = (int)(Math.round(avgA * numGames));

            champs[i] = new Champion(name, wins, losses, kills, deaths, assists);
        }

        p.setSeasonChamps(champs);
    }

    static void getRecentInfo(Player p, Document doc) {
        Map<String, Champion> recentChamps = new LinkedHashMap<>();

        int streak = 0;
        boolean countStreak = true;
        boolean isWinStreak = false;

        //process recent games
        Elements recentGames = doc.select("div.GameItem");
        for(Element e : recentGames) {
            String type = e.select("div.GameType").text();
            String result = e.select("div.GameResult").text();

            if(!type.toLowerCase().contains("rank") || result.contains("Remake"))
                continue;

            String champ = e.select("div.ChampionName").text();
            int k = Integer.parseInt(e.select("span.Kill").first().text());
            int d = Integer.parseInt(e.select("span.Death").first().text());
            int a = Integer.parseInt(e.select("span.Assist").first().text());
            boolean win = result.equals("Victory");

            if(!recentChamps.containsKey(champ))
                recentChamps.put(champ, new Champion(champ));
            Champion c = recentChamps.get(champ);
            c.addKills(k);
            c.addDeaths(d);
            c.addAssists(a);
            c.addResult(win);

            p.addRecentK(k);
            p.addRecentD(d);
            p.addRecentA(a);
            p.addRecentResult(win);

            if(streak == 0)
                isWinStreak = win;
            if(countStreak && win == isWinStreak)
                streak++;
            else
                countStreak = false;
        }

        p.setStreak(streak);
        p.setStreakType(isWinStreak);

        TreeSet<Champion> champSet = new TreeSet<>((c1, c2) -> {
            int diff = c1.games() - c2.games();
            if (diff != 0)
                return diff;
            ArrayList<String> orderedKeys = new ArrayList<>(recentChamps.keySet());
            return orderedKeys.indexOf(c2.name()) - orderedKeys.indexOf(c1.name());
        });
        champSet.addAll(recentChamps.values());

        Champion[] recent = new Champion[2];
        for(int i = 0; i < recent.length; i++)
            recent[i] = champSet.pollLast();

        p.setRecentChamps(recent);
    }

    public static void getRecentTeammates(Player p, Document doc){
        Element info = doc.select("div.SummonersMostGame.Box").first();
        if(info != null) {
            Elements teammates = info.select("td.SummonerName.Cell");
            for (Element e : teammates)
                p.addTeammate(e.text());
        }
    }

    public static String getOpggUrl(String name) {
        String opggUrl = PropertyManager.getPropertyManager().opggUrl();
        try {
            return opggUrl + URLEncoder.encode(name.replaceAll("\\s",""), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
