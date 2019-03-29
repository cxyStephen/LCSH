package webparser;

import data.Game;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import propertymanager.PropertyManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class LolskillParser {

    public static Game getPlayersInGameWith(String name) throws IOException {
        Game game = new Game();

        URL lolskill = new URL(getLolskillUrl(name));
        Document doc = Jsoup.parse(lolskill, 10000);

        String queueType;
        String map;
        try {
            queueType = doc.select("h3").first().text();
            map = doc.select("h3").last().text();
        } catch (NullPointerException e) {
            return game;
        }

        game.setStarted(true);

        game.setGameType(queueType, map);

        Elements teams = doc.select("div.game-view-list");
        for(Element team : teams) {
            Elements players = team.select("div.gamesummoner-container");
            for(Element player : players) {

                List<String> addTo = game.getBlueTeam();
                Element icon = player.selectFirst("div.champion-icon");
                if(icon.hasClass("border-team-200"))
                    addTo = game.getRedTeam();

                String playerName = player.select("p.summoner-name").text();
                String champion = player.select("p.champion-name").text();

                String[] summonerSpells = new String[2];
                summonerSpells[0] = player.select("img.spell.tip").first().attr("data-spell-name");
                summonerSpells[1] = player.select("img.spell.tip").last().attr("data-spell-name");

                addTo.add(playerName);
                game.getChampions().put(playerName, champion);
                game.getSummonerSpells().put(playerName, summonerSpells);
            }
        }

        return game;
    }

    public static void preload(String name) throws IOException {
        URL lolskill = new URL(getLolskillUrl(name));
        Jsoup.parse(lolskill, 10000);
    }

    public static String getLolskillUrl(String name) {
        String lolskillUrl = PropertyManager.getPropertyManager().lolskillUrl();
        try {
            return lolskillUrl + URLEncoder.encode(name.replaceAll("\\s",""), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
