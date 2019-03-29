package stage;

import data.Champion;
import data.Player;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import main.Util;
import propertymanager.PropertyManager.Prop;
import webparser.OpggParser;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class TeamStatsStage extends StatsStage {

    boolean isBlueTeam;
    Map<String, String> champions;
    Map<String, String[]> summonerSpells;

    public TeamStatsStage(List<String> playerNames, Map<String, String> champions,
                          Map<String, String[]> summonerSpells, boolean isBlueTeam) {

        super(playerNames);

        this.champions = champions;
        this.summonerSpells = summonerSpells;
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

    VBox overallBox(Player p) {
        VBox currentBox = new VBox(5);
        Champion champ = p.getCurrChamp();
        addChild(currentBox, createLabel("title", pm.get(Prop.game_champ_stats), 0));
        addChild(currentBox, currentChampionBox(p));

        if (champ.games() > 0) {
            DecimalFormat dmgFormatter = new DecimalFormat("#,###,###");
            addChild(currentBox, createLabel(champ.kda() + " KDA (" + champ.avgKda() + ")", 0));
            addChild(currentBox, createLabel(dmgFormatter.format(champ.avgDmg()) + " dmg", 0));
            addChild(currentBox, createLabel(champ.avgCs() + " cs", 0));
        } else {
            addChild(currentBox, createLabel(pm.get(Prop.loss), "\n" + pm.get(Prop.no_data) ,0));
        }
        return currentBox;
    }

    Node currentChampionBox(Player p) {
        Champion champ = p.getCurrChamp();

        HBox championBox = new HBox(5);

        VBox currentChampBox = new VBox();
        addChild(currentChampBox, Util.getImage("champ", champ.name(), 40));
        addChild(championBox, currentChampBox);

        HBox summonersBox = new HBox();
        String ss1 = summonerSpells.get(p.getName())[0];
        String ss2 = summonerSpells.get(p.getName())[1];
        addChild(summonersBox, Util.getImage("summonerspell", ss1, 20));
        addChild(summonersBox, Util.getImage("summonerspell", ss2, 20));
        addChild(currentChampBox, summonersBox);

        VBox statBox = new VBox(3);

        String g = (champ.games()) == 1 ? pm.get(Prop.game) : pm.get(Prop.games);

        if (champ.games() > 0) {
            addChild(statBox, createLabel(champ.games() + " " + g, 0));
            addChild(statBox, createLabel(champ.wr() + "%WR", 0));
            addChild(statBox, createLabel("smaller", champ.winLoss(), 0));
        } else {
            addChild(statBox, createLabel(pm.get(Prop.loss), pm.get(Prop.first_time_champion), 0));
            statBox.setAlignment(Pos.CENTER_LEFT);
        }

        addChild(championBox, statBox);
        return championBox;
    }

    Player getRelevantStats(String name) throws IOException {
        return OpggParser.getPlayerChampInfo(name, champions.get(name));
    }

    private String teamString() {
        return isBlueTeam ? pm.get(Prop.blue) : pm.get(Prop.red);
    }
}
