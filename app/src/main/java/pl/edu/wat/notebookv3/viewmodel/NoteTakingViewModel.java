package pl.edu.wat.notebookv3.viewmodel;

import android.net.Uri;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import lombok.Getter;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.model.Reminder;
import pl.edu.wat.notebookv3.repository.*;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteTakingViewModel extends ViewModel {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy, HH:mm:ss");
    @Getter
    FirebaseNoteRepository firebaseNoteRepository;
    private FirebaseReminderRepository firebaseReminderRepository;

    NoteRepos noteRepos;
    public NoteTakingViewModel() {
        firebaseNoteRepository = new FirebaseNoteRepository();
        firebaseReminderRepository = new FirebaseReminderRepository();
        noteRepos = new NoteRepos();
    }

    public void deleteNote(String uid) {
        firebaseNoteRepository.remove(uid);
    }

    public MutableLiveData<List<String>> getImages(String tag, String currentFolder) {
        return noteRepos.getImages(tag, currentFolder);
    }
    public void createNote(String title, String body) {
        String time = dtf.format(LocalDateTime.now());
        if(title.isEmpty()) title = time;
        noteRepos.create(
                Note.builder()
                        .uuid(UUID.randomUUID().toString())
                        .title(title)
                        .body(body)
                        .updateTime(time)
                        .imageUrl(new ArrayList<>())
                        .build()
                , DashboardFragment.getCurrentFolder()
        );
    }
    public void createNote(String uuid, String title, String body) {
        String time = dtf.format(LocalDateTime.now());
        if(title.trim().isEmpty()) title = time;
        noteRepos.create(
                Note.builder()
                        .uuid(uuid)
                        .title(title)
                        .body(body)
                        .updateTime(time)
                        .imageUrl(new ArrayList<>())
                        .build()
                , DashboardFragment.getCurrentFolder()
        );
    }
    public void updateNote(String uid, String title, String body, List<String> imageUrls) {
        noteRepos.update(
                Note.builder()
                        .title(title)
                        .body(body)
                        .uuid(uid)
                        .imageUrl(imageUrls)
                        .updateTime(dtf.format(LocalDateTime.now()))
                        .build(),
                DashboardFragment.getCurrentFolder());
    }

    public void createReminder(Reminder reminder) {
        firebaseReminderRepository.create(reminder);
    }

    public void share() {
//        ShareOptionsFragment shareOptionsFragment = ShareOptionsFragment.newInstance("test", "test2");
//        shareOptionsFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "shareOption");
    }


    public Task<Uri> uploadImage(String noteid, Uri data) {
        FirebaseStorageImageRepository firebaseStorageImageRepository = new FirebaseStorageImageRepository();
        return firebaseStorageImageRepository.uploadImage(noteid, data);
    }

    public void updateImage(String tag, String title, String body, List<String> imageUrls) {
        noteRepos.updateImage(tag, title, body, imageUrls);
    }
}
