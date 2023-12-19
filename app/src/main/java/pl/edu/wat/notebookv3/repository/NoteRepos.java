package pl.edu.wat.notebookv3.repository;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteRepos {

    private FirebaseFolderRepository firebaseFolderRepository;
    private final String MAIN_PATH = DashboardFragment.MAIN_FOLDER;

    public NoteRepos() {
        this.firebaseFolderRepository = new FirebaseFolderRepository();
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
    }

    public MutableLiveData<List<Note>> getList(String folder) {
        Log.d("TEST:getList", "currentFolder: " + DashboardFragment.getCurrentFolder() + " passed folder: " + folder);
        MutableLiveData<List<Note>> noteListMutableLiveData = new MutableLiveData<>();
        this.firebaseFolderRepository
                .getById(folder)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<Note> noteList = new ArrayList<>();
                        if (documentSnapshot.toObject(Folder.class) != null && !documentSnapshot.toObject(Folder.class).getNotes().isEmpty()) {
                            for (Note note :
                                    documentSnapshot.toObject(Folder.class).getNotes()) {
                                noteList.add(note);
                            }
                            noteListMutableLiveData.postValue(noteList);
                        }
                    }
                });
        return noteListMutableLiveData;
    }

    /*
    TODO s
     * usuwanie
     * odzyskiwanie
     * wyswietlanie (nieaktualizuje sie)
     * logi do statystyk
     * udostepnianie
     * starred
     * przenoszenie notatek pomiedzy folderami
    */
}
