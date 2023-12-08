package pl.edu.wat.notebookv3.repository;

import androidx.lifecycle.MutableLiveData;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;

import java.util.List;

public interface FolderRepository {
    MutableLiveData<List<Folder>> getList(FolderRepository.FolderListResultListener folderListResultListener);

    interface FolderListResultListener {
        void onResult(List<Folder> note);
        void onError(Exception e);
    }
}
