package pl.edu.wat.notebookv3.model.adapter;

import android.graphics.drawable.Drawable;
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

        holder.view.setTag(folderList.get(position).getName());
        holder.button.setText(folderList.get(position).getName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragment.setCurrentFolder(holder.button.getText().toString());

                Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_self);
            }
        });

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
