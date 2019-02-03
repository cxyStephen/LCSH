package main;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;

public class Util {

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
        if(kda == -1)
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
                    .replace("&#039;","")
                    .replace(".","");
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
