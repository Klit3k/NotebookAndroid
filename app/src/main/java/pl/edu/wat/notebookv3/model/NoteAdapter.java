package pl.edu.wat.notebookv3.model;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.view.DashboardFragmentDirections;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements Filterable {
    List<Note> noteList;
    private List<Note> noteArrayListFilter;

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList.stream()
                .sorted(Comparator.comparing(Note::getUpdateTime).reversed())
                .collect(Collectors.toList());
        this.noteArrayListFilter = this.noteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note, parent, false);
        view.findViewById(R.id.fragmentCard);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleToolbar.setTitle(noteList.get(position).getTitle());
        holder.body.setText(noteList.get(position).getBody());
        holder.time.setText(noteList.get(position).getUpdateTime());
        holder.view.setTag(noteList.get(position).getUuid());
    }

    @Override
    public int getItemCount() {
        if (noteList != null) {
            return noteList.size();
        } else
            return 0;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.values = noteArrayListFilter;
                    filterResults.count = noteArrayListFilter.size();
                } else {
                    String searchString = constraint.toString().toLowerCase();
                    ArrayList<Note> notes = new ArrayList<>();

                    for (Note note :
                            noteArrayListFilter) {
                        if (note.getBody().toLowerCase().contains(searchString.toLowerCase()) ||
                                note.getTitle().toLowerCase().contains(searchString.toLowerCase())) {
                            notes.add(note);
                        }
                    }
                    filterResults.values = notes;
                    filterResults.count = notes.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                noteList = (ArrayList<Note>) results.values;

                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        MaterialToolbar titleToolbar;
        TextView body;
        TextView time;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            titleToolbar = view.findViewById(R.id.topAppBar);
            body = view.findViewById(R.id.body_text);
            time = view.findViewById(R.id.time_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TEST::", "OnClick tag: " + view.getTag());
                    DashboardFragmentDirections.ActionDashboardFragmentToNoteTakingFragment direction = DashboardFragmentDirections.actionDashboardFragmentToNoteTakingFragment(
                            titleToolbar.getTitle().toString()
                            , body.getText().toString()
                            , view.getTag().toString()
                    );

                       Navigation.findNavController(view)
                               .navigate(
                                       direction
                               );
                }

            });

        }
    }
}
