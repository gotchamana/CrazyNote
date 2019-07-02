package crazynote;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.openiconic.Openiconic;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;


public class Note extends Stage {

    private Window owner;
    private Scene scene;
    private BorderPane root;
    private GridPane toolBar;
    private Label title;
    private MenuButton menu;
    private MenuItem newItem, deleteItem, hideItem, separator, renameItem, settingItem;
    private Menu colorMenu;
    private MenuItem yellowTheme, greenTheme, pinkTheme, purpleTheme, beigeTheme, blueTheme;
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
        this.owner = owner;

        root = new BorderPane();
        initGUIComponent(root);

        scene = new Scene(root);
        scene.getStylesheets().add("/app/crazynote.css");

        initOwner(owner);
        initStyle(StageStyle.UNDECORATED);
        setScene(scene);
        ResizeHelper.addResizeListener(this);
    }

    private void initGUIComponent(BorderPane root) {
        toolBar = createToolBar();
        root.setTop(toolBar);

        title = new Label("Title");
        title.getStyleClass().add("title");
        toolBar.add(title, 0, 0);

        menu = createMenu();
        toolBar.add(menu, 1, 0);

        textArea = new RichTextArea();
        textArea.lookup(".web-view").focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                System.out.println("Save Content...");
            }
        });
        root.setCenter(textArea);
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
        toolBar.getStyleClass().add("tool-bar");
        toolBar.setAlignment(Pos.CENTER);
        toolBar.setPadding(new Insets(5.0, 5.0, 5.0, 12.0));
        toolBar.getColumnConstraints().addAll(col1, col2);
        toolBar.getRowConstraints().add(row);

        // Move the note by dragging the toolbar
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

        return toolBar;
    }

    private MenuButton createMenu() {
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
            dialog.setTitle("Name");
            dialog.setHeaderText("Enter the name");
            dialog.setGraphic(null);

            String name = dialog.showAndWait().orElse("");
            title.setText(name);
        });

        colorMenu = createColorMenu();

        settingItem = new MenuItem("Setting");

        MenuButton menu = new MenuButton();
        menu.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        menu.setGraphic(new FontIcon(Openiconic.CHEVRON_BOTTOM));
        menu.getItems().addAll(newItem, deleteItem, hideItem, separator, renameItem, colorMenu, settingItem);

        return menu;
    }

    private Menu createColorMenu() {
        yellowTheme = createColorMenuItem("Yellow", "#ffeb85", "#fff3ac", "/app/theme/yellow.css");
        greenTheme = createColorMenuItem("Green", "#b1eca6", "#cbf1c4", "/app/theme/green.css");
        pinkTheme = createColorMenuItem("Pink", "#ffbbdd", "#ffcce5", "/app/theme/pink.css");
        purpleTheme = createColorMenuItem("Purple", "#dbb7ff", "#e7cfff", "/app/theme/purple.css");
        blueTheme = createColorMenuItem("Blue", "#b7dfff", "#cde9ff", "/app/theme/blue.css");
        beigeTheme = createColorMenuItem("Beige", "#e5e5e5", "#f9f9f9", "/app/theme/beige.css");

        Menu colorMenu = new Menu("Select Color");
        colorMenu.getItems().addAll(yellowTheme, greenTheme, pinkTheme, purpleTheme, blueTheme, beigeTheme);

        return colorMenu;
    }

    private MenuItem createColorMenuItem(String color, String code1, String code2, String cssFilePath) {
        MenuItem colorTheme = new MenuItem(color);
        colorTheme.setGraphic(new Rectangle(48, 16, Color.web(code1)));
        colorTheme.setOnAction(e -> {
            scene.getStylesheets().remove(cssFilePath);
            scene.getStylesheets().add(cssFilePath);
            textArea.setBgColor(code2);
        });

        return colorTheme;
    }
}
