package crazynote;

import crazynote.control.Note;
import crazynote.control.NoteKt;
import crazynote.util.*;
import dorkbox.systemTray.*;
import java.util.ResourceBundle;
import javafx.application.*;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.stage.*;
import jfxtras.styles.jmetro8.JMetro;
import org.apache.log4j.BasicConfigurator;
import java.util.Locale;

public class Main extends Application {

    private ResourceBundle resource = FileUtil.getResourceBundle();

    @Override
    public void start(Stage stage) {
        initSystemTray(stage);
        hidePrimaryStage(stage);

        NoteManager.getNotes(stage)
            .stream()
            .filter(Note::isVisible)
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
            throw new RuntimeException("Unable to load SystemTray!");
        }

        systemTray.setTooltip("CrazyNote");
        systemTray.setImage(getClass().getResource("/app/icon/icon-16x16.png"));

        Menu mainMenu = systemTray.getMenu();

        mainMenu.add(new MenuItem(resource.getString("crazynote.systemtray.new"), e -> {
            Platform.runLater(() -> NoteManager.getNewNote(stage).show());
        })).setShortcut('n');

        mainMenu.add(new Separator());

        mainMenu.add(new MenuItem(resource.getString("crazynote.systemtray.show"), e -> {
            Platform.runLater(() -> {
                NoteManager.getNotes(stage)
                    .stream()
                    .filter(note -> !note.isVisible())
                    .forEach(note -> note.setVisible(true));
            });
        })).setShortcut('s');

        mainMenu.add(new MenuItem(resource.getString("crazynote.systemtray.hide"), e -> {
            Platform.runLater(() -> {
                NoteManager.getNotes(stage)
                    .stream()
                    .filter(Note::isVisible)
                    .forEach(note -> note.setVisible(false));
            });
        })).setShortcut('h');

        mainMenu.add(new Separator());

        mainMenu.add(new MenuItem(resource.getString("crazynote.systemtray.forwards"), e -> {
            Platform.runLater(() -> {
                NoteManager.getNotes(stage)
                    .stream()
                    .forEach(Stage::toFront);
            });
        })).setShortcut('f');

        mainMenu.add(new MenuItem(resource.getString("crazynote.systemtray.backwards"), e -> {
            Platform.runLater(() -> {
                NoteManager.getNotes(stage)
                    .stream()
                    .forEach(Stage::toBack);
            });
        })).setShortcut('b');

        mainMenu.add(new Separator());

        /* mainMenu.add(new Checkbox(resource.getString("crazynote.systemtray.autostart"), e -> {
            if (((Checkbox)e.getSource()).getChecked()) {
                System.out.println("Auto Start");
            } else {
            }
        })); */

        mainMenu.add(new MenuItem(resource.getString("crazynote.systemtray.about"), e -> {
            Platform.runLater(() -> {
                stage.requestFocus();

                Alert dialog = createAboutDialog(stage);
                dialog.show();

                // Dialog has width and height only if it shows first
                dialog.setX(NoteKt.getScreenCnenterX(dialog.getWidth()));
                dialog.setY(NoteKt.getScreenCnenterY(dialog.getHeight()));
            });
        })).setShortcut('a');

        mainMenu.add(new Separator());

        mainMenu.add(new MenuItem(resource.getString("crazynote.systemtray.exit"), e -> {
            systemTray.shutdown();
            Platform.runLater(() -> stage.close());
        })).setShortcut('e');
    }

    private Alert createAboutDialog(Stage stage) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        new JMetro(JMetro.Style.LIGHT).applyTheme(dialog.getDialogPane());
        dialog.initOwner(stage);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(resource.getString("crazynote.dialog.abouttitle"));
        dialog.setHeaderText(resource.getString("crazynote.dialog.aboutheadertext"));
        dialog.setContentText(resource.getString("crazynote.dialog.aboutcontenttext"));
        dialog.setGraphic(new ImageView(getClass().getResource("/app/icon/icon-64x64.png").toExternalForm()));

        return dialog;
    }

    private void hidePrimaryStage(Stage stage) {
        Scene scene = new Scene(new Region());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.setMaxHeight(1);
        stage.setMaxWidth(1);
        stage.setX(Double.MAX_VALUE);
        stage.setY(Double.MAX_VALUE);
        stage.show();
    }
}
