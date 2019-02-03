package data;

import main.Util;

public class Champion {

    private String name;

    private int wins;
    private int losses;

    private int kills;
    private int deaths;
    private int assists;

    public Champion(String name) {
        this(name, 0, 0, 0, 0, 0);
    }

    public Champion(String name, int wins, int losses,
                    int kills, int deaths, int assists) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
    }

    public String name(){
        return name;
    }

    public int games(){
        return wins+losses;
    }

    public int wins(){
        return wins;
    }

    public int losses(){
        return losses();
    }

    public void addKills(int n) {
        kills += n;
    }

    public void addDeaths(int n) {
        deaths += n;
    }

    public void addAssists(int n) {
        assists += n;
    }

    public void addResult(boolean win) {
        if(win)
            wins++;
        else
            losses++;
    }

    public String winLoss(){
        return wins + "W-" + losses + "L";
    }

    public int wr(){
        return (int)(Math.round(100.0 * wins/games()));
    }

    public double kda(){
        return Util.kda(kills, deaths, assists, 1);
    }

    public String avgKda() {
        return Util.avgKda(kills, deaths, assists, games(), 1);
    }
}
