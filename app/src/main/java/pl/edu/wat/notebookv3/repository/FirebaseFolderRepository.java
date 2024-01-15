package pl.edu.wat.notebookv3.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.*;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class FirebaseFolderRepository implements FolderRepository {
    private static final String FOLDER_PATH = "Folders";
    private final String USERS_PATH = "Users";
    private final String NOTES_PATH = "Notes";
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseUserRepository firebaseUserRepository;

    public FirebaseFolderRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUserRepository = new FirebaseUserRepository();
    }

    @Override
    public void create(Folder folder) {

            this.firebaseFirestore.collection(USERS_PATH)
                    .document(firebaseUserRepository.get().getUid())
                    .collection(FOLDER_PATH)
                    .document(folder.getName())
                    .set(folder);
    }

    @Override
    public Task<DocumentSnapshot> getById(String id) {
        return this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(FOLDER_PATH)
                .document(id)
                .get();
    }

    @Override
    public void remove(String id) throws NoSuchElementException {
        getById(id)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        firebaseFirestore.collection(USERS_PATH)
                                .document(firebaseUserRepository.get().getUid())
                                .collection(FOLDER_PATH)
                                .document(id)
                                .delete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        throw new NoSuchElementException("Folder with entered id doesn't exists");
                    }
                });

    }

    @Override
    public void update(String id, Folder folder) {
        firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(FOLDER_PATH)
                .document(id)
                .set(folder);
    }

    @Override
    public void addNoteToFolder(Note note, String folderId) {
        getById(folderId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) throw new NoSuchElementException("Folder doesn't exists");

                        Folder folder = documentSnapshot.toObject(Folder.class);
                        if (folder.getNotes() == null)
                            folder.setNotes(new ArrayList<>());

                        folder.getNotes()
                                .add(note);

                        update(folder.getName(), folder);
                    }
                });
    }

    @Override
    public void removeNoteFromFolder(String noteId, String folderId) {
        getById(folderId)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) throw new NoSuchElementException("Folder doesn't exists");
                        Folder folder = documentSnapshot.toObject(Folder.class);
                        if (folder.getNotes() != null) {
                            folder.getNotes()
                                    .remove(folder);
                        }
                    }
                });
    }

//    public void moveNoteToFolder(String noteId, String folderId) {
//        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
//            @Nullable
//            @Override
//            public Void apply(@NonNull @NotNull Transaction transaction) throws FirebaseFirestoreException {
//
//                firebaseNoteRepository.get(noteId, new NoteRepository.NoteResultListener() {
//                    @Override
//                    public void onNoteResult(Note note) {
//                        firebaseNoteRepository.remove(noteId);
//                        firebaseNoteRepository.create(folderId, note);
//                        Log.d("TEST", "Przeniosłem " + noteId + " do folderu " + folderId);
//
//                    }
//
//                    @Override
//                    public void onNoteError(Exception e) {
//
//                    }
//                });
//                return null;
//            }
//        });
//    }

    public Task<QuerySnapshot> getNoteListInFolder(String folderId) {

        return this.firebaseFirestore
                .collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(FOLDER_PATH)
                .document(folderId)
                .collection(NOTES_PATH)
                .get();

    }
    //===========================================================================

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



//    public void getLastActivity() {
//        this.firebaseFirestore
//                .collection(USERS_PATH)
//                .document(firebaseUserRepository.get().getUid())
//                .collection(LOG_PATH)
//                .
//    }

}

