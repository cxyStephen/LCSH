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

        Elements teams = doc.select("div.game-view-card");
        for(Element team : teams) {
            Elements players = team.select("div.gamesummoner-card");
            for(Element player : players) {
                List<String> addTo = game.getBlueTeam();
                if(player.hasClass("border-team-200"))
                    addTo = game.getRedTeam();
                String playerName = player.select("p.summoner-name").text();
                addTo.add(playerName);
            }
        }

        return game;
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
