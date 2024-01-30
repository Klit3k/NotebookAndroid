package pl.edu.wat.notebookv3.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.viewmodel.LoginViewModel;

import java.util.Arrays;

import static android.content.ContentValues.TAG;


public class LoginFragment extends Fragment {
    private static final int FACEBOOK_LOG_IN_REQUEST_CODE = 64206;
    private EditText email;
    private EditText password;
    private ProgressDialog progressDialog;

    private LoginViewModel loginViewModel;
    private CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        FacebookSdk.sdkInitialize(getActivity());
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        final NavController navController = NavHostFragment.findNavController(this);
        if (loginViewModel.isLogged())
            navController.navigate(R.id.action_loginFragment_to_dashboardFragment);

        email = view.findViewById(R.id.email_input);
        password = view.findViewById(R.id.password_input);
        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    Snackbar.make(v, getResources().getString(R.string.enter_your_email_or_password), Snackbar.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle("Ładowanie ...");
                    progressDialog.show();
                    loginViewModel.login(email, password)
                            .addOnSuccessListener(authResult -> {
                                Snackbar.make(view, "Zalogowano", Snackbar.LENGTH_SHORT).show();
                                Navigation.findNavController(view)
                                        .navigate(R.id.action_loginFragment_to_dashboardFragment);
                                progressDialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(view, getResources().getString(R.string.authentication_failed), Snackbar.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            });
                }
            }
        });

        view.findViewById(R.id.register_now_button)
                .setOnClickListener(v ->
                        Navigation.findNavController(view)
                                .navigate(R.id.action_loginFragment_to_registerFragment)
                );

        view.findViewById(R.id.reset_password_button)
                .setOnClickListener(v ->
                        Navigation.findNavController(view)
                                .navigate(R.id.action_loginFragment_to_recoveryFragment)
                );
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginViewModel.loginWithFb(loginResult)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d("TEST", "Logged successfully with fb");
                                Navigation.findNavController(view)
                                        .navigate(R.id.action_loginFragment_to_dashboardFragment);
                                progressDialog.dismiss();
                            }
                        });

            }

            @Override
            public void onCancel() {
                Log.d("TEST", "facebook:onCancel");
                progressDialog.dismiss();

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TEST", "facebook:onError", error);

                progressDialog.dismiss();

            }
        });

        view.findViewById(R.id.continue_facebook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setTitle("Ładowanie ...");
                progressDialog.show();

                LoginManager.getInstance()
                        .logInWithReadPermissions(getActivity(), callbackManager, Arrays.asList("public_profile", "email"));


            }
        });
        return view;
    }



}
