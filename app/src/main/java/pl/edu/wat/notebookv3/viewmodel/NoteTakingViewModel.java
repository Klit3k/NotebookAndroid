package pl.edu.wat.notebookv3.viewmodel;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import lombok.Getter;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.repository.FirebaseNoteRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class NoteTakingViewModel extends ViewModel {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy, HH:mm:ss");
    @Getter
    FirebaseNoteRepository firebaseNoteRepository;

    public NoteTakingViewModel() {
        firebaseNoteRepository = new FirebaseNoteRepository();
    }

    public void deleteNote(String uid) {
        firebaseNoteRepository.remove(uid);
    }

    public void createNote(String title, String body) {
        String time = dtf.format(LocalDateTime.now());
        if(title.isEmpty()) title = time;
        firebaseNoteRepository.create(
                Note.builder()
                        .uuid(UUID.randomUUID().toString())
                        .title(title)
                        .body(body)
                        .updateTime(time)
                        .build()
        );
    }

    public void updateNote(String uid, String title, String body) {
        firebaseNoteRepository.update(uid,
                Note.builder()
                        .title(title)
                        .body(body)
                        .uuid(uid)
                        .updateTime(dtf.format(LocalDateTime.now()))
                        .build());
    }

    public void share() {
//        ShareOptionsFragment shareOptionsFragment = ShareOptionsFragment.newInstance("test", "test2");
//        shareOptionsFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "shareOption");
    }
}
