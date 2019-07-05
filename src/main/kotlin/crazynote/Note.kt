package crazynote

import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.*;
import kotlin.properties.Delegates;

class Note @JvmOverloads constructor(_owner: Window, val noteData: NoteData = NoteData()): Stage(StageStyle.TRANSPARENT) {

    private var colorTheme: ColorTheme by Delegates.observable(noteData.colorTheme) {
        _, _, newColorTheme ->
        scene.getStylesheets().remove(newColorTheme.cssFilePath)
        scene.getStylesheets().add(newColorTheme.cssFilePath)
        root.textArea.setBgColor(newColorTheme.code2)

        // If the note's color theme was changed, then save file
        saveNoteData("ColorTheme", newColorTheme, ColorTheme::class.java)
    }

    private var isVisible: Boolean by Delegates.observable(noteData.isVisible) {
        _, _, newVisible ->
        if(newVisible) show() else close()

        // If the note's visibility was changed, then save file
        saveNoteData("Visible", newVisible, Boolean::class.java)
    }

    private val root: NotePane

    init {
        root = NotePane(noteData.title, noteData.contents, noteData.colorTheme)
        root.getStyleClass().add("note")

        initToolBar()
        initMenu()
        initTextArea()
        initNote(_owner)
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
            _, _, newTitle ->
            saveNoteData("Title", newTitle, String::class.java)
        }
    }

    private fun initMenu() {
        val menu: DropDownMenu = root.toolBar.menu
        menu.newItem.setOnAction { Note(owner).show() }

        menu.deleteItem.setOnAction { 
            FileUtil.deleteNoteData(noteData)
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
        val textArea: RichTextArea = root.textArea
        textArea.lookup(".web-view")
            .focusedProperty()
            .addListener { 
                _, _, isFocused -> 
                if (!isFocused) {
                    saveNoteData("Contents", textArea.contents, String::class.java)
                }
        }
    }

    private fun initNote(_owner: Window) {
        val scene: Scene = Scene(root)
        scene.getStylesheets().add("/app/crazynote.css")
        scene.getStylesheets().add(colorTheme.cssFilePath)

        // Set initial position
        x = noteData.x
        y = noteData.y
        width = noteData.width
        height = noteData.height

        // If the note's position was changed, then save file
        xProperty().addListener {
            _, _, newX ->
            saveNoteData("X", newX, Double::class.java)
        }
        yProperty().addListener {
            _, _, newY ->
            saveNoteData("Y", newY, Double::class.java)
        }

        // If the note's size was changed, then save file
        widthProperty().addListener {
            _, _, newWidth ->
            saveNoteData("Width", newWidth, Double::class.java)
        }
        heightProperty().addListener {
            _, _, newHeight ->
            saveNoteData("Height", newHeight, Double::class.java)
        }

        initOwner(_owner)
        setScene(scene)

        // Add resize function
        ResizeHelper.addResizeListener(this)
    }

    private fun saveNoteData(property: String, newValue: Any, vararg parameterTypes: Class<*>) {
        val setMethod: Method = noteData::class.java.getMethod("set$property", *parameterTypes)
        setMethod.invoke(noteData, newValue)
        FileUtil.saveNoteData(noteData)
    }
}

data class NoteData(var width: Double = 300.0, var height: Double = 300.0, var x: Double = getScreenCnenterX(width), var y: Double = getScreenCnenterY(height), val timestamp: LocalDateTime = LocalDateTime.now(), var title: String = "Title", var contents: String = "{\"ops\":[{\"insert\":\"Some Text\\\\n\"}]}", var colorTheme: ColorTheme = ColorTheme.YELLOW, var isVisible: Boolean = true): Serializable

private fun getScreenCnenterX(width: Double): Double {
    val screenBounds: Rectangle2D = Screen.getPrimary().getVisualBounds()
    val centerX: Double = (screenBounds.getWidth() - width) / 2
    return centerX
}

private fun getScreenCnenterY(height: Double): Double {
    val screenBounds: Rectangle2D = Screen.getPrimary().getVisualBounds()
    val centerY: Double = (screenBounds.getHeight() - height) / 2
    return centerY
}
