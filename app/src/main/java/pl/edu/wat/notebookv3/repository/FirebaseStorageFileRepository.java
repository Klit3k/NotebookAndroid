package pl.edu.wat.notebookv3.repository;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class FirebaseStorageFileRepository {
    FirebaseStorage firebaseStorage;
    public FirebaseStorageFileRepository() {
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    public Task<Uri> uploadFile(String noteId, Uri file, String filename) {
        StorageReference fileRef = firebaseStorage.getReference(noteId+"/files/"+file.getLastPathSegment());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setCustomMetadata("name", filename)
                .build();

        UploadTask uploadTask = fileRef.putFile(file, metadata);

        return uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return fileRef.getDownloadUrl();
            }
        });
    }

    public Task<StorageMetadata> getFileName(String fileUrl) {
        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(fileUrl);

        return imageRef.getMetadata();

    }

    public void removeFile(String fileUrl) {
        StorageReference fileRef = firebaseStorage.getReferenceFromUrl(fileUrl);
        fileRef.delete();
        Log.d("Test", "Usunieto " + fileRef.getName());
    }

}
