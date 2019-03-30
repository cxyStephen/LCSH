package stage;

import main.Util;
import propertymanager.PropertyManager.Prop;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;

import java.awt.TrayIcon;
import java.awt.SystemTray;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class MainStage extends LCSHStage{

    private TrayIcon trayIcon;

    double xOffset;
    double yOffset;

    public MainStage(){
        super();
        createTrayIcon();
    }

    Scene initLayout() { //todo: clean this up
        BorderPane main = new BorderPane();
        main.setPrefWidth(300);
        main.setMaxWidth(300);
        main.setId("infoBox");

        Button[] topButtons = {minimizeButton(), exitButton()};
        HBox topBar = titleBar(topButtons);
        topBar.setOnMousePressed(event -> {
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
        });

        topBar.setOnMouseDragged(event -> {
            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);
        });

        main.setTop(topBar);

        Label info = new Label(pm.get(Prop.info));
        info.setId("infoText");
        info.setWrapText(true);

        Label bottom = new Label(String.format("%s (%s)", pm.get(Prop.copyright), pm.get(Prop.website)));
        bottom.getStyleClass().add("default");
        bottom.setStyle(bottom.getStyle() + "-fx-padding: 15;");

        main.setCenter(info);
        main.setBottom(bottom);

        return new Scene(main);
    }

    private void createTrayIcon() {
        if(!SystemTray.isSupported())
            return;

        SystemTray tray = SystemTray.getSystemTray();
        Image fximg = new Image(Util.getResource(pm.get(Prop.icon_small)));
        BufferedImage img = SwingFXUtils.fromFXImage(fximg, null);

        trayIcon = new TrayIcon(img, pm.get(Prop.title));
        trayIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Platform.runLater(()->show());
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        try {
            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TrayIcon getTrayIcon(){return trayIcon;}
}
