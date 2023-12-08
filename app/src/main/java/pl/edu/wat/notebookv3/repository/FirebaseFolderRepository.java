package pl.edu.wat.notebookv3.repository;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.Note;

import java.util.ArrayList;
import java.util.List;

public class FirebaseFolderRepository implements FolderRepository{
    private static final String FOLDER_PATH = "Folders";
    private static final String MAIN_FOLDER = "Main";
    private final String USERS_PATH = "Users";
    private final String NOTES_PATH = "Notes";
    private final FirebaseFirestore firebaseFirestore;
    private final FirebaseUserRepository firebaseUserRepository;
    public FirebaseFolderRepository() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseUserRepository = new FirebaseUserRepository();
    }
    @Override
    public MutableLiveData<List<Folder>> getList(FolderListResultListener folderListResultListener) {
        MutableLiveData<List<Folder>> folderListMutableLiveData = new MutableLiveData<>();
        this.firebaseFirestore.collection(USERS_PATH)
                .document(firebaseUserRepository.get().getUid())
                .collection(FOLDER_PATH)
                .addSnapshotListener((value, error) -> {
                    List<Folder> folderList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc != null) {
//                            Folder folder = doc.toObject(Folder.class);
                            //doc.getReference().collection("Notes")
                            Folder folder = Folder.builder()
                                    .name(doc.getId())
                                    .notes(new ArrayList<>())
                                    .build();

                            doc.getReference().collection("Notes")
                                    .addSnapshotListener((v, e) -> {
                                        for (QueryDocumentSnapshot d : v) {
                                            folder.getNotes().add(d.toObject(Note.class));
                                        }
                                    });
                            folderList.add(folder);
                        }
                    }
                    folderListMutableLiveData.postValue(folderList);
                });
        return folderListMutableLiveData;
    }
}
