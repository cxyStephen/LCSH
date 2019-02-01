package main;

import javafx.scene.input.Clipboard;
import propertymanager.PropertyManager;
import stage.StatsStage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private PropertyManager pm;

    private String joinedLobby;
    private StatsStage currentStats;

    public Controller(PropertyManager pm) {
        this.pm = pm;

        joinedLobby = " joined the lobby"; //change this to be language dependent
        initListeners();
    }

    private void initListeners(){
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        new com.sun.glass.ui.ClipboardAssistance(com.sun.glass.ui.Clipboard.SYSTEM) {
            @Override
            public void contentChanged() {
                if (clipboard.hasString())
                    processString(clipboard.getString());
            }
        };
    }

    private void processString(String s) {

        if(!s.contains(joinedLobby))
            return;

        List<String> players = new ArrayList<>();
        for(String line : s.split("\n")) {
            int joinedIndex = line.indexOf(joinedLobby);
            if(joinedIndex > 0)
                players.add(line.substring(0, joinedIndex));
            if(players.size() > 5)
                break;
        }

        if(currentStats != null)
            currentStats.close();

        currentStats = new StatsStage(pm, players);
        currentStats.show();
    }

    void setLanguage(String str) {
        throw new NotImplementedException();
    }

    public void exit(){
        if(currentStats != null)
            currentStats.close();
    }
}