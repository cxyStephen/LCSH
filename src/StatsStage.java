import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StatsStage extends Stage{

    private ConcurrentLinkedQueue<Player> players;
    static Map<String, HBox> playerHBoxes;
    static Rectangle2D screen;
    private List<String> playerNames;

    public StatsStage(List<String> playerNames) {
        super();

        players = new ConcurrentLinkedQueue<>();
        playerHBoxes = new HashMap<>();

        screen = Screen.getPrimary().getVisualBounds();
        setAlwaysOnTop(true);
        initStyle(StageStyle.TRANSPARENT);
        this.playerNames = playerNames;

        initLayout();

        for(String name : playerNames) {
            Task task = new opggTask(players, name);
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    private void initLayout(){
        setHeight(screen.getHeight());
        setWidth(screen.getWidth()/5);
        setX(0);
        setY(0);

        GridPane grid = new GridPane();
        for(int i = 0; i < 5; i++) {
            Node box;
            if(i < playerNames.size())
                box = playerBox(playerNames.get(i));
            else
                box = emptyBox();
            grid.add(box, 0, i);
        }

        Button closeButton = new Button("\t\tclose\t\t");
        closeButton.setOnAction(e->{
            this.close();
        });
        grid.add(closeButton, 0, 5);

        Scene scene = new Scene(grid);
        setScene(scene);
    }

    private Node emptyBox() {
        VBox out = new VBox();
        out.setPrefHeight(screen.getHeight() * .18);
        out.getChildren().add(new Label("---"));
        return out;
    }

    private Node playerBox(String name) {
        HBox playerBox = new HBox();
        playerBox.setPrefHeight(screen.getHeight() * .18);
        playerBox.getChildren().add(new Label("loading " + name));
        playerHBoxes.put(name, playerBox);
        return playerBox;
    }

    static void populatePlayerBox(HBox playerBox, Player p) {
        //HBox playerBox = new HBox();
        Platform.runLater(() -> {
            playerBox.getChildren().clear();
            playerBox.setPrefHeight(screen.getHeight() * .18);

            VBox rankBox = new VBox();
            addChild(rankBox, getImage("rank", p.getRank()));
            addChild(rankBox, new Label(p.getRankString()));
            addChild(rankBox, new Label(p.getOverallWinLoss()));

            VBox infoBox = new VBox();
            addChild(infoBox, playerButton(p.getName()));

            HBox statsBox = new HBox();
            addChild(infoBox, statsBox);

            VBox overallBox = new VBox();
            addChild(overallBox, new Label("This season:"));
            String[][] overallStats = p.getOverallChampions();
            for (int i = 0; i < overallStats[0].length; i++)
                addChild(overallBox, championBox(overallStats[0][i], overallStats[1][i]));

            VBox recentBox = new VBox();
            addChild(recentBox, new Label("Past " + p.getNumRecent() + " games: " + p.getRecentWLString()));
            addChild(recentBox, new Label(p.getStreakString()));
            addChild(recentBox, new Label(p.getKdaString()));
            LinkedHashMap<String, String> recents = p.getRecentChampions();
            for (String champ : recents.keySet())
                addChild(recentBox, championBox(champ, recents.get(champ)));

            addChild(statsBox, overallBox);
            addChild(statsBox, recentBox);

            addChild(playerBox, rankBox);
            addChild(playerBox, infoBox);
            //return playerBox;
        });
    }

    static Button playerButton(String name) {
        Button btn = new Button(name);
        btn.setOnAction(e->{
            try {
                Desktop.getDesktop().browse(new URI(OpggParser.getUrl(name)));
            } catch (Exception ex) {
                ex.printStackTrace();//TODO
            }
        });
        return btn;
    }

    static Node championBox(String champ, String stats) {
        HBox championBox = new HBox();
        addChild(championBox, getImage("champ", champ));
        addChild(championBox, new Label(stats));
        return championBox;
    }

    static ImageView getImage(String type, String name) {
        if(name != null)
            name = name.replaceAll("\\s", "")
                    .replace("&#039;","")
                    .replace(".","");
        else
            name = "error";
        String url = type + "/" + name + ".png";
        InputStream urlStream = StatsStage.class.getResourceAsStream(url);
        Image img = null;
        try {
            img = new Image(urlStream);
        } catch (Exception e) {
            e.printStackTrace();
            img = new Image(StatsStage.class.getResourceAsStream("error.png"));
        }
        ImageView imgv = new ImageView(img);
        imgv.setFitHeight(40);//size map
        imgv.setPreserveRatio(true);
        return imgv;
    }

    static void addChild(Pane parent, Node child) {
        parent.getChildren().add(child);
    }

    static class opggTask extends Task {
        private final String name;
        private final ConcurrentLinkedQueue players;

        public opggTask(ConcurrentLinkedQueue players, String name) {
            this.name = name;
            this.players = players;
        }

        @Override
        protected Void call() throws IOException {
            populatePlayerBox(playerHBoxes.get(name), OpggParser.getPlayerInfo(name));
            return null;
        }
    }
}

