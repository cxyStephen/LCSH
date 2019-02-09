package stage;

import data.Player;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import propertymanager.PropertyManager.Prop;
import webparser.OpggParser;

import java.io.IOException;
import java.util.List;

public class TeamStatsStage extends StatsStage {

    boolean isBlueTeam;

    public TeamStatsStage(List<String> playerNames, boolean isBlueTeam) {
        super(playerNames);
        this.isBlueTeam = isBlueTeam;

        if(!isBlueTeam) //red team stage should be on the right side.
            setX(Screen.getPrimary().getVisualBounds().getWidth() - paneWidth);

        setTeamText();
    }

    Node bottomBox(){
        HBox box = new HBox();
        return box;
    }

    void setTeamText() {
        GridPane grid = (GridPane)getScene().getRoot();

        for(Node n : grid.getChildren()) {
            if(GridPane.getRowIndex(n) != 9)
                continue;
            HBox box = (HBox)n;

            String bottomText = String.format(pm.get(Prop.team), teamString());
            String[] styles = new String[]{"info", teamString(), "bold"};
            Label info = createLabel(styles, bottomText, paneWidth);
            info.setPrefHeight(paneHeight * 0.05);
            addChild(box, info);
        }
    }

    Button playerButton(String name) {
        return playerButton(name, teamString());
    }

    HBox statsBox(Player p, VBox rankBox) {
        HBox statsBox = new HBox(3);
        statsBox.setFillHeight(true);
        return statsBox;
    }

    Player getRelevantStats(String name) throws IOException {
        return OpggParser.getPlayerChampInfo(name, game);
    }

    private String teamString() {
        return isBlueTeam ? pm.get(Prop.blue) : pm.get(Prop.red);
    }
}
