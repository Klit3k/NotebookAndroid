package pl.edu.wat.notebookv3.view;

import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.model.FolderAdapter;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.model.Folder;
import pl.edu.wat.notebookv3.model.NoteAdapter;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.viewmodel.DashboardViewModel;


public class DashboardFragment extends Fragment {
    private DashboardViewModel viewModel;
    private ProgressDialog progressDialog;
    private RecyclerView noteRecycler;
    private RecyclerView folderRecycler;
    private NoteAdapter noteAdapter;
    private FolderAdapter folderAdapter;
    private MutableLiveData<Note> lastNote;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        lastNote = new MutableLiveData<>();
        progressDialog = new ProgressDialog(getContext());

        noteRecycler = view.findViewById(R.id.noteRecyclerView);
        noteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        noteRecycler.setHasFixedSize(true);

        RecyclerView.LayoutManager folderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        folderRecycler = view.findViewById(R.id.folderRecyclerView);
        folderRecycler.setLayoutManager(folderLayoutManager);
//        folderRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        folderRecycler.setHasFixedSize(true);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(noteRecycler);
        ImageView imageView = view.findViewById(R.id.empty_recycler);

        viewModel.getListNote().observe(getViewLifecycleOwner(), noteList -> {
            progressDialog.dismiss();
            noteAdapter = new NoteAdapter(noteList);
            noteRecycler.setAdapter(noteAdapter);
            noteAdapter.notifyDataSetChanged();

            if(noteList.isEmpty()) {
                noteRecycler.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                noteRecycler.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                lastNote.postValue(noteList.stream().findFirst().get());
            }
        });

        viewModel.getListFolder().observe(getViewLifecycleOwner(), folderList -> {
            folderAdapter = new FolderAdapter(folderList);
            folderRecycler.setAdapter(folderAdapter);
            folderAdapter.notifyDataSetChanged();
        });
        view.findViewById(R.id.account_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DashboardFragmentDirections.ActionDashboardFragmentToAccountFragment direction =
                                DashboardFragmentDirections.actionDashboardFragmentToAccountFragment()
                                        .setLastNoteDate(lastNote.getValue().getUpdateTime());

                        Navigation.findNavController(view)
                                .navigate(
                                        direction
                                );
                    }
                });
        view.findViewById(R.id.new_note_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_dashboardFragment_to_noteTakingFragment);
            }
        });


        return view;
    }
    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public int getMovementFlags(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            viewModel.getNote(viewHolder.itemView.getTag().toString())
                    .observe(getActivity(), note -> {
                        Snackbar.make(getView(), "Notatka usunięta.", Snackbar.LENGTH_LONG)
                                .setAction("Cofnij", view -> {
                                    viewModel.recoverNote(note);
                                    Snackbar.make(getView(), "Notatka została przywrócona.", Snackbar.LENGTH_SHORT);
                                    noteAdapter.notifyDataSetChanged();
                                }).show();
                        viewModel.removeNote(viewHolder.itemView.getTag().toString());
                        noteAdapter.notifyDataSetChanged();
                    });

        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete_background))
                    .addActionIcon(R.drawable.delete)
                    .addCornerRadius(1, 3)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };

}