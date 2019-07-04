package crazynote

import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.stage.*;
import kotlin.properties.Delegates;
import javafx.geometry.Rectangle2D;

class Note(_owner: Window, val timestamp: LocalDateTime, _title: String, _contents: String, _colorTheme: ColorTheme, _isVisible: Boolean, _position: Pair<Double, Double>, _width: Double, _height: Double): Stage() {

    constructor(_owner: Window): this(_owner, LocalDateTime.now(), "Title", "{\"ops\":[{\"insert\":\"Some Text\\\\n\"}]}", ColorTheme.YELLOW, true, getScreenCnenter(300.0, 300.0), 300.0, 300.0)

    val contents: String 
        get() = root.textArea.contents

    var colorTheme: ColorTheme by Delegates.observable(_colorTheme) {
        _, oldColorTheme, newColorTheme ->
        scene.getStylesheets().remove(newColorTheme.cssFilePath)
        scene.getStylesheets().add(newColorTheme.cssFilePath)
        root.textArea.setBgColor(newColorTheme.code2)

        // If the note's color theme was changed, then save file
        saveNoteState("Color Theme", oldColorTheme, newColorTheme)
    }

    var isVisible: Boolean by Delegates.observable(_isVisible) {
        _, oldIsVisible, newIsVisible ->
        if(newIsVisible) show() else close()

        // If the note's visibility was changed, then save file
        saveNoteState("Visibility", oldIsVisible, newIsVisible)
    }

    val position: Pair<Double, Double>
        get() = Pair(x, y)

    private val root: NotePane

    init {
        root = NotePane(_title, _contents, _colorTheme)
        root.getStyleClass().add("note")

        initToolBar()
        initMenu()
        initTextArea()
        initNote(_owner, _position, _width, _height)
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
        titleProperty().bindBidirectional(toolBar.title.textProperty())
        toolBar.title.textProperty().addListener { 
            _, oldTitle, newTitle ->
            saveNoteState("Title", oldTitle, newTitle)
        }
    }

    private fun initMenu() {
        val menu: DropDownMenu = root.toolBar.menu
        menu.newItem.setOnAction { Note(owner).show() }

        menu.deleteItem.setOnAction { 
            FileUtil.deleteNote(this)
            close()
        }

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
            .addListener { 
                _, _, isFocused -> 
                if (!isFocused) {
                    saveNoteState("Contents", "Hide", "Hide")
                }
        }
    }

    private fun initNote(_owner: Window, _position: Pair<Double, Double>, _width: Double, _height: Double) {
        val scene: Scene = Scene(root, _width, _height, Color.TRANSPARENT)
        scene.getStylesheets().add("/app/crazynote.css")
        scene.getStylesheets().add(colorTheme.cssFilePath)

        // Set initial position
        setX(_position.first)
        setY(_position.second)

        // If the note's position was changed, then save file
        xProperty().addListener {
            _, oldX, newX ->
            saveNoteState("X", oldX, newX)
        }
        yProperty().addListener {
            _, oldY, newY ->
            saveNoteState("Y", oldY, newY)
        }

        // If the note's size was changed, then save file
        widthProperty().addListener {
            _, oldWidth, newWidth ->
            saveNoteState("Width", oldWidth, newWidth)
        }
        heightProperty().addListener {
            _, oldHeight, newHeight ->
            saveNoteState("Height", oldHeight, newHeight)
        }

        initOwner(_owner)
        initStyle(StageStyle.TRANSPARENT)
        setScene(scene)

        // Add resize function
        ResizeHelper.addResizeListener(this)
    }

    private fun saveNoteState(property: String, oldValue: Any, newValue: Any) {
        println("Update $property: $oldValue -> $newValue")
        FileUtil.saveNote(this)
    }
}

private fun getScreenCnenter(width: Double, height: Double): Pair<Double, Double> {
    val screenBounds: Rectangle2D = Screen.getPrimary().getVisualBounds()
    val centerX: Double = (screenBounds.getWidth() - width) / 2
    val centerY: Double = (screenBounds.getHeight() - height) / 2
    return Pair(centerX, centerY)
}
