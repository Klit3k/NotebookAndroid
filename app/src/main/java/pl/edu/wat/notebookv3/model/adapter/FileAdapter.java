package pl.edu.wat.notebookv3.model.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.StorageMetadata;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.repository.FirebaseStorageFileRepository;
import pl.edu.wat.notebookv3.repository.NoteRepos;
import pl.edu.wat.notebookv3.util.AlarmReceiver;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    NoteRepos noteRepos;
    String noteId, currentFolder;
    List<String> fileList;
    FirebaseStorageFileRepository firebaseStorageFileRepository;

    public FileAdapter(List<String> fileList, String noteId, String currentFolder) {
        noteRepos = new NoteRepos();
        this.fileList = fileList;
        this.noteId = noteId;
        this.currentFolder = currentFolder;
        this.firebaseStorageFileRepository = new FirebaseStorageFileRepository();

    }

    @NonNull
    @NotNull
    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_file, parent, false);
        return new FileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FileAdapter.ViewHolder holder, int position) {
        FirebaseStorageFileRepository firebaseStorageFileRepository = new FirebaseStorageFileRepository();
        firebaseStorageFileRepository.getFileName(fileList.get(holder.getBindingAdapterPosition()))
                        .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                            @Override
                            public void onSuccess(StorageMetadata storageMetadata) {

                                holder.fileName.setText(storageMetadata.getCustomMetadata("name"));

                            }
                        });

        holder.fileName.setTag(fileList.get(holder.getBindingAdapterPosition()));
        holder.view.setTag(noteId);
        Log.d("Test", "Rozmiar listy binding: " + fileList.size());


        holder.imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Test", holder.view.getTag().toString() + " file: " + holder.fileName.getTag().toString());

                noteRepos.updateFile(noteId, currentFolder, fileList);
                firebaseStorageFileRepository.removeFile(holder.fileName.getTag().toString());
                notifyItemRemoved(holder.getBindingAdapterPosition());

                String fileurl = fileList.stream()
                        .filter(e -> e.equals(holder.fileName.getTag().toString()))
                        .findFirst()
                        .get();

                fileList.remove(fileurl);

                Snackbar.make(v, "Usunięto plik", Snackbar.LENGTH_SHORT).show();

            }
        });
        holder.imageButtonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse(holder.fileName.getTag().toString()));
                v.getContext().startActivity(httpIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (fileList != null) {
            return fileList.size();
        } else
            return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView fileName;
        ImageButton imageButtonDownload;
        ImageButton imageButtonRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            fileName = view.findViewById(R.id.filename);
            imageButtonRemove = view.findViewById(R.id.remove);
            imageButtonDownload = view.findViewById(R.id.download);
        }
    }
}
