package crazynote;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Base64;

public class RichTextArea extends Region {
    private WebView browser;
    private WebEngine engine;

    public RichTextArea() {
        browser = new WebView();
        browser.setContextMenuEnabled(false);
        browser.prefWidthProperty().bind(widthProperty());
        browser.prefHeightProperty().bind(heightProperty());

        engine = browser.getEngine();
        engine.load(getClass().getResource("/richTextArea/richTextArea.html").toExternalForm());
        engine.setJavaScriptEnabled(true);

        JSObject window = (JSObject)engine.executeScript("window");
        window.setMember("textArea", this);

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

    public String uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a image...");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image file", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));

        Path imagePath = fileChooser.showOpenDialog(null).toPath();
        String imageDataURI = null;

        try(BufferedInputStream in = new BufferedInputStream(Files.newInputStream(imagePath))) {
            byte[] byteArray = new byte[(int)Files.size(imagePath)];
            in.read(byteArray, 0, byteArray.length);

            String imageMimeType = Files.probeContentType(imagePath);
            imageDataURI = "data:" + imageMimeType + ";base64," + Base64.getEncoder().encodeToString(byteArray);

        } catch(IOException e){
            e.printStackTrace();
        }

        return imageDataURI;
    }

    public void log(Object obj) {
        System.out.println(obj);
    }
}
