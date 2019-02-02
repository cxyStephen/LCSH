package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import propertymanager.Language;
import propertymanager.PropertyManager;
import propertymanager.Region;
import stage.MainStage;

import java.awt.SystemTray;

public class Main extends Application {

    @Override
    public void start(Stage dummy) {
        Platform.setImplicitExit(false);

        Controller controller = new Controller();

        MainStage mainStage = new MainStage();
        mainStage.setOnCloseRequest(event -> {
            SystemTray.getSystemTray().remove(mainStage.getTrayIcon());
            controller.exit();
            Platform.exit();
        });
        mainStage.show();
    }

    public static void main(String[] args) {launch(args);}
}
