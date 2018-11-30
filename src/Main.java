import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller controller = new Controller();

        Parent root = new StackPane();
        ((StackPane) root).getChildren().add(new Label(
                "hi this is the first functional(ish) version of my champ select helper.\n" +
                "leave this window open and copy the join messages in champ select.\n" +
                 "it doesnt have to be exact as long as you get the name and \"joined the lobby\"\n\n" +
                "e.g:\nŚtephen joined the lobby\nBCKC joined the lobby\nTheorize joined the lobby\n\n" +
                "© Stephen X Chen (cxystephen.com) "
        ));
        primaryStage.setTitle("LCSH v0.1");
        primaryStage.setScene(new Scene(root, 450, 200));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
