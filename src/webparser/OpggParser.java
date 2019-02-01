package webparser;

import data.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class OpggParser {

    static final String opggUrl = "http://na.op.gg/summoner/userName=";

    static final String statsProp = "<meta name=\"description\" content=";
    static final String gameProp = "<div class=\"GameType\">";
    static final String rankProp = "rank";
    static final String gameStatsProp = "<div class=\"GameResult\">";
    static final String championNameProp = "<div class=\"ChampionName\">";
    static final String championProp = "target=\"_blank\">";
    static final String kdaProp = "<div class=\"KDA\">";
    static final String killProp = "<span class=\"Kill\">";
    static final String deathProp = "<span class=\"Death\">";
    static final String assistProp = "<span class=\"Assist\">";
    static final String victoryProp = "Victory";

    public static Player getPlayerInfo(String name) throws IOException {
        Player p = new Player(name);

        URL opgg = new URL(getUrl(name));
        InputStreamReader isr = new InputStreamReader(opgg.openStream(), "UTF8");
        BufferedReader in = new BufferedReader(isr);

        String inputLine;
        while ((inputLine = in.readLine()) != null) {

            if(inputLine.contains(statsProp))
                p.processBasicStats(inputLine);

            if(inputLine.contains(gameProp)) {
                inputLine = in.readLine();
                if(inputLine.toLowerCase().contains(rankProp)) {

                    while(!inputLine.contains(gameStatsProp))
                        inputLine = in.readLine();
                    inputLine = in.readLine();
                    String result = inputLine.replaceAll("\\s", "");
                    result = result.substring(0,result.indexOf("</div>"));

                    while(!inputLine.contains(championNameProp))
                        inputLine = in.readLine();
                    inputLine = in.readLine();
                    String champion = getProperty(inputLine, championProp);

                    while(!inputLine.contains(kdaProp))
                        inputLine = in.readLine();
                    in.readLine();
                    inputLine = in.readLine();
                    int k = Integer.parseInt(getProperty(inputLine, killProp));
                    inputLine = in.readLine();
                    int d = Integer.parseInt(getProperty(inputLine, deathProp));
                    inputLine = in.readLine();
                    int a = Integer.parseInt(getProperty(inputLine, assistProp));

                    p.addGame(champion, result.equals(victoryProp), k, d, a);
                }
            }
        }

        return p;
    }

    public static String getUrl(String name) {
        try {
            return opggUrl + URLEncoder.encode(name.replaceAll("\\s",""), "UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    static String getProperty(String line, String prop) {
        int startIndex = line.indexOf(prop) + prop.length();
        int endIndex = line.indexOf("</");
        return line.substring(startIndex, endIndex);
    }
}
