package main;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;

public class Util {

    public static InputStream getResource(String path) {
        return ClassLoader.getSystemResourceAsStream(path);
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
