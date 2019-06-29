package crazynote;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class RichTextArea extends Region {
    private WebView browser;
    private WebEngine engine;

    public RichTextArea() {
        browser = new WebView();
        browser.prefWidthProperty().bind(widthProperty());
        browser.prefHeightProperty().bind(heightProperty());

        engine = browser.getEngine();
        engine.load(getClass().getResource("/richTextArea/richTextArea.html").toExternalForm());
        engine.setJavaScriptEnabled(true);

        setPrefSize(300, 300);
        getChildren().add(browser);
    }

    public String getContents() {
        String contentsWithoutEscaped = (String)engine.executeScript("getContents()");
        String contentsWithEscaped = contentsWithoutEscaped.replace("\\", "\\\\");
        return contentsWithEscaped;
    }

    public void setContents(String contents) {
        engine.executeScript("setContents('" + contents + "')");
    }

    public void format(String name, Object value) {
        JSObject quill = (JSObject)engine.executeScript("getQuill()");
        quill.call("format", name, value);
    }

    public void setBgColor(String color) {
        engine.executeScript("document.body.style.backgroundColor = '" + color + "'");
    }
}
