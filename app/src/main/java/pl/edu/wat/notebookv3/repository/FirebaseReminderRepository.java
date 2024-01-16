package pl.edu.wat.notebookv3.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import pl.edu.wat.notebookv3.model.ActivityLog;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.model.Reminder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FirebaseReminderRepository {

    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseUserRepository firebaseUserRepository;
    private final String USERS_PATH = "Users";
    private final String REMINDER_PATH = "Reminders";
    private final String TIME_PATH = "Time";
    private static final String LOG_PATH = "Logs";


    public FirebaseReminderRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUserRepository = new FirebaseUserRepository();
    }

    public void create(Reminder reminder) {

        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(REMINDER_PATH)
                .document(reminder.getId())
                .set(reminder);
    }


    public MutableLiveData<List<Reminder>> getReminders() {

        MutableLiveData<List<Reminder>> getReminderList = new MutableLiveData<>();
        this.firebaseFirestore
                .collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(REMINDER_PATH)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        List<Reminder> reminderList = new ArrayList<>();
                        if (value.toObjects(Reminder.class) != null ) {
                            reminderList.addAll(value.toObjects(Reminder.class));
                            getReminderList.postValue(reminderList);
                        }
                    }
                });

        return getReminderList;
    }


    public void remove(String id) {
        firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(REMINDER_PATH)
                .document(id)
                .delete();
    }
}
