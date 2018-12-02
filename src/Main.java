import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class Main extends Application {

    public static final String version = "0.3.1";
    private TrayIcon trayIcon;
    static double xOffset;
    static double yOffset;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller controller = new Controller();
        Platform.setImplicitExit(false);

        createTrayIcon(primaryStage);
        Parent root = constructWindow(primaryStage);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("LCSH v" + version);
        primaryStage.setScene(new Scene(root));
        primaryStage.getScene().getStylesheets().add("stylesheet.css");
        primaryStage.setOnCloseRequest(event -> {
            SystemTray.getSystemTray().remove(trayIcon);
            controller.exit();
            Platform.exit();
        });
        primaryStage.getIcons().add(new javafx.scene.image.Image(Main.class.getResourceAsStream("icon.png")));
        primaryStage.show();
    }

    private void createTrayIcon(Stage stage) {
        if(!SystemTray.isSupported())
            return;
        SystemTray tray = SystemTray.getSystemTray();
        Image img = null;
        try {
            img = ImageIO.read(Main.class.getResourceAsStream("iconsmall.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        trayIcon = new TrayIcon(img, "LCSH");
        trayIcon.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Platform.runLater(()->stage.show());
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private Parent constructWindow(Stage primaryStage) {
        BorderPane main = new BorderPane();
        main.setPrefWidth(300);
        main.setMaxWidth(300);
        main.setId("infoBox");
        HBox topBar = new HBox();
        Label title = new Label("LCSH v" + version);
        title.setPrefSize(210, 40);
        title.setId("info");
        Button minimize = new Button("-");
        minimize.setPrefSize(45,40);
        minimize.setId("minButton");
        Button exit = new Button("x");
        exit.setPrefSize(45,40);
        exit.setId("closeButton");
        topBar.getChildren().addAll(title, minimize, exit);
        main.setTop(topBar);

        minimize.setOnAction(e->{
            Platform.runLater(() -> {
                if (SystemTray.isSupported()) {
                    primaryStage.hide();
                } else {
                    primaryStage.setIconified(true);
                }
            });
        });

        exit.setOnAction(e-> {
            primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        topBar.setOnMousePressed(event -> {
            xOffset = primaryStage.getX() - event.getScreenX();
            yOffset = primaryStage.getY() - event.getScreenY();
        });

        topBar.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() + xOffset);
            primaryStage.setY(event.getScreenY() + yOffset);
        });

        Label info = new Label(
                "Leave this window open or minimized and copy the join messages in champion select.\n\n" +
                        "Make sure you get the summoner name and \"joined the lobby\"\n\n" +
                        "e.g:\n\tŚtephen joined the lobby\n\tBCKC joined the lobby\n\tTheorize joined the lobby\n"
        );
        info.setId("infoText");
        info.setWrapText(true);

        Label bottom = new Label("© Stephen X Chen (cxystephen.com)");
        bottom.setId("default");
        bottom.setStyle(bottom.getStyle() + "-fx-padding: 15;");

        main.setCenter(info);
        main.setBottom(bottom);
        return main;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
