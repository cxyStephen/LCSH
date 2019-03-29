package main;

import data.Game;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.input.Clipboard;
import propertymanager.PropertyManager;
import propertymanager.PropertyManager.Prop;
import stage.GameStage;
import stage.StatsStage;
import webparser.LolskillParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private PropertyManager pm;
    private StatsStage currentStats;
    private GameStage currentGame;

    private Thread gameStartThread;

    public Controller() {
        this.pm = PropertyManager.getPropertyManager();
        pm.setOs(System.getProperty("os.name"));
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
                players.add(line.substring(0, joinedIndex).trim());
            if(players.size() > 5)
                break;
        }

        if(currentStats != null)
            currentStats.close();

        if(currentGame != null)
            currentGame.close();

        currentStats = new StatsStage(players);
        currentStats.show();

        waitForGameStart();
    }

    private void waitForGameStart(){
        if(gameStartThread != null) {
            gameStartThread.interrupt();
        }
        Task task = new gameStartTask();
        gameStartThread = new Thread(task);
        gameStartThread.setDaemon(true);
        gameStartThread.start();
    }

    private void gameStarted(){
        String player = currentStats.getPlayerNames().get(0);

        Platform.runLater(() -> {
            if (currentGame != null)
                currentGame.close();

            Game game = null;
            try {
                game = LolskillParser.getPlayersInGameWith(player);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(game.isStarted()) {
                currentGame = new GameStage(game);
                currentGame.show();
                currentStats.close();
            }
        });
    }

    public void exit(){
        if(currentStats != null)
            currentStats.close();
    }

    class gameStartTask extends Task {
        static final int delay = 3000;
        long timeElapsed = 0;

        @Override
        protected Void call() {
            boolean started = false;
            while(!started && timeElapsed < 360000) {
                try {
                    String line;
                    Process p;
                    if (PropertyManager.getPropertyManager().getOs().contains("Windows"))
                        p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
                    else
                        p = Runtime.getRuntime().exec("ps -e");
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while ((line = input.readLine()) != null) {
                        if(line.contains("League of Legends")) {
                            started = true;
                            break;
                        }
                    }
                    input.close();
                } catch (Exception err) {
                    err.printStackTrace();
                    return null;
                }

                timeElapsed += delay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return null;
                }
                if (Thread.currentThread().isInterrupted())
                    return null;
            }

            if (started)
                gameStarted();

            return null;
        }
    }
}
