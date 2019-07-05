package crazynote;

import dorkbox.systemTray.*;
import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.*;
import org.apache.log4j.BasicConfigurator;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        initSystemTray(stage);
        hidePrimaryStage(stage);

        FileUtil.getNoteDatas()
            .stream()
            .filter(NoteData::isVisible)
            .map(data -> new Note(stage, data))
            .forEach(Stage::show);
    }

    public static void main(String[] args) {
        // Improve font rendering for Linux
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        // Configure log4j
        BasicConfigurator.configure();

        launch(args);
    }

    private void initSystemTray(Stage stage) {
        SystemTray systemTray = SystemTray.get();
        if (systemTray == null) {
            try {
                throw new RuntimeException("Unable to load SystemTray!");
            } catch(Exception e){
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                alert.initStyle(StageStyle.UTILITY);
                alert.setHeaderText(null);
                alert.showAndWait();

                Platform.exit();
            }
        }

        systemTray.setTooltip("CrazyNote");
        systemTray.setImage(getClass().getResource("/app/icon.png"));

        Menu mainMenu = systemTray.getMenu();

        mainMenu.add(new Checkbox("Auto Start", e -> {
            if (((Checkbox)e.getSource()).getChecked()) {
                System.out.println("Auto Start");
            } else {
            }
        }));

        mainMenu.add(new MenuItem("New Note", e -> {
            Platform.runLater(() -> new Note(stage).show());
        }));

        mainMenu.add(new MenuItem("Manage", e -> {
            Platform.runLater(() -> System.out.println("Manage"));
        }));

        mainMenu.add(new MenuItem("About", e -> {
            Platform.runLater(() -> System.out.println("About"));
        }));

        mainMenu.add(new MenuItem("Exit", e -> {
            systemTray.shutdown();
            Platform.runLater(() -> stage.close());
        }));
    }

    private void hidePrimaryStage(Stage stage) {
        stage.initStyle(StageStyle.UTILITY);
        stage.setMaxHeight(1);
        stage.setMaxWidth(1);
        stage.setX(Double.MAX_VALUE);
        stage.show();
    }
}
