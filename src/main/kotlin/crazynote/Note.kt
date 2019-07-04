package crazynote

import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import kotlin.properties.Delegates;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

class Note(owner: Window, val timestamp: LocalDateTime, title: String, var contents: String, colorTheme: ColorTheme, isVisible: Boolean, var position: Pair<Double, Double>): Stage() {

    var colorTheme: ColorTheme by Delegates.observable(colorTheme) {
        _, _, colorTheme ->
        scene.getStylesheets().remove(colorTheme.cssFilePath)
        scene.getStylesheets().add(colorTheme.cssFilePath)
        root.textArea.setBgColor(colorTheme.code2)
    }

    var isVisible: Boolean by Delegates.observable(isVisible) {
        _, _, isVisible ->
        if(isVisible) show() else close()
    }

    private val root: NotePane

    constructor(owner: Window): this(owner, LocalDateTime.now(), "", "{\"ops\":[{\"insert\":\"Some Text\\\\n\"}]}", ColorTheme.YELLOW, true, getScreenCnenter(300.0, 300.0))

    init {
        root = NotePane(title, contents, colorTheme)
        root.getStyleClass().add("note")

        initToolBar()
        initMenu()
        initTextArea()
        initNote(owner)
    }

    private fun initToolBar() {
        val toolBar: ToolBar = root.toolBar

        // Move the note by dragging the toolbar
        var xOffset: Double = 0.0
        var yOffset: Double = 0.0
        toolBar.setOnMousePressed {
            root.toolBar.requestFocus()
            xOffset = it.getSceneX()
            yOffset = it.getSceneY()
        }

        toolBar.setOnMouseDragged {
            setX(it.getScreenX() - xOffset)
            setY(it.getScreenY() - yOffset)
        }

        // Bind the stage's title to note's title
        toolBar.title.textProperty().bind(titleProperty())
    }

    private fun initMenu() {
        val menu: DropDownMenu = root.toolBar.menu
        menu.newItem.setOnAction { Note(owner).show() }

        menu.deleteItem.setOnAction { close() }

        menu.hideItem.setOnAction { isVisible = false }

        menu.renameItem.setOnAction {
            Platform.runLater {
                val dialog: TextInputDialog = TextInputDialog()
                dialog.initOwner(this)
                dialog.initStyle(StageStyle.UTILITY)
                dialog.setTitle("Name")
                dialog.setHeaderText("Enter the name")
                dialog.setGraphic(null)

                title = dialog.showAndWait().orElse("")
            }
        }

        for (colorMenuItem in menu.colorMenu.getItems()) {
            if (colorMenuItem is ColorMenuItem) {
                colorMenuItem.setOnAction {
                    colorTheme = colorMenuItem.colorTheme
                }
            } 
        }
    }

    private fun initTextArea() {
        root.textArea.lookup(".web-view")
            .focusedProperty()
            .addListener { _, _, isFocused -> 
                if (!isFocused) {
                    println("Save Content...")
                }
        }
    }

    private fun initNote(owner: Window) {
        val scene: Scene = Scene(root, 300.0, 300.0, Color.TRANSPARENT)
        scene.getStylesheets().add("/app/crazynote.css")
        scene.getStylesheets().add(colorTheme.cssFilePath)

        // Set initial position
        setX(position.first)
        setY(position.second)

        initOwner(owner)
        initStyle(StageStyle.TRANSPARENT)
        setScene(scene)

        // Add resize function
        ResizeHelper.addResizeListener(this)
    }
}

private fun getScreenCnenter(width: Double, height: Double): Pair<Double, Double> {
    val screenBounds: Rectangle2D = Screen.getPrimary().getVisualBounds()
    val centerX: Double = (screenBounds.getWidth() - width) / 2
    val centerY: Double = (screenBounds.getHeight() - height) / 2
    return Pair(centerX, centerY)
}
