package crazynote;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;

public class Note extends Stage {

    private Scene scene;
    private BorderPane root;
    private GridPane toolBar;
    private Label title;
    private MenuButton menu;
    private MenuItem newItem, deleteItem, hideItem, separator, renameItem,  selectColorItem, settingItem;
    private RichTextArea textArea;

    private class DoubleWrapper {
        private double value;

        public DoubleWrapper(double value) {
            this.value = value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value + "";
        }
    } 

    public Note(Window owner) {
        root = new BorderPane();

        toolBar = createToolBar();
        DoubleWrapper xOffset = new DoubleWrapper(0.0);
        DoubleWrapper yOffset = new DoubleWrapper(0.0);
        toolBar.setOnMousePressed(e -> {
            toolBar.requestFocus();
            xOffset.setValue(e.getSceneX());
            yOffset.setValue(e.getSceneY());
        });
        toolBar.setOnMouseDragged(e -> {
            setX(e.getScreenX() - xOffset.getValue());
            setY(e.getScreenY() - yOffset.getValue());
        });
        root.setTop(toolBar);

        title = new Label("Title");
        toolBar.add(title, 0, 0);

        menu = createMenu();
        toolBar.add(menu, 1, 0);

        newItem = new MenuItem("New");
        newItem.setOnAction(e -> new Note(owner).show());

        deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> close());

        hideItem = new MenuItem("Hide");
        hideItem.setOnAction(e -> close());

        separator = new SeparatorMenuItem();
        renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initOwner(this);
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle(null);
            dialog.setHeaderText("Enter the name");
            dialog.setGraphic(null);

            String name = dialog.showAndWait().orElse("");
            title.setText(name);
        });

        selectColorItem = new MenuItem("Select Color");
        settingItem = new MenuItem("Setting");
        menu.getItems().addAll(newItem, deleteItem, hideItem, separator, renameItem, selectColorItem, settingItem);

        textArea = new RichTextArea();
        textArea.lookup(".web-view").focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                System.out.println("Save Content...");
            }
        });
        root.setCenter(textArea);

        scene = new Scene(root);
        scene.getStylesheets().add("/app/crazynote.css");

        initOwner(owner);
        initStyle(StageStyle.UNDECORATED);
        setScene(scene);
        ResizeHelper.addResizeListener(this);
    }

    private GridPane createToolBar() {
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.SOMETIMES);
        col1.setMinWidth(10.0);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.RIGHT);
        col2.setHgrow(Priority.SOMETIMES);
        col2.setMinWidth(10.0);

        RowConstraints row = new RowConstraints();
        row.setMinHeight(10.0);
        row.setVgrow(Priority.SOMETIMES);

        GridPane toolBar = new GridPane();
        toolBar.setAlignment(Pos.CENTER);
        toolBar.setPadding(new Insets(5.0, 5.0, 5.0, 12.0));
        toolBar.getColumnConstraints().addAll(col1, col2);
        toolBar.getRowConstraints().add(row);

        return toolBar;
    }

    private MenuButton createMenu() {
        FontIcon buttonIcon = new FontIcon(Material.EXPAND_MORE);
        buttonIcon.setIconSize(20);

        MenuButton menu = new MenuButton(null, buttonIcon);
        menu.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        return menu;
    }

}
