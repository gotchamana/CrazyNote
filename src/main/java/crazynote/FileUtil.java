package crazynote;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;
import javafx.stage.Window;
import kotlin.Pair;

public class FileUtil {
    
    public final static Path SAVE_PATH;

    static {
        SAVE_PATH = getSaveFilePath("data");

        try {
            if (!Files.exists(SAVE_PATH)) {
                Files.createDirectory(SAVE_PATH);
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private FileUtil() {}

    public static void saveNote(Note note) {
        Path filePath = getNoteFilePath(note);

        try(ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(note.getTimestamp());
            out.writeObject(note.getTitle());
            out.writeObject(note.getContents());
            out.writeObject(note.getColorTheme().toString());
            out.writeBoolean(note.isVisible());
            out.writeDouble(note.getPosition().getFirst());
            out.writeDouble(note.getPosition().getSecond());
            out.writeDouble(note.getWidth());
            out.writeDouble(note.getHeight());
            
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void deleteNote(Note note) {
        try {
            Path filePath = getNoteFilePath(note);
            Files.deleteIfExists(filePath);

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static List<Note> getNotes(Window owner) {
        List<Note> notes = new ArrayList<>();

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(SAVE_PATH, "Note*")) {
            notes = asStream(stream.iterator()).map(path -> {
                Note note = null;

                try(ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
                    LocalDateTime timestamp = (LocalDateTime)in.readObject();
                    String title = (String)in.readObject();
                    String contents = (String)in.readObject();
                    ColorTheme colorTheme = ColorTheme.valueOf(((String)in.readObject()).toUpperCase());
                    boolean isVisible = in.readBoolean();
                    double x = in.readDouble();
                    double y = in.readDouble();
                    double width = in.readDouble();
                    double height = in.readDouble();

                    note = new Note(owner, timestamp, title, contents, colorTheme, isVisible, new Pair<Double, Double>(x, y), width, height);

                } catch(IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }

                return note;
            }).collect(Collectors.toList());

        } catch(IOException e){
            e.printStackTrace();
        }

        return notes;
    }

    private static Path getSaveFilePath(String dir) {
        Path savePath = Paths.get(System.getProperty("user.home"));

        try {
            URI uri = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path codeSource = Paths.get(uri);

            if (!Files.isDirectory(codeSource)) {
                codeSource = codeSource.getParent();
            }

            savePath = codeSource.resolve(dir);

        } catch(URISyntaxException e){
            e.printStackTrace();
        }

        return savePath;
    }

    private static Path getNoteFilePath(Note note) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmssSSS");
        String fileName = "Note" + note.getTimestamp().format(formatter);
        Path filePath = SAVE_PATH.resolve(fileName);

        return filePath;
    }

    private static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        return asStream(sourceIterator, false);
    }

    private static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {

        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
}
