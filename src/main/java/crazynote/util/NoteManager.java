package crazynote.util;

import crazynote.control.Note;
import java.util.*;
import java.util.stream.Collectors;
import javafx.stage.Window;

public class NoteManager {

    private static List<Note> notes;

    private NoteManager() {}

    public static List<Note> getNotes(Window owner) {
        if (notes == null) {
            notes = FileUtil.getNoteDatas()
                .stream()
                .map(data -> new Note(owner, data))
                .collect(Collectors.toList());
        }
        return notes;
    }

    public static Note getNewNote(Window owner) {
        Note note = new Note(owner);
        notes.add(note);
        return note;
    }

    public static void saveNote(Note note) {
        FileUtil.saveNoteData(note.getNoteData());
    }

    public static void deleteNote(Note note) {
        notes.remove(note);
        FileUtil.deleteNoteData(note.getNoteData());
    }
}
