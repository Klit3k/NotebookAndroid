package pl.edu.wat.notebookv3.viewmodel;

import android.view.View;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.Navigation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.DocumentSnapshot;
import lombok.Getter;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.repository.*;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import static android.app.PendingIntent.getActivity;

public class DashboardViewModel extends ViewModel {
    @Getter
    MutableLiveData<List<Note>> noteListMutableLiveData;
    private FirebaseUserRepository firebaseUserRepository;
    private FirebaseFolderRepository firebaseFolderRepository;
    private NoteRepos noteRepos;
    public DashboardViewModel() {
        firebaseUserRepository = new FirebaseUserRepository();
        firebaseFolderRepository = new FirebaseFolderRepository();
        noteRepos = new NoteRepos();
    }
    public boolean isLogged() {
        return firebaseUserRepository.get() != null;
    }

    private MutableLiveData<Note> lastlyDeletedNote;

    public MutableLiveData<List<Note>> getListNote() {
        return noteRepos.getList(DashboardFragment.getCurrentFolder());
    }

    public void moveToTrash(String noteId) {

        noteRepos.moveToTrash(noteId, DashboardFragment.getCurrentFolder());
    }
    public void transferNote(String noteId, String folder, String destinationFolder) {
        noteRepos.moveToAnotherFolder(noteId, folder, destinationFolder);
    }
    public MutableLiveData<List<Folder>> getListFolder() {
        return firebaseFolderRepository.getList(new FolderRepository.FolderListResultListener() {
            @Override
            public void onResult(List<Folder> note) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    public void removeNote(String uuid, String currentFolder) {
        noteRepos.remove(uuid, currentFolder);
    }
    public Task<DocumentSnapshot> existsFolder(String folderName) {
        return firebaseFolderRepository.getById(folderName);
    }
    public void addFolder(String folderName) {
            firebaseFolderRepository.create(
                    Folder.builder()
                            .name(folderName)
                            .notes(new ArrayList<>())
                            .build()
            );
    }
    public void logout(View view) {
        firebaseUserRepository.logout();
        Navigation.findNavController(view)
                .navigate(R.id.action_dashboardFragment_to_loginFragment);
    }
}

