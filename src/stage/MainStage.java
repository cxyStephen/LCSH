package stage;

import main.Util;
import propertymanager.PropertyManager;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
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

    public MainStage(PropertyManager pm){
        super(pm);
        createTrayIcon();
        getScene().getStylesheets().add("stylesheet.css");
    }

    Scene initLayout() {
        BorderPane main = new BorderPane();
        main.setPrefWidth(300);
        main.setMaxWidth(300);
        main.setId("infoBox");
        HBox topBar = new HBox();
        javafx.scene.control.Label title = new javafx.scene.control.Label("LCSH v" + pm.version());
        title.setPrefSize(210, 40);
        title.setId("info");
        javafx.scene.control.Button minimize = new javafx.scene.control.Button("-");
        minimize.setPrefSize(45,40);
        minimize.setId("minButton");
        javafx.scene.control.Button exit = new javafx.scene.control.Button("x");
        exit.setPrefSize(45,40);
        exit.setId("closeButton");
        topBar.getChildren().addAll(title, minimize, exit);
        main.setTop(topBar);

        minimize.setOnAction(e->{
            Platform.runLater(() -> {
                if (SystemTray.isSupported()) {
                    hide();
                } else {
                    setIconified(true);
                }
            });
        });

        exit.setOnAction(e-> {
            fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        topBar.setOnMousePressed(event -> {
            xOffset = getX() - event.getScreenX();
            yOffset = getY() - event.getScreenY();
        });

        topBar.setOnMouseDragged(event -> {
            setX(event.getScreenX() + xOffset);
            setY(event.getScreenY() + yOffset);
        });

        javafx.scene.control.Label info = new javafx.scene.control.Label(
                "Leave this window open or minimized and copy the join messages in champion select.\n\n" +
                        "Make sure you get the summoner name and \"joined the lobby\"\n\n" +
                        "e.g:\n\tŚtephen joined the lobby\n\tBCKC joined the lobby\n\tTheorize joined the lobby\n"
        );
        info.setId("infoText");
        info.setWrapText(true);

        javafx.scene.control.Label bottom = new Label("© Stephen X Chen (cxystephen.com)");
        bottom.setId("default");
        bottom.setStyle(bottom.getStyle() + "-fx-padding: 15;");

        main.setCenter(info);
        main.setBottom(bottom);

        return new Scene(main);
    }

    private void createTrayIcon() {
        if(!SystemTray.isSupported())
            return;

        SystemTray tray = SystemTray.getSystemTray();
        Image fximg = new Image(Util.getResource("iconsmall.png"));
        BufferedImage img = SwingFXUtils.fromFXImage(fximg, null);

        trayIcon = new TrayIcon(img, "LCSH");
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
