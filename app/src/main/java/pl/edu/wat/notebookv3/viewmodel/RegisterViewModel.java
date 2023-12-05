package pl.edu.wat.notebookv3.viewmodel;

import android.app.ProgressDialog;
import android.view.View;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;

public class RegisterViewModel extends ViewModel  {
    FirebaseUserRepository firebaseUserRepository;
    ProgressDialog progressDialog;
    public RegisterViewModel() {
        firebaseUserRepository = new FirebaseUserRepository();
    }

    public Task<AuthResult> register(String email, String password) {
        return firebaseUserRepository.register(email, password);
    }

}
