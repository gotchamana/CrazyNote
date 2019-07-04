package crazynote;

import java.io.*;
import java.nio.file.*;
import java.util.Base64;
import javafx.concurrent.Worker;
import javafx.scene.layout.Region;
import javafx.scene.web.*;
import javafx.stage.FileChooser;
import netscape.javascript.JSObject;

public class RichTextArea extends Region {
    private WebView browser;
    private WebEngine engine;

    public RichTextArea(String contents, ColorTheme colorTheme) {
        browser = new WebView();
        browser.setContextMenuEnabled(false);
        browser.prefWidthProperty().bind(widthProperty());
        browser.prefHeightProperty().bind(heightProperty());

        engine = browser.getEngine();
        engine.load(getClass().getResource("/web/richTextArea.html").toExternalForm());
        engine.setJavaScriptEnabled(true);
        engine.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                setContents(contents);
                setBgColor(colorTheme.getCode2());
            }
        });

        // Pass the textarea reference to JavaScript
        JSObject window = (JSObject)engine.executeScript("window");
        window.setMember("textArea", this);

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
        FileChooser fileChooser = createFileChooser();
        Path imagePath = fileChooser.showOpenDialog(null).toPath();
        String imageDataURI = null;

        try(BufferedInputStream in = new BufferedInputStream(Files.newInputStream(imagePath))) {
            byte[] byteArray = new byte[(int)Files.size(imagePath)];
            in.read(byteArray, 0, byteArray.length);

            // Encode the image to data URI
            String imageMimeType = Files.probeContentType(imagePath);
            imageDataURI = "data:" + imageMimeType + ";base64," + Base64.getEncoder().encodeToString(byteArray);

        } catch(IOException e){
            e.printStackTrace();
        }

        return imageDataURI;
    }

    private FileChooser createFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a image...");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Image file", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        return fileChooser;
    }

    public void log(Object obj) {
        System.out.println(obj);
    }
}
