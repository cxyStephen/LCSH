package main;

import javafx.scene.input.Clipboard;
import propertymanager.PropertyManager;
import propertymanager.PropertyManager.Prop;
import stage.StatsStage;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private PropertyManager pm;
    private StatsStage currentStats;

    public Controller() {
        this.pm = PropertyManager.getPropertyManager();
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

        if(!s.contains(pm.get(Prop.join)))
            return;

        List<String> players = new ArrayList<>();
        for(String line : s.split("\n")) {
            int joinedIndex = line.indexOf(pm.get(Prop.join));
            if(joinedIndex > 0)
                players.add(line.substring(0, joinedIndex));
            if(players.size() > 5)
                break;
        }

        if(currentStats != null)
            currentStats.close();

        currentStats = new StatsStage(players);
        currentStats.show();
    }

    public void exit(){
        if(currentStats != null)
            currentStats.close();
    }
}
