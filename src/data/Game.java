package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    String queueType = "";

    List<String> blueTeam = new ArrayList<>();
    List<String> redTeam = new ArrayList<>();

    Map<String, Player> players = new HashMap<>();

    public List<String> getBlueTeam() {
        return blueTeam;
    }

    public List<String> getRedTeam() {
        return redTeam;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }
}
