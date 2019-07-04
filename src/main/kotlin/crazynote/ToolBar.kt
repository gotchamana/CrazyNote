package crazynote

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

class ToolBar(title: String): GridPane() {
    
    val title: Label = Label(title)
    val menu: DropDownMenu = DropDownMenu()

    init {
        initTitle()
        initMenu()
        initToolBar()
    }

    private fun initTitle() {
        title.getStyleClass().add("title")
        add(title, 0, 0)
    }

    private fun initMenu() {
        add(menu, 1, 0)
    }

    private fun initToolBar() {
        val col1: ColumnConstraints = ColumnConstraints()
        col1.setHgrow(Priority.SOMETIMES)
        col1.setMinWidth(10.0)

        val col2: ColumnConstraints = ColumnConstraints()
        col2.setHalignment(HPos.RIGHT)
        col2.setHgrow(Priority.SOMETIMES)
        col2.setMinWidth(10.0)

        val row: RowConstraints = RowConstraints()
        row.setMinHeight(10.0)
        row.setVgrow(Priority.SOMETIMES)

        getStyleClass().add("tool-bar")
        getColumnConstraints().addAll(col1, col2)
        getRowConstraints().add(row)
    }
}
