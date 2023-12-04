package pl.edu.wat.notebookv3.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.snackbar.Snackbar;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.viewmodel.LoginViewModel;


public class LoginFragment extends Fragment {
    private EditText email;
    private EditText password;
    private ProgressDialog progressDialog;

    private LoginViewModel loginViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
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
//                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.enter_your_email_or_password), Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle("Loading ...");
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
        return view;
    }
}