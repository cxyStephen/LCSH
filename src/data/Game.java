package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    boolean isStarted;

    String queueType = "";
    String map = "";

    List<String> blueTeam = new ArrayList<>();
    List<String> redTeam = new ArrayList<>();

    Map<String, String> champions = new HashMap<>();
    Map<String, String[]> summonerSpells = new HashMap<>();

    int totalRank = 0;
    int numRankedPlayers = 0;

    public List<String> getBlueTeam() {
        return blueTeam;
    }

    public List<String> getRedTeam() {
        return redTeam;
    }

    public Map<String, String> getChampions() {
        return champions;
    }

    public Map<String, String[]> getSummonerSpells() {
        return summonerSpells;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean s) {
        isStarted = s;
    }

    public String getQueueType() {
        return queueType;
    }

    public String getMap() {
        return map;
    }

    public void addRank(int i) {
        totalRank += i;
        numRankedPlayers++;
    }

    public int getAverageRank() {
        if (numRankedPlayers == 0)
            return -1;
        return (int) Math.round((double) totalRank / numRankedPlayers);
    }

    public void setGameType(String queueType, String map) {
        this.queueType = queueType;
        this.map = map;
    }
}
