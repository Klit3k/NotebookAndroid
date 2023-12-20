package pl.edu.wat.notebookv3.repository;

import androidx.lifecycle.MutableLiveData;
import pl.edu.wat.notebookv3.model.Note;

import java.util.List;

public interface NoteRepository {
    void get(String id, NoteResultListener noteResultListener);
    MutableLiveData<List<Note>> getList(String folder, NoteListResultListener noteListResultListener);
    void remove(String id);
    void create(String folderName, Note note);
    void update(String id, Note note);
    interface NoteResultListener {
        void onNoteResult(Note note);
        void onNoteError(Exception e);
    }

    interface NoteListResultListener {
        void onNoteResult(List<Note> note);
        void onNoteError(Exception e);
    }
}
