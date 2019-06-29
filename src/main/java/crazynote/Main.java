package crazynote;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();

        RichTextArea textArea = new RichTextArea();
        root.setCenter(textArea);

        Scene scene = new Scene(root);

        stage.setTitle("CrazyNote");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Improve font rendering for Linux
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        launch(args);
    }
}
