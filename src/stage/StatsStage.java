package stage;

import data.Champion;
import data.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import main.Util;
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
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import propertymanager.PropertyManager.Prop;

public class StatsStage extends LCSHStage{

    private Map<String, HBox> playerHBoxes;
    private double paneWidth;
    private double paneHeight;
    private List<String> playerNames;

    public StatsStage(List<String> playerNames) {
        super();
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

    Node emptyBox() {
        VBox out = new VBox();
        out.setFillWidth(true);
        out.setPrefHeight(paneHeight * .18);
        out.getChildren().add(new Label("---"));
        return out;
    }

    Node playerBox(String name) {
        HBox playerBox = new HBox(3);
        playerBox.setPrefHeight(paneHeight * .18);
        playerBox.setPrefWidth(paneWidth);
        Label loading = createLabel("loading", pm.get(Prop.loading) + " " + name + "...", paneWidth);
        loading.setPrefHeight(playerBox.getPrefHeight());
        addChild(playerBox, loading);
        playerHBoxes.put(name, playerBox);
        return playerBox;
    }

    private void populatePlayerBox(HBox playerBox, Player p) {
        Platform.runLater(() -> {
            playerBox.getChildren().clear();

            VBox rankBox = new VBox();
            rankBox.setPrefWidth(paneWidth * 0.3);
            addChild(rankBox, Util.getImage("rank", p.getRank(), rankBox.getPrefWidth()));
            addChild(rankBox, createLabel(new String[]{"rank", p.getRank()}, p.getRankString(), rankBox.getPrefWidth()));
            addChild(rankBox, createLabel(p.getOverallWinLoss(), rankBox.getPrefWidth()));

            VBox infoBox = new VBox();
            infoBox.setFillWidth(true);
            BorderPane nameBox = new BorderPane();
            nameBox.setPadding(new Insets(0, 25, 0, 0));
            nameBox.setLeft(playerButton(p.getName()));
            Label recentTeammates = recentTeammates(p);
            nameBox.setRight(recentTeammates);
            BorderPane.setAlignment(recentTeammates, Pos.CENTER_RIGHT);
            addChild(infoBox, nameBox);

            HBox statsBox = new HBox(3);
            statsBox.setFillHeight(true);
            addChild(infoBox, statsBox);

            VBox overallBox = new VBox(5);
            overallBox.setPrefWidth((paneWidth-rankBox.getPrefWidth()) * 0.4);
            addChild(overallBox, createLabel("title", p.getSeasonTitle(), 0));
            Champion[] overallStats = p.getSeasonChamps();
            for (Champion c : overallStats)
                addChild(overallBox, championBox(c, false));

            VBox recentBox = new VBox(5);
            recentBox.setPrefWidth(paneWidth-rankBox.getPrefWidth() - overallBox.getPrefWidth());
            String recentTitle;
            switch (p.getNumRecent()) {
                case 0: recentTitle = pm.get(Prop.no_games);
                        break;
                case 1: recentTitle = pm.get(Prop.last_game) + " " + (p.isWinStreak() ? "W" : "L");
                        break;
                default: recentTitle = String.format(pm.get(Prop.past_games), p.getNumRecent()) + " " + p.getRecentWLString();
            }
            addChild(recentBox, createLabel("title",recentTitle,0));
            String streakType = p.isWinStreak() ? pm.get(Prop.win) : pm.get(Prop.loss);
            addChild(recentBox, createLabel(streakType, p.getStreakString(),0));
            addChild(recentBox, createLabel(p.getKdaString(),0));
            Champion[] recents = p.getRecentChamps();
            for (Champion c : recents)
                addChild(recentBox, championBox(c, true));

            addChild(statsBox, overallBox);
            addChild(statsBox, recentBox);

            addChild(playerBox, rankBox);
            addChild(playerBox, infoBox);
        });
    }

    Button playerButton(String name) {
        Button btn = new Button(name);
        btn.setId("playerButton");
        btn.setOnAction(e->{
            try {
                Desktop.getDesktop().browse(new URI(OpggParser.getOpggUrl(name)));
            } catch (Exception ex) {
                ex.printStackTrace();//TODO
            }
        });
        return btn;
    }

    Label recentTeammates(Player p) {
        Set<Integer> teammates = new TreeSet<>();
        for(String teammate : p.getRecentTeammates()) {
            if(playerNames.contains(teammate))
                teammates.add(playerNames.indexOf(teammate) + 1);
        }

        String queuedWith = "";
        if(!teammates.isEmpty())
            queuedWith = pm.get(Prop.queued_with) + " " + teammates.toString();

        return createLabel(new String[]{"smaller","light"}, queuedWith, 1);
    }

    Node championBox(Champion champ, boolean detailed) {
        HBox championBox = new HBox(5);
        String name = (champ != null) ? champ.name() : "null";
        addChild(championBox, Util.getImage("champ", name, 40));

        if(champ == null)
            return championBox;

        VBox statBox = new VBox();
        String[] stats = new String[2];

        String g = (champ.games()) == 1 ? pm.get(Prop.game) : pm.get(Prop.games);
        if(!detailed) {
            stats[0] = champ.games() + " " + g;
            stats[1] = "(" + champ.wr() + "%WR)";
        } else {
            stats[0] = champ.games() + " " + g + " (" + champ.winLoss() + ")";
            stats[1] = champ.kda() + " KDA (" + champ.avgKda() + ")";
        }

        addChild(statBox, createLabel(stats[0], 0));
        addChild(statBox, createLabel("smaller", stats[1], 0));
        addChild(championBox, statBox);
        return championBox;
    }

     Node bottomBox(){
        HBox box = new HBox();
        String bottomText = pm.get(Prop.title) + " " + pm.version() + "  -  " + pm.get(Prop.copyright);
        Label info = createLabel("info", bottomText, paneWidth * 0.7);
        info.setPrefHeight(paneHeight * 0.05);
        addChild(box, info);
        Button exit = new Button(pm.get(Prop.close));
        exit.setId("closeButton");
        exit.setPrefWidth(paneWidth * 0.3);
        exit.setPrefHeight(paneHeight * 0.05);
        exit.setOnAction(e->{
            this.close();
        });
        addChild(box, exit);
        return box;
    }

    class opggTask extends Task {
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

