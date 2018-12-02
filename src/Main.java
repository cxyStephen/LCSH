import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final String version = "0.3";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller controller = new Controller();

        Parent root = new StackPane();
        primaryStage.setTitle("LCSH v" + version);
        ((StackPane) root).getChildren().add(new Label(
                "hi this is the second version of my champ select helper. it looks nicer.\n" +
                "leave this window open and copy the join messages in champ select.\n" +
                 "it doesnt have to be exact as long as you get the name and \"joined the lobby\"\n\n" +
                "e.g:\nŚtephen joined the lobby\nBCKC joined the lobby\nTheorize joined the lobby\n\n" +
                "© Stephen X Chen (cxystephen.com) "
        ));
        primaryStage.setScene(new Scene(root, 450, 200));
        primaryStage.setOnCloseRequest(event -> {
            controller.exit();
        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
