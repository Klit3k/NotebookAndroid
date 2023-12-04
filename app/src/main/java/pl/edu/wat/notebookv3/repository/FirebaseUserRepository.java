package pl.edu.wat.notebookv3.repository;

import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;

public class FirebaseUserRepository {
    private FirebaseAuth mAuth;

    public FirebaseUserRepository() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser get() {
        return this.mAuth.getCurrentUser();
    }

    public void logout() {
        this.mAuth.signOut();
    }

    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }
    public Task<AuthResult> loginWithFb(LoginResult loginResult) {
        AuthCredential authCredential =  FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
        return mAuth.signInWithCredential(authCredential);
    }
}
