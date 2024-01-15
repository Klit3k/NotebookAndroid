package pl.edu.wat.notebookv3.repository;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

public class FirebaseStorageImageRepository {
    FirebaseStorage firebaseStorage;
    public FirebaseStorageImageRepository() {
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    public Task<Uri> uploadImage(String noteId, Uri file) {
        StorageReference imageRef = firebaseStorage.getReference(noteId+"/images/"+file.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(file);

        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return imageRef.getDownloadUrl();
                    }
                });
    }
    public void removeImage(String fileUrl) {
        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(fileUrl);
        imageRef.delete();
        Log.d("Test", "Usunieto " + imageRef.getName());

    }
}
