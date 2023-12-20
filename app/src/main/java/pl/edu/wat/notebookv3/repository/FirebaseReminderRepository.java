package pl.edu.wat.notebookv3.repository;

import com.google.firebase.firestore.FirebaseFirestore;
import pl.edu.wat.notebookv3.model.Reminder;

import java.time.LocalDateTime;
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


}
