package pl.edu.wat.notebookv3.repository;

import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUserRepository {
    private FirebaseAuth mAuth;
    private final FirebaseFirestore firebaseFirestore;
    private final String COLLECTION_PATH = "Users";

    public FirebaseUserRepository() {
        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
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

    public Task<AuthResult> register(String email, String password) {
       return mAuth.createUserWithEmailAndPassword(email, password);
    }

}
