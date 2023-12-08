package pl.edu.wat.notebookv3.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Predicate;

import lombok.Getter;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.repository.*;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import static android.app.PendingIntent.getActivity;

public class DashboardViewModel extends ViewModel {
    @Getter
    MutableLiveData<List<Note>> noteListMutableLiveData;
    private FirebaseNoteRepository firebaseNoteRepository;
    private FirebaseUserRepository firebaseUserRepository;
    private FirebaseFolderRepository firebaseFolderRepository;

    public DashboardViewModel() {
        firebaseNoteRepository = new FirebaseNoteRepository();
        firebaseUserRepository = new FirebaseUserRepository();
        firebaseFolderRepository = new FirebaseFolderRepository();
        noteListMutableLiveData = getListNote();
    }
    public boolean isLogged() {
        return firebaseUserRepository.get() != null;
    }
    public void recoverNote(Note note) {
        firebaseNoteRepository.create(
                Note.builder()
                        .uuid(note.getUuid())
                        .title(note.getTitle())
                        .body(note.getBody())
                        .updateTime(note.getUpdateTime())
                        .build()
        );
    }
    private MutableLiveData<Note> lastlyDeletedNote;
    public MutableLiveData<Note> getNote(String uuid) {
        lastlyDeletedNote = new MutableLiveData<>();
        firebaseNoteRepository.get(uuid, new NoteRepository.NoteResultListener() {
            @Override
            public void onNoteResult(Note n) {
                lastlyDeletedNote.postValue(n);
            }

            @Override
            public void onNoteError(Exception e) {

            }
        });

        return lastlyDeletedNote;
    }
    public MutableLiveData<List<Note>> getListNote() {
        return firebaseNoteRepository.getList(new NoteRepository.NoteListResultListener() {
            @Override
            public void onNoteResult(List<Note> notes) {

            }

            @Override
            public void onNoteError(Exception e) {

            }
        });
    }
    public MutableLiveData<List<Folder>> getListFolder() {
        return firebaseFolderRepository.getList(new FolderRepository.FolderListResultListener() {
            @Override
            public void onResult(List<Folder> note) {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    public void removeNote(String uuid) {
        firebaseNoteRepository.get(uuid, new NoteRepository.NoteResultListener() {
            @Override
            public void onNoteResult(Note note) {

            }

            @Override
            public void onNoteError(Exception e) {

            }
        });
        firebaseNoteRepository.remove(uuid);
    }

}

