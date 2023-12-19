package pl.edu.wat.notebookv3.model.adapter;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.repository.FirebaseFolderRepository;
import pl.edu.wat.notebookv3.view.DashboardFragment;
import pl.edu.wat.notebookv3.view.DashboardFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> implements Filterable {
    List<Folder> folderList;
    FirebaseFolderRepository folderRepository;
    public FolderAdapter(List<Folder> folderList) {

        this.folderList = folderList;
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
                DashboardFragmentDirections.ActionDashboardFragmentSelf direction =
                        DashboardFragmentDirections.actionDashboardFragmentSelf()
                                .setFolder(folderList.get(position).getName());
                Navigation.findNavController(v)
                        .navigate(
                                direction
                        );
            }
        });
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
        Button button;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            button = view.findViewById(R.id.nav_folder_btn);
        }
    }
}
