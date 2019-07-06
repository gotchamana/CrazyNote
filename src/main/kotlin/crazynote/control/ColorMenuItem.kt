package crazynote.control

import crazynote.ColorTheme;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

class ColorMenuItem(val colorTheme: ColorTheme): MenuItem(colorTheme.toString()) {
    
    init {
        setGraphic(Rectangle(48.0, 16.0, Color.web(colorTheme.code1)))
    }
}

