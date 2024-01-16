package pl.edu.wat.notebookv3.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import pl.edu.wat.notebookv3.R;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class RecoveryFragment extends Fragment {


    public RecoveryFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recovery, container, false);
        Button recoveryButton = view.findViewById(R.id.recovery_button);
        TextInputEditText emailText =  view.findViewById(R.id.email_recovery_input);

        recoveryButton.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = Objects.requireNonNull(emailText.getText()).toString();
            if(!emailAddress.isEmpty())
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Snackbar.make(view, getResources().getString(R.string.check_your_email), Snackbar.LENGTH_SHORT).show();
                                Navigation.findNavController(view).navigate(R.id.action_recoveryFragment_to_loginFragment);
                            }
                            else {
                                Snackbar.make(view,getResources().getString(R.string.check_your_email), Snackbar.LENGTH_SHORT).show();

                            }
                        });
            else
             Snackbar.make(view,getResources().getString(R.string.enter_your_email), Snackbar.LENGTH_SHORT).show();
        });
        return view;
    }
}