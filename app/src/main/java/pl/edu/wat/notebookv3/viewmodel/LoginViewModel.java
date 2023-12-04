package pl.edu.wat.notebookv3.viewmodel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;


import java.util.Arrays;
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

    public void loginWithFb(CallbackManager callbackManager, View v, Activity activity, Class direction) {

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setTitle("Loading ...");
        progressDialog.show();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseUserRepository.loginWithFb(loginResult)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d("TEST", "Logged successfully with fb");
                                //Navigate to Dashboard TODO
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Log.d("TEST", "Cannot login with fb credentials");
                                progressDialog.dismiss();
                            }
                        });
                Log.d("TEST", "Logged passed with fb");
            }

            @Override
            public void onCancel() {
                Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TEST", "Something went wrong FB");
                progressDialog.dismiss();
            }
        });
    }

    public boolean isLogged(){
        return firebaseUserRepository.get() != null;
    }

}
