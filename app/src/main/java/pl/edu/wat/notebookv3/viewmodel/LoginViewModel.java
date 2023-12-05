package pl.edu.wat.notebookv3.viewmodel;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;


import java.util.Objects;

public class LoginViewModel extends ViewModel {
    FirebaseUserRepository firebaseUserRepository;
    ProgressDialog progressDialog;
    public LoginViewModel() {
        firebaseUserRepository = new FirebaseUserRepository();
    }

    public Task<AuthResult> login(EditText email, EditText password) {
        String nameStr = Objects.requireNonNull(email.getText()).toString();
        String passwordStr = Objects.requireNonNull(password.getText()).toString();
        return firebaseUserRepository.login(nameStr, passwordStr);
    }

    public Task<AuthResult> loginWithFb(LoginResult loginResult) {
        return firebaseUserRepository.loginWithFb(loginResult);
    }

    public boolean isLogged(){
        return firebaseUserRepository.get() != null;
    }

}
