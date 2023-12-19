package pl.edu.wat.notebookv3.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;

import java.util.List;

public interface FolderRepository {
    MutableLiveData<List<Folder>> getList(FolderRepository.FolderListResultListener folderListResultListener);

    void create(Folder folder) throws FirebaseException;
    Task<DocumentSnapshot> getById(String id);
    void remove(String id);
    void update(String id, Folder folder);
    void addNoteToFolder(Note note, String folderId);
    void removeNoteFromFolder(String noteId, String folderId);
    Task<QuerySnapshot> getNoteListInFolder(String folderId);

//    void moveNoteToFolder(String noteId, String folderId);
    interface FolderListResultListener {
        void onResult(List<Folder> note);
        void onError(Exception e);
    }
}
