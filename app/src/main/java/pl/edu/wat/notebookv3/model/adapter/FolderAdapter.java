package pl.edu.wat.notebookv3.model.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.repository.FirebaseFolderRepository;
import pl.edu.wat.notebookv3.repository.NoteRepos;
import pl.edu.wat.notebookv3.view.DashboardFragment;
import pl.edu.wat.notebookv3.view.DashboardFragmentDirections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> implements Filterable {
    List<Folder> folderList;
    FirebaseFolderRepository folderRepository;

    public FolderAdapter(List<Folder> folderList) {

        this.folderList = folderList.stream()
                .filter(e -> !e.getName().equals(NoteRepos.TRASH_PATH))
                .filter(e -> !e.getName().equals(NoteRepos.STARRED_PATH))
                .collect(Collectors.toList());
        this.folderRepository = new FirebaseFolderRepository();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nav_folder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.view.setTag(folderList.get(holder.getBindingAdapterPosition()).getName());
        holder.button.setText(folderList.get(holder.getBindingAdapterPosition()).getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragment.setCurrentFolder(holder.button.getText().toString());

                Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_self);
            }
        });

        if(!holder.button.getText().equals(DashboardFragment.MAIN_FOLDER)) {
            holder.button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                    builder
                            .setTitle("Usuwanie folderu")
                            .setMessage("Czy chcesz usunąć folder: \n" + holder.button.getText())
                            .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    folderRepository.remove(holder.button.getText().toString());
                                    notifyItemRemoved(holder.getBindingAdapterPosition());
                                    if (DashboardFragment.getCurrentFolder().equals(holder.button.getText().toString())) {
                                        DashboardFragment.setCurrentFolder(DashboardFragment.MAIN_FOLDER);
                                        Navigation.findNavController(v)
                                                .navigate(R.id.action_dashboardFragment_self);
                                    }
                                }
                            })
                            .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setCancelable(false)
                            .show();
                    return true;
                }
            });
        }
        if (holder.button.getText().equals(DashboardFragment.getCurrentFolder())) {
            holder.button.setIcon(ResourcesCompat.getDrawable(holder.view.getResources(), R.drawable.folder_special, null));
        } else holder.button.setIcon(ResourcesCompat.getDrawable(holder.view.getResources(), R.drawable.folder, null));

    }

    @Override
    public int getItemCount() {
        if (folderList != null) {
            return folderList.size();
        } else
            return 0;
    }

    @Override
    public Filter getFilter() {
        return null;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        MaterialButton button;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            button = view.findViewById(R.id.nav_folder_btn);

        }
    }
}
