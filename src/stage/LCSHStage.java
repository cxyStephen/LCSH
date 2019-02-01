package stage;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import main.Util;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import propertymanager.PropertyManager;

public abstract class LCSHStage extends Stage {

    PropertyManager pm;

    public LCSHStage(PropertyManager pm) {
        super();
        this.pm = pm;

        initStyle(StageStyle.TRANSPARENT);
        getIcons().add(new Image(Util.getResource("icon.png")));
        setTitle("LCSH v" + pm.version());

        Scene scene = initLayout();
        scene.getStylesheets().add("stylesheet.css");
        setScene(scene);
    }

    abstract Scene initLayout();


    static Label createLabel(String content, double width) {
        return createLabel("default", content, width);
    }

    static Label createLabel(String type, String content, double width) {
        Label l = new Label(content);
        l.setId(type);
        l.setMinWidth(width);
        return l;
    }

    static void addChild(Pane parent, Node child) {
        parent.getChildren().add(child);
    }
}
