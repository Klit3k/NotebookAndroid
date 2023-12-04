package pl.edu.wat.notebookv3.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import pl.edu.wat.notebookv3.model.Note;

import java.util.ArrayList;
import java.util.List;

public class FirebaseNoteRepository implements NoteRepository {
    private final FirebaseFirestore firebaseFirestore;
    private final String COLLECTION_PATH = "Note";

    public FirebaseNoteRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }


    @Override
    public void get(String id, NoteResultListener noteResultListener) {
        this.firebaseFirestore.collection(COLLECTION_PATH)
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    noteResultListener.onNoteResult(documentSnapshot.toObject(Note.class));
                })
                .addOnFailureListener(noteResultListener::onNoteError);
    }

    @Override
    public MutableLiveData<List<Note>> getList(NoteListResultListener noteListResultListener) {
        MutableLiveData<List<Note>> noteListMutableLiveData = new MutableLiveData<>();
        this.firebaseFirestore.collection(COLLECTION_PATH)
                .addSnapshotListener((value, error) -> {
                   List<Note> noteList = new ArrayList<>();
                   for (QueryDocumentSnapshot doc : value) {
                       if (doc != null) {
                           Note note = doc.toObject(Note.class);
                           noteList.add(note);
                       }
                   }
                    noteListMutableLiveData.postValue(noteList);
                });
        return noteListMutableLiveData;
    }

    @Override
    public void remove(String id) {
        this.firebaseFirestore.collection(COLLECTION_PATH)
                .document(id)
                .delete();
    }

    @Override
    public void create(Note note) {
        this.firebaseFirestore.collection(COLLECTION_PATH)
                .document(note.getUuid())
                .set(note);
    }

    @Override
    public void update(String id, Note note) {
        this.firebaseFirestore.collection(COLLECTION_PATH)
                .document(id)
                .set(note);
    }
}
