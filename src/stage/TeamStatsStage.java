package stage;

import javafx.stage.Screen;

import java.util.List;

public class TeamStatsStage extends StatsStage {

    boolean isBlueTeam;

    public TeamStatsStage(List<String> playerNames, boolean isBlueTeam) {
        super(playerNames);
        this.isBlueTeam = isBlueTeam;

        if(!isBlueTeam) //red team stage should be on the right side.
            setX(Screen.getPrimary().getVisualBounds().getWidth() - paneWidth);
    }


}
