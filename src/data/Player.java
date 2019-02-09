package data;

import main.Util;
import propertymanager.PropertyManager;
import propertymanager.PropertyManager.Prop;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private PropertyManager pm;

    private String name;

    private String queueType = "";
    private String rank = "";
    private int lp = -1;

    private String mainPosition = "";

    private int wins = 0;
    private int losses = 0;

    private int recentWins = 0;
    private int recentLosses = 0;

    private int recentK = 0;
    private int recentD = 0;
    private int recentA = 0;

    private int streak = 0;
    private boolean isWinStreak = false;

    private Champion[] seasonChamps;
    private Champion[] recentChamps;

    private List<String> recentTeammates = new ArrayList<>();

    private String team = "Teamless";

    private Champion currChamp;
    private String summoner1;
    private String summoner2;

    public Player(String name) {
        pm = PropertyManager.getPropertyManager();
        this.name = name.trim();
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setSeasonChamps(Champion[] seasonChamps) {
        this.seasonChamps = seasonChamps;
    }

    public void setRecentChamps(Champion[] recentChamps) {
        this.recentChamps = recentChamps;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void setStreakType(boolean isWinStreak) {
        this.isWinStreak = isWinStreak;
    }

    public void setMainPosition(String pos) {
        mainPosition = pos;
    }

    public void addRecentK(int n) {
        recentK += n;
    }

    public void addRecentD(int n) {
        recentD += n;
    }

    public void addRecentA(int n) {
        recentA += n;
    }

    public void addRecentResult(boolean win) {
        if(win)
            recentWins++;
        else
            recentLosses++;
    }

    public void addTeammate(String name) {
        recentTeammates.add(name);
    }

    public String getRank() {
        if(!rank.contains(" "))
            return rank;
        return rank.substring(0, rank.indexOf(" "));
    }

    public String getRankString() {
        switch (queueType) {
            case "Ranked Solo" :
            case "Flex 5:5" :
            case "Flex 3:3" :
                return rank + " - " + lp + "LP";
            case "Unranked":
                return rank;
            default:
                return "(" + queueType + ") " + rank;
        }
    }

    public String getOverallWinLoss() {
        String line1;
        String line2;

        int totalGames = wins+losses;
        String g = (totalGames) == 1 ? pm.get(Prop.game) : pm.get(Prop.games);
        String wr = "" + Math.round(100 * wins / totalGames);

        if(queueType.toLowerCase().contains("solo")) {
            if(!mainPosition.isEmpty()) {
                line1 = (totalGames) + " " + g + " " + "(" + wr + "%)";
                line2 = mainPosition;
            } else {
                line1 = (totalGames) + " " + g;
                line2 = "(" + wr + "%WR)";
            }
        } else if (queueType.toLowerCase().contains("flex")){
            line1 = (totalGames) + " " + g + " " + "(" + wr + "%)";
            line2 = queueType.toUpperCase().replace(":", "v");
        } else {
            line1 = "Unranked";
            line2 = "";
        }

        return line1 + "\n" + line2;
    }

    public String getName() {
        return name;
    }

    public String getTeam() { return team; }

    public String getQueueType() {
        return queueType;
    }

    public int getNumRecent() {
        return recentWins + recentLosses;
    }

    public boolean isWinStreak(){
        return isWinStreak;
    }

    public String getRecentWLString() {
        return recentWins + "W-" + recentLosses + "L";
    }

    public String getStreakString() {
        if(streak == 0)
            return pm.get(Prop.no_streak);

        String streakType = isWinStreak ? pm.get(Prop.win) : pm.get(Prop.loss);
        return String.format(pm.get(Prop.streak), streak, streakType);
    }

    public String getKdaString(){
        if(recentLosses + recentLosses == 0)
            return "";

        return Util.kdaString(recentK, recentD, recentA, 2) + " KDA (" +
                Util.avgKda(recentK, recentD, recentA, recentWins + recentLosses, 1) + ")";
    }

    public String getSeasonTitle() {
        if(queueType.toLowerCase().contains("rank") || queueType.toLowerCase().contains("flex"))
            return pm.get(Prop.season_stats);
        return pm.get(Prop.prev_season);
    }

    public Champion[] getSeasonChamps(){
        return seasonChamps;
    }

    public Champion[] getRecentChamps() {
        return recentChamps;
    }

    public List<String> getRecentTeammates() {
        return recentTeammates;
    }
}
