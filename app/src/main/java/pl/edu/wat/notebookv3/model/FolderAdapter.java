package pl.edu.wat.notebookv3.model;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.repository.FirebaseFolderRepository;
import pl.edu.wat.notebookv3.view.DashboardFragment;
import pl.edu.wat.notebookv3.view.DashboardFragmentDirections;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> implements Filterable {
    List<Folder> folderList;
    FirebaseFolderRepository folderRepository;
    public FolderAdapter(List<Folder> folderList) {
        List<Folder> test = new ArrayList<>();
        this.folderList = folderList;
        this.folderRepository = new FirebaseFolderRepository();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_folder, parent, false);
        view.findViewById(R.id.folder);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(folderList.get(position).getName());
        holder.view.setTag(folderList.get(position).getId());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragment.setCurrentFolder(v.getTag().toString());
                Log.d("TEST", "Ustawiono aktualny folder na: " + DashboardFragment.getCurrentFolder());
                DashboardFragmentDirections.ActionDashboardFragmentSelf direction = DashboardFragmentDirections.actionDashboardFragmentSelf().setFolder(DashboardFragment.getCurrentFolder());
                Navigation.findNavController(v)
                        .navigate(
                                direction
                        );
            }
        });
        holder.view.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        View draggedView = (View) event.getLocalState();
                        folderRepository.moveNoteToFolder(draggedView.getTag().toString(), v.getTag().toString());
                        break;
                    default:
                        break;
                }
                return true;
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

        TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = view.findViewById(R.id.folderName);

        }
    }
}
