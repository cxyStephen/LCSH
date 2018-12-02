import java.util.*;
import java.util.stream.Collectors;

public class Player {

    private String name;

    private String rank;
    private String division;
    private String lp;
    private String overallWinLoss;

    private int streak;
    private boolean winStreak;
    private boolean countStreak;
    private int numRecentWins;
    private int numRecentLosses;
    private Map<String,Map<String, int[]>> recentChampStats;
    private Map<String, Integer> recentChamps;
    private String[][] overallChamps;
    private double recentKills;
    private double recentDeaths;
    private double recentAssists;

    public Player(String name) {
        this.name = name;
        recentChamps = new LinkedHashMap<>();
        recentChampStats = new LinkedHashMap<>();
        overallChamps = new String[2][3];
        countStreak = true;
        numRecentWins = 0;
        numRecentLosses = 0;
    }

    public void processBasicStats(String line) {
        String[] stats = line.split(" / ");

        String[] rankStats = stats[1].split(" ");
        rank = rankStats[0];
        if(stats[1].contains("LP") && rankStats.length == 2) { //challenger or master
            division = "";
            lp = rankStats[1];
        } else if (stats.length == 2){ //unranked
            rank = "Level";
            division = rankStats[1].substring(0,rankStats[1].indexOf("\"/"));
            lp = "";
        } else {
            division = rankStats[1];
            lp = rankStats[2];
        }

        //only available if ranked
        if(stats.length > 3) {
            overallWinLoss = calcWLStats(stats[2]);

            int numChamps = 0;
            String[] champs = stats[3].split(", ");
            if (stats[3].contains("-")) {
                for (String champ : champs) {
                    String champName = champ.substring(0, champ.indexOf(" - "));
                    String champStats = calcWLStats(champ.substring(champ.indexOf(" - ") + 3));
                    overallChamps[0][numChamps] = champName;
                    overallChamps[1][numChamps] = champStats;

                    if (numChamps == 2)
                        break;
                    numChamps++;
                }
            }
        }
    }

    public void addGame(String champion, boolean win, int k, int d, int a) {
        recentKills += k;
        recentDeaths += d;
        recentAssists += a;

        if(!recentChampStats.containsKey(champion)) {
            Map<String, int[]> info = new HashMap<>();
            info.put("kills", new int[]{0});
            info.put("deaths", new int[]{0});
            info.put("assists", new int[]{0});
            info.put("wins", new int[]{0});
            info.put("losses", new int[]{0});
            recentChampStats.put(champion, info);
        }
        Map<String, int[]> stats = recentChampStats.get(champion);
        stats.get("kills")[0] += k;
        stats.get("deaths")[0] += d;
        stats.get("assists")[0] += a;
        String result = win ? "wins" : "losses";
        stats.get(result)[0] += 1;

        recentChamps.merge(champion, 1, Integer::sum);

        if(streak == 0)
            winStreak = win;
        if(countStreak && win == winStreak)
            streak++;
        else
            countStreak = false;

        if(win)
            numRecentWins++;
        else
            numRecentLosses++;
    }

    private String calcWLStats(String s) {
        String[] games = s.split(" ");
        int wins = Integer.parseInt(games[0].substring(0, games[0].indexOf("W")));
        int losses = Integer.parseInt(games[1].substring(0, games[1].indexOf("L")));
        String g = (wins + losses) == 1 ? "game" : "games";
        return (wins+losses) + " " + g + "\n(" + games[4] + "WR)";
    }

    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    public String getRankString() {
        if(division.equals(""))
            return rank + " - " + lp;
        if(lp.equals(""))
            return rank + " " + division;
        return rank + " " + division + " - " + lp;
    }

    public String getOverallWinLoss() {
        return overallWinLoss;
    }

    public String[][] getOverallChampions() {
        return overallChamps;
    }

    public int getNumRecent() {
        return numRecentLosses + numRecentWins;
    }

    public String getRecentWLString(){
        return numRecentWins + "W-" + numRecentLosses + "L";
    }

    public String kdaStringHelper(double kills, double deaths, double assists, int numGames) {
        double kda = (deaths != 0) ? Math.round((kills+assists)/deaths * 100.0) / 100.0 : -1;
        double kpg = Math.round(kills/numGames * 10.0) / 10.0;
        double dpg = Math.round(deaths/numGames * 10.0) / 10.0;
        double apg = Math.round(deaths/numGames * 10.0) / 10.0;
        if (kda != -1)
            return kda + " KDA (" + kpg + "/" + dpg + "/" + apg + ")";
        return "∞ KDA (" + kpg + "/" + dpg + "/" + apg + ")";
    }

    public String getKdaString() {
        if(numRecentWins + numRecentLosses == 0)
            return "";
        return kdaStringHelper(recentKills, recentDeaths, recentAssists, numRecentLosses+numRecentWins);
    }

    public LinkedHashMap<String, String> getRecentChampions() {
        LinkedHashMap<String, Integer> topTwo =
                recentChamps.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(2).collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        LinkedHashMap<String, String> topTwoStats = new LinkedHashMap<>();
        for(String champ : topTwo.keySet()) {
            int wins = recentChampStats.get(champ).get("wins")[0];
            int losses = recentChampStats.get(champ).get("losses")[0];
            int numGames = topTwo.get(champ);
            String games = (numGames == 1) ? "game" : "games";
            String statsString = numGames + " " + games + " (" + wins + "W-" + losses + "L)\n";

            int kills = recentChampStats.get(champ).get("kills")[0];
            int deaths = recentChampStats.get(champ).get("deaths")[0];
            int assists = recentChampStats.get(champ).get("assists")[0];
            statsString += kdaStringHelper(kills, deaths, assists, numGames);

            topTwoStats.put(champ, statsString);
        }
        return topTwoStats;
    }

    public String getStreakString() {
        if(numRecentLosses + numRecentWins == 0)
            return "";
        String type = winStreak ? "win" : "loss";
        return streak + " game " + type + " streak";
    }

    public boolean isWinStreak() {
        return winStreak;
    }
}
