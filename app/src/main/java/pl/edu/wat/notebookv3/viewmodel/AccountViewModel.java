package pl.edu.wat.notebookv3.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import pl.edu.wat.notebookv3.model.ActivityLog;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;
import pl.edu.wat.notebookv3.repository.NoteRepos;

import java.util.List;

public class AccountViewModel extends ViewModel {
    FirebaseUserRepository firebaseUserRepository;
    NoteRepos noteRepos;
    public AccountViewModel() {
        firebaseUserRepository = new FirebaseUserRepository();
        noteRepos = new NoteRepos();
    }



    public String getEmail() {
        return firebaseUserRepository.get().getEmail();
    }
    public MutableLiveData<List<ActivityLog>> getLogs() {
        return noteRepos.getLogs();
    }

    public void removeAccount() {
         firebaseUserRepository.removeAccount();
    }
}
