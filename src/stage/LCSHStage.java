package stage;

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

    static void addChild(Pane parent, Node child) {
        parent.getChildren().add(child);
    }
}
