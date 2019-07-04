package crazynote

import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.openiconic.Openiconic;

class DropDownMenu: MenuButton() {

    val newItem: MenuItem = MenuItem("New")
    val deleteItem: MenuItem = MenuItem("Delete")
    val hideItem: MenuItem = MenuItem("Hide")
    val separator: MenuItem = SeparatorMenuItem()
    val renameItem: MenuItem = MenuItem("Rename")
    val settingItem: MenuItem = MenuItem("Setting")

    val colorMenu: Menu = Menu("Select Color")
    val yellowTheme: MenuItem = ColorMenuItem(ColorTheme.YELLOW)
    val greenTheme: MenuItem = ColorMenuItem(ColorTheme.GREEN)
    val pinkTheme: MenuItem = ColorMenuItem(ColorTheme.PINK)
    val purpleTheme: MenuItem = ColorMenuItem(ColorTheme.PURPLE)
    val beigeTheme: MenuItem = ColorMenuItem(ColorTheme.BEIGE)
    val blueTheme: MenuItem = ColorMenuItem(ColorTheme.BLUE)

    init {
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY)
        setGraphic(FontIcon(Openiconic.CHEVRON_BOTTOM))
        colorMenu.getItems().addAll(yellowTheme, greenTheme, pinkTheme, purpleTheme, blueTheme, beigeTheme)
        getItems().addAll(newItem, deleteItem, hideItem, separator, renameItem, colorMenu, settingItem)
    }
}
