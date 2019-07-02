package crazynote;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setTitle("Owner");
        stage.setScene(scene);

        /* stage.initStyle(StageStyle.UTILITY);
        stage.setMaxHeight(1);
        stage.setMaxWidth(1);
        stage.setX(Double.MAX_VALUE); */
        stage.show();

        Note note = new Note(stage);
        note.show();
    }

    public static void main(String[] args) {
        // Improve font rendering for Linux
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        launch(args);
    }
}
