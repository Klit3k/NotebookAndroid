package pl.edu.wat.notebookv3.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import pl.edu.wat.notebookv3.model.Reminder;
import pl.edu.wat.notebookv3.repository.*;

import java.util.List;

public class ReminderViewModel extends ViewModel {
    private FirebaseReminderRepository firebaseReminderRepository;
    public ReminderViewModel() {
        firebaseReminderRepository = new FirebaseReminderRepository();
    }

    public MutableLiveData<List<Reminder>> getReminders() {
        return firebaseReminderRepository.getReminders();
    };

}
