package pl.edu.wat.notebookv3.viewmodel;

import android.view.View;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.repository.FirebaseNoteRepository;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;

import java.util.List;

public class AccountViewModel extends ViewModel {
    FirebaseUserRepository firebaseUserRepository;
    FirebaseNoteRepository firebaseNoteRepository;
    public AccountViewModel() {
        firebaseUserRepository = new FirebaseUserRepository();
        firebaseNoteRepository = new FirebaseNoteRepository();
    }

    public void logout(View view) {
        firebaseUserRepository.logout();
        Navigation.findNavController(view)
                .navigate(R.id.action_accountFragment_to_loginFragment);
    }
    public MutableLiveData<List<String>> getTimes() {
        return firebaseNoteRepository.getUpdatesTime();
    }
}