package crazynote;

import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.openiconic.Openiconic;


public class Note extends Stage {

    private LocalDateTime timestamp;
    private String contents;
    private SimpleObjectProperty<ColorTheme> colorThemeProperty;
    private SimpleStringProperty titleProperty;
    private SimpleBooleanProperty visibleProperty;
    private Point2D position;

    private Window owner;
    private Scene scene;
    private BorderPane root;
    private GridPane toolBar;
    private Label titleLabel;
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
        this(owner, LocalDateTime.now(), "", "{\"ops\":[{\"insert\":\"Some Text\\\\n\"}]}", ColorTheme.YELLOW, true, null);
    }

    public Note(Window owner, LocalDateTime timestamp, String title, String contents, ColorTheme colorTheme, boolean isVisible, Point2D position) {
        this.owner = owner;
        this.timestamp = timestamp;
        titleProperty = new SimpleStringProperty(title);
        this.contents = contents;
        colorThemeProperty = new SimpleObjectProperty<>(colorTheme);
        visibleProperty = new SimpleBooleanProperty(isVisible);
        this.position = position;

        root = new BorderPane();
        root.getStyleClass().add("note");
        initGUIComponent(root);

        scene = new Scene(root, 300, 300, Color.TRANSPARENT);
        scene.getStylesheets().add("/app/crazynote.css");

        initOwner(owner);
        initStyle(StageStyle.TRANSPARENT);
        setScene(scene);

        if (position == null) {
            centerOnScreen();
        } else {
            setX(position.getX());
            setY(position.getY());
        }

        titleProperty.addListener((obs, oldValue, newValue) -> titleLabel.setText(newValue));

        colorThemeProperty.addListener((obs, oldValue, newValue) -> {
            scene.getStylesheets().remove(newValue.getCssFilePath());
            scene.getStylesheets().add(newValue.getCssFilePath());
            textArea.setBgColor(newValue.getCode2());
        });

        visibleProperty.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                show();
            } else {
                close();
            }
        });

        ResizeHelper.addResizeListener(this);
    }

    private void initGUIComponent(BorderPane root) {
        toolBar = createToolBar();
        root.setTop(toolBar);

        titleLabel = new Label(titleProperty.get());
        titleLabel.getStyleClass().add("title");
        toolBar.add(titleLabel, 0, 0);

        menu = createMenu();
        toolBar.add(menu, 1, 0);

        textArea = new RichTextArea(contents);
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
        hideItem.setOnAction(e -> setVisible(false));

        separator = new SeparatorMenuItem();

        renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> {
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.initOwner(this);
                dialog.initStyle(StageStyle.UTILITY);
                dialog.setTitle("Name");
                dialog.setHeaderText("Enter the name");
                dialog.setGraphic(null);

                String title = dialog.showAndWait().orElse("");
                setNoteTitle(title);
            });
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
        yellowTheme = createColorMenuItem(ColorTheme.YELLOW);
        greenTheme = createColorMenuItem(ColorTheme.GREEN);
        pinkTheme = createColorMenuItem(ColorTheme.PINK);
        purpleTheme = createColorMenuItem(ColorTheme.PURPLE);
        blueTheme = createColorMenuItem(ColorTheme.BLUE);
        beigeTheme = createColorMenuItem(ColorTheme.BEIGE);

        Menu colorMenu = new Menu("Select Color");
        colorMenu.getItems().addAll(yellowTheme, greenTheme, pinkTheme, purpleTheme, blueTheme, beigeTheme);

        return colorMenu;
    }

    private MenuItem createColorMenuItem(ColorTheme theme) {
        MenuItem colorTheme = new MenuItem(theme.toString());
        colorTheme.setGraphic(new Rectangle(48, 16, Color.web(theme.getCode1())));
        colorTheme.setOnAction(e -> {
            setColorTheme(theme);
        });

        return colorTheme;
    }

    public void setNoteTitle(String title) {
        titleProperty.set(title);
    }

    public void setColorTheme(ColorTheme theme) {
        colorThemeProperty.set(theme);
    }

    public void setVisible(boolean isVisible) {
        visibleProperty.set(isVisible);
    }
}
