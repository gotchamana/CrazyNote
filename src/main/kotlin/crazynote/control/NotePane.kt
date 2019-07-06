package crazynote.control

import crazynote.ColorTheme;
import javafx.scene.layout.BorderPane;

class NotePane(title: String, contents: String, colorTheme: ColorTheme): BorderPane() {

    val toolBar: ToolBar = ToolBar(title)
    val textArea: RichTextArea = RichTextArea(contents, colorTheme)

    init {
        setTop(toolBar)
        setCenter(textArea)
    } 
} 
