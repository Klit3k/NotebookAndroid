package pl.edu.wat.notebookv3.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.model.TimeLog;
import pl.edu.wat.notebookv3.model.User;

import java.util.ArrayList;
import java.util.List;

public class FirebaseNoteRepository implements NoteRepository {
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseUserRepository firebaseUserRepository;
    private final String USERS_PATH = "Users";
    private final String NOTES_PATH = "Notes";
    private final String TIME_PATH = "Time";

    public FirebaseNoteRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUserRepository = new FirebaseUserRepository();
    }


    @Override
    public void get(String id, NoteResultListener noteResultListener) {
        this.firebaseFirestore
                .collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(NOTES_PATH)
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
       this.firebaseFirestore.collection(USERS_PATH)
               .document(firebaseUserRepository.get().getUid())
               .collection(NOTES_PATH)
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
        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(NOTES_PATH)
                .document(id)
                .delete();
    }

    @Override
    public void create(Note note) {
        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(NOTES_PATH)
                .document(note.getUuid())
                .set(note);
    }

    @Override
    public void update(String id, Note note) {
        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(NOTES_PATH)
                .document(id)
                .set(note);
    }

    public MutableLiveData<List<String>> getUpdatesTime() {
        MutableLiveData<List<String>> timeListMutableLiveData = new MutableLiveData<>();
        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(NOTES_PATH)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    List<String> timeList = new ArrayList<>();

                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot snapshot:
                             queryDocumentSnapshots.getDocuments()) {
                            timeList.add(snapshot.toObject(Note.class).getUpdateTime());
                        }
                        timeListMutableLiveData.postValue(timeList);
                    }
                });
        return timeListMutableLiveData;
    }
}
