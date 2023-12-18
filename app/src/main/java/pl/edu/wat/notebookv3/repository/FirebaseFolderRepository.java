package pl.edu.wat.notebookv3.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;

import java.util.ArrayList;
import java.util.List;

public class FirebaseFolderRepository implements FolderRepository {
    private static final String FOLDER_PATH = "Folders";
    private static final String MAIN_FOLDER = "Main";
    private final String USERS_PATH = "Users";
    private final String NOTES_PATH = "Notes";
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseUserRepository firebaseUserRepository;
    private final FirebaseNoteRepository firebaseNoteRepository;

    public FirebaseFolderRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUserRepository = new FirebaseUserRepository();
        this.firebaseNoteRepository = new FirebaseNoteRepository();
    }

    public void moveNoteToFolder(String noteId, String folderId) {
        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull @NotNull Transaction transaction) throws FirebaseFirestoreException {

                firebaseNoteRepository.get(noteId, new NoteRepository.NoteResultListener() {
                    @Override
                    public void onNoteResult(Note note) {
                        firebaseNoteRepository.remove(noteId);
                        firebaseNoteRepository.create(folderId, note);
                        Log.d("TEST", "Przeniosłem " + noteId + " do folderu " + folderId);

                    }

                    @Override
                    public void onNoteError(Exception e) {

                    }
                });
                return null;
            }
        });

    }

    @Override
    public MutableLiveData<List<Folder>> getList(FolderListResultListener folderListResultListener) {
        MutableLiveData<List<Folder>> folderListMutableLiveData = new MutableLiveData<>();
        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(FOLDER_PATH)
                .addSnapshotListener((value, error) -> {
                    List<Folder> folderList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc != null) {
//                            Folder folder = doc.toObject(Folder.class);
                            //doc.getReference().collection("Notes")
                            Folder folder = Folder.builder()
                                    .id(doc.getId())
                                    .name(doc.getId())
                                    .notes(new ArrayList<>())
                                    .build();

                            doc.getReference().collection("Notes")
                                    .addSnapshotListener((v, e) -> {
                                        for (QueryDocumentSnapshot d : v) {
                                            folder.getNotes().add(d.toObject(Note.class));
                                        }
                                    });
                            folderList.add(folder);
                        }
                    }
                    folderListMutableLiveData.postValue(folderList);
                });
        return folderListMutableLiveData;
    }

    public Task<QuerySnapshot> getNoteListInFolder(String folderId) {

        return this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(FOLDER_PATH)
                .document(folderId)
                .collection(NOTES_PATH)
                .get();

    }
}

