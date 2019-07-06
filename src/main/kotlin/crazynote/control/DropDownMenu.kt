package crazynote.control

import crazynote.ColorTheme;
import crazynote.util.FileUtil;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.openiconic.Openiconic;

class DropDownMenu: MenuButton() {

    private val resource: ResourceBundle = FileUtil.getResourceBundle()

    val newItem: MenuItem = MenuItem(resource.getString("crazynote.control.menuitem.new"))
    val deleteItem: MenuItem = MenuItem(resource.getString("crazynote.control.menuitem.delete"))
    val hideItem: MenuItem = MenuItem(resource.getString("crazynote.control.menuitem.hide"))
    val separator: MenuItem = SeparatorMenuItem()
    val renameItem: MenuItem = MenuItem(resource.getString("crazynote.control.menuitem.rename"))

    val colorMenu: Menu = Menu(resource.getString("crazynote.control.menuitem.color"))
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
        getItems().addAll(newItem, deleteItem, hideItem, separator, renameItem, colorMenu)
    }
}
