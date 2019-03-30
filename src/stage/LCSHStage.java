package stage;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.WindowEvent;
import main.Util;
import propertymanager.PropertyManager;
import propertymanager.PropertyManager.Prop;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public abstract class LCSHStage extends Stage {

    static PropertyManager pm;

    public LCSHStage() {
        super();
        this.pm = PropertyManager.getPropertyManager();

        initStyle(StageStyle.TRANSPARENT);
        getIcons().add(new Image(Util.getResource(pm.get(Prop.icon))));
        setTitle(pm.get(Prop.title) + " " + pm.version());

        Scene scene = initLayout();
        scene.getStylesheets().add(pm.get(Prop.stylesheet));
        setScene(scene);
    }

    abstract Scene initLayout();

    static Label createLabel(String content, double width) {
        return createLabel("default", content, width);
    }

    static Label createLabel(String type, String content, double width) {
        return createLabel(new String[]{type}, content, width);
    }

    static Label createLabel(String[] type, String content, double width) {
        Label l = new Label(content);
        l.getStyleClass().addAll(type);
        l.setMinWidth(width);
        return l;
    }

    HBox titleBar(Button[] buttons) {
        HBox topBar = new HBox();
        Label title = new Label(pm.get(Prop.title) + " " + pm.version());
        title.setPrefSize(210, 40);
        title.getStyleClass().add("info");

        topBar.getChildren().add(title);
        topBar.getChildren().addAll(buttons);

        return topBar;
    }

    Button minimizeButton() {
        Button minimize = new Button("-");
        minimize.setPrefSize(45,40);
        minimize.setId("minButton");

        minimize.setOnAction(e->{
            Platform.runLater(() -> {
                if (SystemTray.isSupported()) {
                    hide();
                } else {
                    setIconified(true);
                }
            });
        });

        return minimize;
    }

    Button exitButton() {
        Button exit = new Button("x");
        exit.setPrefSize(45,40);
        exit.setId("closeButton");

        exit.setOnAction(e-> {
            fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        return exit;
    }

    static void addChild(Pane parent, Node child) {
        parent.getChildren().add(child);
    }
}
