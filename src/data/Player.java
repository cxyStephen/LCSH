package data;

import main.Util;
import propertymanager.PropertyManager;
import propertymanager.PropertyManager.Prop;

public class Player {

    PropertyManager pm;

    String name;

    private String queueType;
    private String rank;
    private int lp = -1;

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

    public Player(String name) {
        pm = PropertyManager.getPropertyManager();
        this.name = name.trim();
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

    public String getRank() {
        if(!rank.contains(" "))
            return rank;
        return rank.substring(0, rank.indexOf(" "));
    }

    public String getRankString() {
        return rank + " - " + lp + "LP";
    }

    public String getOverallWinLoss() {
        int totalGames = wins+losses;
        String g = (totalGames) == 1 ? pm.get(Prop.game) : pm.get(Prop.games);
        return (totalGames) + " " + g + "\n(" + Math.round(100*wins/totalGames) + "%WR)";
    }

    public String getName() {
        return name;
    }

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
        String streakType = isWinStreak ? pm.get(Prop.win) : pm.get(Prop.loss);
        return String.format(pm.get(Prop.streak), streak, streakType);
    }

    public String getKdaString(){
        return Util.kdaString(recentK, recentD, recentA, 2) + " KDA (" +
                Util.avgKda(recentK, recentD, recentA, recentWins + recentLosses, 1) + ")";
    }

    public Champion[] getSeasonChamps(){
        return seasonChamps;
    }

    public Champion[] getRecentChamps() {
        return recentChamps;
    }
}
