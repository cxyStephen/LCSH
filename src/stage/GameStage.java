package stage;

import data.Game;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

public class GameStage extends LCSHStage {

    Game game;

    TeamStatsStage blueTeamStats;
    TeamStatsStage redTeamStats;

    public GameStage(Game game) {
        super();
        this.game = game;

        blueTeamStats = new TeamStatsStage(game.getBlueTeam(), game.getChampions(),
                game.getSummonerSpells(), true);
        redTeamStats = new TeamStatsStage(game.getRedTeam(), game.getChampions(),
                game.getSummonerSpells(), false);

        blueTeamStats.show();
        redTeamStats.show();

        setOnCloseRequest( e-> {
            blueTeamStats.close();
            redTeamStats.close();
        });
    }

    Scene initLayout() {
        return new Scene(new HBox());
    }

    @Override
    public void close() {
        blueTeamStats.close();
        redTeamStats.close();
        hide();
    }
}
