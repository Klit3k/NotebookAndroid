package pl.edu.wat.notebookv3.repository;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import pl.edu.wat.notebookv3.model.ActivityLog;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.model.OperationType;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteRepos {
    private static final String LOG_PATH = "Logs";

    public static final String TRASH_PATH = ".Trash";
    public static final String STARRED_PATH = ".Starred";
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseUserRepository firebaseUserRepository;
    private static final String FOLDER_PATH = "Folders";
    private static final String MAIN_FOLDER = "Main";
    private final String USERS_PATH = "Users";
    private final String NOTES_PATH = "Notes";
    private final String TIME_PATH = "Time";
    private FirebaseFolderRepository firebaseFolderRepository;
    private final String MAIN_PATH = DashboardFragment.MAIN_FOLDER;

    public NoteRepos() {
        this.firebaseFolderRepository = new FirebaseFolderRepository();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUserRepository = new FirebaseUserRepository();
    }

    public void create(Note note, String folderName) {
        this.firebaseFolderRepository
                .getById(folderName)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Folder folder = documentSnapshot.toObject(Folder.class);
                            folder.getNotes().add(note);
                            firebaseFolderRepository
                                    .update(folderName, folder);
                        } else {
                            if (folderName.equals(MAIN_PATH)) {
                                Folder folder = Folder
                                        .builder()
                                        .name(MAIN_PATH)
                                        .notes(Arrays.asList(note))
                                        .build();

                                firebaseFolderRepository
                                        .create(folder);
                            }
                        }
                    }
                });
        logActivity(OperationType.CREATE);

    }

    public void moveToTrash(String noteId, String folderName) {
        this.firebaseFolderRepository
                .getById(folderName)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (!documentSnapshot.exists()) {
                            Folder folder = Folder.builder()
                                    .notes(new ArrayList<>())
                                    .name(TRASH_PATH)
                                    .build();

                            firebaseFolderRepository.create(folder);
                            firebaseFolderRepository
                                    .update(folderName, folder);
                        }
                        moveToAnotherFolder(noteId, folderName, TRASH_PATH);

                    }
                });
    }
    public void update(Note note, String folderName) {
        this.firebaseFolderRepository
                .getById(folderName)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Folder folder = documentSnapshot.toObject(Folder.class);
                            Note noteX = folder.getNotes().stream()
                                    .filter(note1 -> note1.getUuid().equals(note.getUuid()))
                                    .findFirst()
                                    .get();

                            folder.getNotes().remove(noteX);
                            folder.getNotes().add(note);


                            firebaseFolderRepository
                                    .update(folderName, folder);
                        } else {
                            if (folderName.equals(MAIN_PATH)) {
                                Folder folder = Folder
                                        .builder()
                                        .name(MAIN_PATH)
                                        .notes(Arrays.asList(note))
                                        .build();

                                firebaseFolderRepository
                                        .create(folder);
                            }
                        }
                    }
                });
        logActivity(OperationType.UPDATE);

    }
    public  void remove(String uuid, String folderName) {

        this.firebaseFolderRepository
                .getById(folderName)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Folder folder = documentSnapshot.toObject(Folder.class);

                            Note noteX = folder.getNotes().stream()
                                    .filter(note1 -> note1.getUuid().equals(uuid))
                                    .findFirst()
                                    .get();

                            folder.getNotes().remove(noteX);

                            firebaseFolderRepository
                                    .update(folderName, folder);
                        }
                    }
                });
        logActivity(OperationType.REMOVE);

    }

    public MutableLiveData<List<Note>> getList(String folder) {
        MutableLiveData<List<Note>> noteListMutableLiveData = new MutableLiveData<>();
        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(FOLDER_PATH)
                .document(folder)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<Note> noteList = new ArrayList<>();
                        if (value.toObject(Folder.class) != null && value.toObject(Folder.class).getNotes() != null) {

                            for (Note note : value.toObject(Folder.class).getNotes()) {
                                noteList.add(note);
                            }
                            noteListMutableLiveData.postValue(noteList);
                        }
                    }
                });
        return noteListMutableLiveData;
    }

    public void moveToAnotherFolder(String noteId, String currentFolder, String destinationFolder) {
        this.firebaseFolderRepository
                .getById(currentFolder)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            Folder folder = documentSnapshot.toObject(Folder.class);
                            Note noteX = folder.getNotes().stream()
                                    .filter(note1 -> note1.getUuid().equals(noteId))
                                    .findFirst()
                                    .get();

                            remove(noteId, currentFolder);
                            create(noteX, destinationFolder);

                        }
                    }
                });
    }
    public void logActivity(OperationType operationType) {

        this.firebaseFirestore
                .collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(LOG_PATH)
                .document()
                .set(
                        ActivityLog.builder()
                                .operationType(operationType)
                                .time(LocalDateTime.now().toString())
                                .build()
                );
    }
    public MutableLiveData<List<ActivityLog>> getLogs() {

        MutableLiveData<List<ActivityLog>> logs = new MutableLiveData<>();
        this.firebaseFirestore
                .collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(LOG_PATH)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<ActivityLog> logList = new ArrayList<>();
                        for (DocumentSnapshot snapshot:
                                queryDocumentSnapshots.getDocuments()) {

                            logList.add(snapshot.toObject(ActivityLog.class));
                        }
                        logs.postValue(logList);
                    }
                });

        return logs;
    }
    /*
    TODO s
     * logi do statystyk
     * udostepnianie
     * starred
     * poprawić wizualke przy przenoszeniu
    */
}
