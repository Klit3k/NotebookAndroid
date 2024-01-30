package pl.edu.wat.notebookv3.viewmodel;

import android.app.ProgressDialog;
import android.view.View;
import androidx.lifecycle.ViewModel;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.repository.FirebaseFolderRepository;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;
import pl.edu.wat.notebookv3.repository.NoteRepos;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class RegisterViewModel extends ViewModel  {
    FirebaseUserRepository firebaseUserRepository;
    FirebaseFolderRepository firebaseFolderRepository;
    ProgressDialog progressDialog;
    public RegisterViewModel() {
        firebaseUserRepository = new FirebaseUserRepository();
        firebaseFolderRepository = new FirebaseFolderRepository();

    }
    public Task<AuthResult> register(String email, String password) {
        return firebaseUserRepository.register(email, password);
    }

    public void createFolders() {
        Folder folder = Folder
                .builder()
                .name(DashboardFragment.MAIN_FOLDER)
                .notes(new ArrayList<>())
                .build();
        Folder folderTrash = Folder
                .builder()
                .name(NoteRepos.TRASH_PATH)
                .notes(new ArrayList<>())
                .build();

        firebaseFolderRepository
                .create(folder);
        firebaseFolderRepository
                .create(folderTrash);
    }
    public Task<AuthResult> loginWithFb(LoginResult loginResult) {
        return firebaseUserRepository.loginWithFb(loginResult);
    }
}
