package pl.edu.wat.notebookv3.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.viewmodel.RegisterViewModel;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

/** @noinspection ALL*/
public class RegisterFragment extends Fragment {
    private ProgressDialog progressDialog;
    private RegisterViewModel viewModel;
    private CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        view.findViewById(R.id.login_now_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_registerFragment_to_loginFragment);
                    }
                });

        TextInputEditText email = view.findViewById(R.id.email_input);
        TextInputEditText password = view.findViewById(R.id.password_input);
        TextInputEditText passwordRepeat = view.findViewById(R.id.password_repeat_input);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                viewModel.loginWithFb(loginResult)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.d("TEST", "Register successfully with fb");
                                viewModel.createFolders();

                                Navigation.findNavController(view)
                                        .navigate(R.id.action_registerFragment_to_loginFragment);
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
        view.findViewById(R.id.continue_with_facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setTitle("Ładowanie ...");
                progressDialog.show();

                LoginManager.getInstance()
                        .logInWithReadPermissions(getActivity(), callbackManager,Arrays.asList("public_profile", "email"));


            }
        });
        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || passwordRepeat.getText().toString().isEmpty()) {
                    Snackbar.make(v, getResources().getString(R.string.enter_your_email_or_password), Snackbar.LENGTH_SHORT).show();
                } else if (!passwordRepeat.getText().toString().equals(password.getText().toString())) {
                    Snackbar.make(v, getResources().getString(R.string.passwords_are_not_matching), Snackbar.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle("Ładowanie ...");
                    progressDialog.show();
                    viewModel.register(email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    viewModel.createFolders();
                                    Navigation.findNavController(v)
                                            .navigate(R.id.action_registerFragment_to_loginFragment);
                                }
                            });
                    progressDialog.dismiss();
                }
            }
        });

        return view;
    }

}