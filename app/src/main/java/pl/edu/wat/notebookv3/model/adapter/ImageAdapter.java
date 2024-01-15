package pl.edu.wat.notebookv3.model.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.repository.FirebaseStorageImageRepository;
import pl.edu.wat.notebookv3.repository.NoteRepos;
import pl.edu.wat.notebookv3.view.DashboardFragment;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    NoteRepos noteRepos;
    String noteId, currentFolder;
    List<String> imageList;

    FirebaseStorageImageRepository firebaseStorageImageRepository;

    public ImageAdapter(List<String> imageList, String noteId, String currentFolder) {
        noteRepos = new NoteRepos();
        this.imageList = imageList;
        this.noteId = noteId;
        this.currentFolder = currentFolder;
        this.firebaseStorageImageRepository = new FirebaseStorageImageRepository();

    }

    @NonNull
    @NotNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_image, parent, false);
        return new ImageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImageAdapter.ViewHolder holder, int position) {
        Glide.with(holder.view).load(imageList.get(position)).into(holder.imageView);

        holder.imageView.setTag(imageList.get(holder.getBindingAdapterPosition()));


        final ImagePopup imagePopup = new ImagePopup(holder.view.getContext());
        imagePopup.setWindowHeight(800); // Optional
        imagePopup.setWindowWidth(800); // Optional
        imagePopup.setBackgroundColor(Color.TRANSPARENT);  // Optional
        imagePopup.setFullScreen(true); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup.initiatePopupWithGlide(imageList.get(holder.getBindingAdapterPosition()));
                imagePopup.viewPopup();
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Czy chcesz usunąć obraz ? ")
                        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                noteRepos.updateImage(noteId, DashboardFragment.getCurrentFolder(), imageList);
                                firebaseStorageImageRepository.removeImage(imageList.get(holder.getBindingAdapterPosition()));

                                notifyItemRemoved(holder.getBindingAdapterPosition());

                                String imageUrl = imageList.stream()
                                        .filter(e -> e.equals(holder.imageView.getTag().toString()))
                                        .findFirst()
                                        .get();

                                imageList.remove(imageUrl);
                                Snackbar.make(v, "Usunięto obraz", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (imageList != null) {
            return imageList.size();
        } else
            return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imageView = view.findViewById(R.id.iconImage);

        }
    }
}
