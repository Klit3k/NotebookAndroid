package pl.edu.wat.notebookv3.model.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.model.safenote.SafenoteResponse;
import pl.edu.wat.notebookv3.util.SafenoteService;
import pl.edu.wat.notebookv3.view.DashboardFragment;
import pl.edu.wat.notebookv3.view.DashboardFragmentDirections;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements Filterable {
    List<Note> noteList;
    private List<Note> noteArrayListFilter;

    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
        this.noteList.sort(Comparator.comparing(Note::getUpdateTime).reversed());
        this.noteArrayListFilter = this.noteList;
    }


    public void sortByTitle(boolean asc) {
        if(asc)
            noteList.sort(Comparator.comparing(Note::getTitle));
        else
            noteList.sort(Comparator.comparing(Note::getTitle).reversed());
        notifyDataSetChanged();
    }

    public void sortByDate(boolean asc) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy, HH:mm:ss");

        if (asc)
            noteList.sort(Comparator.comparing(e -> LocalDateTime.parse(e.getUpdateTime(), dtf)));
        else {
            noteList.sort(Comparator.comparing((Note e) -> LocalDateTime.parse(e.getUpdateTime(), dtf)).reversed());
        }
        notifyDataSetChanged();
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

        if(noteList.get(holder.getBindingAdapterPosition()).getTitle().length() >= 40) {
            holder.titleToolbar.setTitle(noteList.get(holder.getBindingAdapterPosition()).getTitle().substring(0, 40)+"...");
        } else  holder.titleToolbar.setTitle(noteList.get(holder.getBindingAdapterPosition()).getTitle());
        if(noteList.get(holder.getBindingAdapterPosition()).getBody().length() >= 35) {
            holder.body.setText(noteList.get(holder.getBindingAdapterPosition()).getBody().substring(0, 35)+"...");
        } else holder.body.setText(noteList.get(holder.getBindingAdapterPosition()).getBody());

        holder.time.setText(noteList.get(holder.getBindingAdapterPosition()).getUpdateTime());
        holder.view.setTag(noteList.get(holder.getBindingAdapterPosition()).getUuid());
        holder.body.setTag(noteList.get(holder.getBindingAdapterPosition()).isStarred());
        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                Log.d("TEST::", "LongClick tag: " + v.getTag());

                return true;
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TEST::", "OnClick tag: " + holder.view.getTag());
                DashboardFragmentDirections.ActionDashboardFragmentToNoteTakingFragment direction = DashboardFragmentDirections.actionDashboardFragmentToNoteTakingFragment(
                        noteList.get(holder.getBindingAdapterPosition()).getTitle()
                        ,  noteList.get(holder.getBindingAdapterPosition()).getBody()
                        ,  noteList.get(holder.getBindingAdapterPosition()).getUuid()
                        , DashboardFragment.getCurrentFolder()
                        , noteList.get(holder.getBindingAdapterPosition()).isStarred()
                );

                Navigation.findNavController( holder.view)
                        .navigate(
                                direction
                        );
            }

        });
    }
    public void updateList() {
        noteList.clear();
        noteList.addAll(noteList);
        this.notifyDataSetChanged();
        Log.d("TEST", "Update list");
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

    public void setFolder(String folderId) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        MaterialToolbar titleToolbar;
        TextView body;
        TextView time;
        MaterialCardView card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            titleToolbar = view.findViewById(R.id.topAppBar);
            body = view.findViewById(R.id.body_text);
            time = view.findViewById(R.id.time_text);
            card = view.findViewById(R.id.card);

        }
    }
}
