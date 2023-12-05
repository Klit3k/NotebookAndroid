package pl.edu.wat.notebookv3.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.viewmodel.RegisterViewModel;

public class RegisterFragment extends Fragment {
    private ProgressDialog progressDialog;
    private RegisterViewModel viewModel;

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
        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || passwordRepeat.getText().toString().isEmpty()) {
                    Snackbar.make(v, getResources().getString(R.string.enter_your_email_or_password), Snackbar.LENGTH_SHORT).show();
                } else if (!passwordRepeat.getText().toString().equals(password.getText().toString())) {
                    Snackbar.make(v, getResources().getString(R.string.passwords_are_not_matching), Snackbar.LENGTH_SHORT).show();
                } else {
                    progressDialog = new ProgressDialog(v.getContext());
                    progressDialog.setTitle("Loading ...");
                    progressDialog.show();
                    viewModel.register(email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
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