package crazynote.util;

import crazynote.control.NoteData;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;
import javafx.stage.Window;

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

    public static void saveNoteData(NoteData noteData) {
        Path filePath = getNoteFilePath(noteData);

        try(ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(noteData);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void deleteNoteData(NoteData noteData) {
        try {
            Path filePath = getNoteFilePath(noteData);
            Files.deleteIfExists(filePath);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static List<NoteData> getNoteDatas() {
        List<NoteData> noteDatas = new ArrayList<>();

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(SAVE_PATH, "Note*")) {
            noteDatas = asStream(stream.iterator()).map(path -> {
                NoteData noteData = null;

                try(ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
                    noteData = (NoteData)in.readObject();
                } catch(IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
                return noteData;

            }).collect(Collectors.toList());
        } catch(IOException e){
            e.printStackTrace();
        }

        return noteDatas;
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

    private static Path getNoteFilePath(NoteData noteData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmssSSS");
        String fileName = "Note" + noteData.getTimestamp().format(formatter);
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
