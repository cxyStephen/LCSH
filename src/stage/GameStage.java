package stage;

import data.Game;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import main.Util;
import propertymanager.PropertyManager.Prop;

import javax.swing.text.html.ImageView;

public class GameStage extends LCSHStage {

    Game game;

    TeamStatsStage blueTeamStats;
    TeamStatsStage redTeamStats;

    public GameStage(Game game) {
        super();
        setAlwaysOnTop(true);

        this.game = game;

        blueTeamStats = new TeamStatsStage(game.getBlueTeam(), game.getChampions(),
                game.getSummonerSpells(), true);
        redTeamStats = new TeamStatsStage(game.getRedTeam(), game.getChampions(),
                game.getSummonerSpells(), false);

        blueTeamStats.show();
        redTeamStats.show();

        addElements();

        setOnCloseRequest( e-> {
            blueTeamStats.close();
            redTeamStats.close();
        });
    }

    Scene initLayout() {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        BorderPane main = new BorderPane();
        setWidth(220);
        setHeight(350);
        setX(screen.getWidth()/2 - 110);
        setY(screen.getHeight()/2 - 190);
        main.setId("infoBox");

        return new Scene(main, Color.TRANSPARENT);
    }

    private void addElements() {
        BorderPane main = (BorderPane)getScene().getRoot();

        VBox vbox = new VBox(3);
        vbox.setAlignment(Pos.CENTER);

        addChild(vbox, createLabel("xlarge", game.getMap(), 0));
        addChild(vbox, createLabel("large", game.getQueueType(), 0));

        int avgRank = game.getAverageRank();

        if(avgRank != -1) {
            String[] rank = Util.intToRank(game.getAverageRank());
            if (rank[1].equals("")) {
                addChild(vbox, Util.getImage("rank", "Master", 180));
                addChild(vbox, createLabel("large", pm.get(Prop.average_rank) + ":", 0));
                addChild(vbox, createLabel(new String[]{"rank", "large", "Master"}, rank[0] + " " + rank[2] + " LP", 0));
            } else {
                addChild(vbox, Util.getImage("rank", rank[0], 180));
                addChild(vbox, createLabel("large", pm.get(Prop.average_rank) + ":", 0));
                addChild(vbox, createLabel(new String[]{"rank", "large", rank[0]}, rank[0] + " " + rank[1], 0));
            }
        } else {
            addChild(vbox, Util.getImage("rank", null, 180));
            addChild(vbox, createLabel("large", pm.get(Prop.average_rank) + ":", 0));
            addChild(vbox, createLabel(new String[]{"rank", "large", "Level"}, "Unranked", 0));
        }

        main.setTop(titleBar(new Button[]{exitButton()}));
        main.setCenter(vbox);
    }

    @Override
    public void close() {
        blueTeamStats.close();
        redTeamStats.close();
        hide();
    }
}
