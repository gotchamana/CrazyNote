package crazynote;

import crazynote.util.FileUtil;
import java.util.ResourceBundle;

public enum ColorTheme {

    YELLOW("#ffeb85", "#fff3ac", "/app/theme/yellow.css"),
    GREEN("#b1eca6", "#cbf1c4", "/app/theme/green.css"),
    PINK("#ffbbdd", "#ffcce5", "/app/theme/pink.css"),
    PURPLE("#dbb7ff", "#e7cfff", "/app/theme/purple.css"),
    BLUE("#b7dfff", "#cde9ff", "/app/theme/blue.css"),
    BEIGE("#e0e094", "#f5f5dc", "/app/theme/beige.css");

    private ResourceBundle resource = FileUtil.getResourceBundle();
    private String code1, code2, cssFilePath;

    private ColorTheme(String code1, String code2, String cssFilePath) {
        this.code1 = code1;
        this.code2 = code2;
        this.cssFilePath = cssFilePath;
    }

    public String getCode1() {
        return code1;
    }

    public String getCode2() {
        return code2;
    }

    public String getCssFilePath() {
        return cssFilePath;
    }

    @Override
    public String toString() {
        // return capitalize(super.toString().toLowerCase());
        return resource.getString("crazynote.control.menuitem." + super.toString().toLowerCase());
    }

    private String capitalize(String word) {
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
