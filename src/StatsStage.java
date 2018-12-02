import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private static Map<String, HBox> playerHBoxes;
    private static double paneWidth;
    private static double paneHeight;
    private List<String> playerNames;

    public StatsStage(List<String> playerNames) {
        super();

        players = new ConcurrentLinkedQueue<>();
        playerHBoxes = new HashMap<>();

        setAlwaysOnTop(true);
        initStyle(StageStyle.TRANSPARENT);
        this.playerNames = playerNames;

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        paneWidth = screen.getWidth()*0.23;
        paneHeight = screen.getHeight();

        initLayout();

        for(String name : playerNames) {
            Task task = new opggTask(name);
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    private void initLayout(){

        setHeight(paneHeight);
        setWidth(paneWidth);
        setX(0);
        setY(0);

        GridPane grid = new GridPane();
        grid.setId("main-pane");
        grid.setVgap(5);
        for(int i = 0; i < 5; i++) {
            Node box;
            if(i < playerNames.size()) {
                box = playerBox(playerNames.get(i));
                if(i != Math.max(playerNames.size() - 1, 4))
                    grid.add(new Separator(Orientation.HORIZONTAL), 0, 2*i+1);
            }
            else
                box = emptyBox();
            grid.add(box, 0, 2*i);
        }

        grid.add(bottomBox(), 0, 9);

        Scene scene = new Scene(grid, Color.TRANSPARENT);
        scene.getStylesheets().add("stylesheet.css");
        setScene(scene);
    }

    private Node emptyBox() {
        VBox out = new VBox();
        out.setFillWidth(true);
        out.setPrefHeight(paneHeight * .18);
        out.getChildren().add(new Label("---"));
        return out;
    }

    private Node playerBox(String name) {
        HBox playerBox = new HBox(3);
        playerBox.setPrefHeight(paneHeight * .18);
        playerBox.setPrefWidth(paneWidth);
        Label loading = createLabel("loading", "loading " + name +"...", paneWidth);
        loading.setPrefHeight(playerBox.getPrefHeight());
        addChild(playerBox, loading);
        playerHBoxes.put(name, playerBox);
        return playerBox;
    }

    static void populatePlayerBox(HBox playerBox, Player p) {
        Platform.runLater(() -> {
            playerBox.getChildren().clear();

            VBox rankBox = new VBox();
            rankBox.setPrefWidth(paneWidth * 0.3);
            addChild(rankBox, getImage("rank", p.getRank(), rankBox.getPrefWidth()));
            addChild(rankBox, createLabel(p.getRank(), p.getRankString(), rankBox.getPrefWidth()));
            addChild(rankBox, createLabel(p.getOverallWinLoss(), rankBox.getPrefWidth()));

            VBox infoBox = new VBox();
            infoBox.setFillWidth(true);
            addChild(infoBox, playerButton(p.getName()));

            HBox statsBox = new HBox(3);
            statsBox.setFillHeight(true);
            addChild(infoBox, statsBox);

            VBox overallBox = new VBox(5);
            overallBox.setPrefWidth((paneWidth-rankBox.getPrefWidth()) * 0.4);
            addChild(overallBox, createLabel("title","This season:", 0));
            String[][] overallStats = p.getOverallChampions();
            for (int i = 0; i < overallStats[0].length; i++)
                addChild(overallBox, championBox(overallStats[0][i], overallStats[1][i]));

            VBox recentBox = new VBox(5);
            recentBox.setPrefWidth(paneWidth-rankBox.getPrefWidth() - overallBox.getPrefWidth());
            String recentTitle;
            switch (p.getNumRecent()) {
                case 0: recentTitle = "No recent games.";
                        break;
                case 1: recentTitle = "Last game: " + (p.isWinStreak() ? "W" : "L");
                        break;
                default: recentTitle = "Past " + p.getNumRecent() + " games: " + p.getRecentWLString();
            }
            addChild(recentBox, createLabel("title",recentTitle,0));
            String streakType = p.isWinStreak() ? "win" : "loss";
            addChild(recentBox, createLabel(streakType, p.getStreakString(),0));
            addChild(recentBox, createLabel(p.getKdaString(),0));
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
        btn.setId("playerButton");
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
        HBox championBox = new HBox(5);
        addChild(championBox, getImage("champ", champ, 40));

        if(stats == null)
            return championBox;

        VBox statBox = new VBox();
        String[] s = stats.split("\n");
        if(s.length == 2) {
            addChild(statBox, createLabel(s[0], 0));
            addChild(statBox, createLabel("smaller", s[1], 0));
        }
        addChild(championBox, statBox);
        return championBox;
    }

     Node bottomBox(){
        HBox box = new HBox();
        Label info = createLabel("info", "LCSH v" + Main.version + "  -  © Stephen X Chen", paneWidth * 0.7);
        info.setPrefHeight(paneHeight * 0.05);
        addChild(box, info);
        Button exit = new Button("close");
        exit.setId("closeButton");
        exit.setPrefWidth(paneWidth * 0.3);
        exit.setPrefHeight(paneHeight * 0.05);
        exit.setOnAction(e->{
            this.close();
        });
        addChild(box, exit);
        return box;
    }

    static ImageView getImage(String type, String name, double width) {
        if(name != null)
            name = name.replaceAll("\\s", "")
                    .replace("&#039;","")
                    .replace(".","");
        else
            name = "default";
        String url = type + "/" + name + ".png";
        InputStream urlStream = StatsStage.class.getResourceAsStream(url);
        Image img = null;
        try {
            img = new Image(urlStream);
        } catch (Exception e) {
            img = new Image(StatsStage.class.getResourceAsStream(type + "/" + "default.png"));
        }
        ImageView imgv = new ImageView(img);
        imgv.setFitWidth(width);//size map
        imgv.setPreserveRatio(true);
        return imgv;
    }

    static Label createLabel(String content, double width) {
        return createLabel("default", content, width);
    }

    static Label createLabel(String type, String content, double width) {
        Label l = new Label(content);
        l.setId(type);
        l.setMinWidth(width);
        return l;
    }

    static void addChild(Pane parent, Node child) {
        parent.getChildren().add(child);
    }

    static class opggTask extends Task {
        private final String name;

        public opggTask(String name) {
            this.name = name;
        }

        @Override
        protected Void call() throws IOException {
            populatePlayerBox(playerHBoxes.get(name), OpggParser.getPlayerInfo(name));
            return null;
        }
    }
}

