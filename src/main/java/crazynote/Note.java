package crazynote;

import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;
import javafx.scene.control.MenuItem;

public class Note extends Stage {

    private Scene scene;
    private BorderPane root;
    private HBox toolBar;
    private MenuButton menu;
    private MenuItem newItem, deleteItem, hideItem, selectColorItem, settingItem;
    private RichTextArea textArea;

    public Note(Window owner) {
        root = new BorderPane();

        toolBar = new HBox();
        toolBar.setAlignment(Pos.CENTER_RIGHT);
        root.setTop(toolBar);

        FontIcon buttonIcon = new FontIcon(Material.EXPAND_MORE);
        buttonIcon.setIconSize(20);

        menu = new MenuButton(null, buttonIcon);
        menu.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        toolBar.getChildren().add(menu);

        newItem = new MenuItem("New");
        deleteItem = new MenuItem("Delete");
        hideItem = new MenuItem("Hide");
        selectColorItem = new MenuItem("Select Color");
        settingItem = new MenuItem("Setting");
        menu.getItems().addAll(newItem, deleteItem, hideItem, selectColorItem, settingItem);

        textArea = new RichTextArea();
        root.setCenter(textArea);

        scene = new Scene(root);
        scene.getStylesheets().add("/crazynote.css");

        initOwner(owner);
        setTitle("Note");
        setScene(scene);
    }
}
