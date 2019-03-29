package main;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.Arrays;

public class Util {

    public static final String[] RANKS =
            {"Iron", "Bronze", "Silver", "Gold", "Platinum", "Diamond", "Master", "Grandmaster", "Challenger"};

    public static final String[] DIVISIONS =
            {"I", "II", "III", "IV"};

    public static final int HIGH_ELO_INDEX = Arrays.asList(RANKS).indexOf("Master");

    public static InputStream getResource(String path) {
        return ClassLoader.getSystemResourceAsStream(path);
    }

    public static double kda(int k, int d, int a){
        return (d != 0) ? ((k+a)/(double)d) : -1;
    }

    public static double kda(int k, int d, int a, int decimals){
        double kda = kda(k, d, a);
        double factor = Math.pow(10, decimals);
        return (Math.round(kda * factor) / factor);
    }

    public static String kdaString(int k, int d, int a, int decimals) {
        double kda = kda(k, d, a, decimals);
        if(kda == -1.0)
            return "∞";
        String s = "" + kda;
        while(s.substring(s.indexOf(".")+1).length() < decimals)
            s+="0";
        return s;
    }

    public static String avgKda(int k, int d, int a, int games, int decimals) {
        if(games == 0)
            return "0/0/0";

        double factor = Math.pow(10, decimals);
        double kpg = Math.round((factor * k)/games) / factor;
        double dpg = Math.round((factor * d)/games) / factor;
        double apg = Math.round((factor * a)/games) / factor;
        return kpg + "/" + dpg + "/" + apg;
    }

    public static int divisionToInt(String division) {
        return Arrays.asList(DIVISIONS).indexOf(division);
    }

    public static int rankToInt(String rank, String division, int lp) {
        int result = 0;

        int rankInt = Arrays.asList(RANKS).indexOf(rank);
        int divisionInt = divisionToInt(division);

        if (rankInt >= HIGH_ELO_INDEX)
            result += HIGH_ELO_INDEX * (DIVISIONS.length * 100) - 100; //extra 100 is accounted for by division 1
        else if (rankInt == -1 || divisionInt == -1)
            return -1;
        else
            result += rankInt * (DIVISIONS.length * 100);

        result += (DIVISIONS.length - divisionToInt(division)) * 100;
        result += lp;

        return result;
    }

    public static String[] intToRank(int n) {
        String[] result = new String[3];

        if (n >= HIGH_ELO_INDEX * (DIVISIONS.length * 100)) {
            result[0] = "Master+";
            result[1] = "";
            result[2] = (n - HIGH_ELO_INDEX * (DIVISIONS.length * 100)) + "";

            return result;
        }

        int rankIndex = n/(DIVISIONS.length * 100);
        int divisionIndex = DIVISIONS.length - ((n % (DIVISIONS.length * 100)) / 100);
        result[0] = RANKS[rankIndex];
        result[1] = DIVISIONS[divisionIndex];
        result[2] = (n % 100) + "";

        return result;
    }

    public static String readFile(String path) throws IOException {
        InputStream in = getResource(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        reader.close();
        return out.toString();
    }

    public static ImageView getImage(String type, String name, double width) {
        if(name != null)
            name = name.replaceAll("\\s", "")
                    .replace("&#039;", "")
                    .replace(".", "")
                    .replace("'", "");
        else
            name = "default";
        String url = type + "/" + name + ".png";
        InputStream urlStream = getResource(url);
        Image img = null;
        try {
            img = new Image(urlStream);
        } catch (Exception e) {
            img = new Image(getResource(type + "/" + "default.png"));
        }
        ImageView imgv = new ImageView(img);
        imgv.setFitWidth(width);//size map
        imgv.setPreserveRatio(true);
        return imgv;
    }
}
