package stage;

import data.Player;
import main.Util;
import propertymanager.PropertyManager;
import webparser.OpggParser;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatsStage extends LCSHStage{

    private static Map<String, HBox> playerHBoxes;
    private static double paneWidth;
    private static double paneHeight;
    private List<String> playerNames;

    public StatsStage(PropertyManager pm, List<String> playerNames) {
        super(pm);
        setAlwaysOnTop(true);

        playerHBoxes = new HashMap<>();
        this.playerNames = playerNames;

        addElements();

        for(String name : playerNames) {
            Task task = new opggTask(name);
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();
        }
    }

    Scene initLayout(){
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        paneWidth = screen.getWidth()*0.23;
        paneHeight = screen.getHeight();

        setHeight(paneHeight);
        setWidth(paneWidth);
        setX(0);
        setY(0);

        GridPane grid = new GridPane();
        grid.setId("main-pane");
        grid.setVgap(5);

        return new Scene(grid, Color.TRANSPARENT);
    }

    private void addElements() {
        GridPane grid = (GridPane)getScene().getRoot();
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
            addChild(rankBox, Util.getImage("rank", p.getRank(), rankBox.getPrefWidth()));
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
        addChild(championBox, Util.getImage("champ", champ, 40));

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
        Label info = createLabel("info", "LCSH v" + pm.version() + "  -  © Stephen X Chen", paneWidth * 0.7);
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

