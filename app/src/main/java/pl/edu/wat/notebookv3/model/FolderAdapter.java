package pl.edu.wat.notebookv3.model;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.view.DashboardFragmentDirections;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> implements Filterable {
    List<Folder> folderList;

    public FolderAdapter(List<Folder> folderList) {
        List<Folder> test = new ArrayList<>();
        this.folderList = folderList;
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
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragmentDirections.ActionDashboardFragmentToNoteTakingFragment direction = DashboardFragmentDirections.actionDashboardFragmentToNoteTakingFragment(
                            folderList.get(position).getName().toString()
                            , folderList.get(position).getNotes().toString()
                            , "Test"
                    );

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

        TextView name;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = view.findViewById(R.id.folderName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TEST::", "OnClick tag: " + view.getTag());
//                    DashboardFragmentDirections.ActionDashboardFragmentToNoteTakingFragment direction = DashboardFragmentDirections.actionDashboardFragmentToNoteTakingFragment(
//                            titleToolbar.getTitle().toString()
//                            , body.getText().toString()
//                            , view.getTag().toString()
//                    );
//
//                    Navigation.findNavController(view)
//                            .navigate(
//                                    direction
//                            );
                }

            });

        }
    }
}
