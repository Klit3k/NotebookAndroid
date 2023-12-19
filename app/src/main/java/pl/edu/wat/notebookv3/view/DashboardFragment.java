package pl.edu.wat.notebookv3.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.firestore.DocumentSnapshot;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.model.*;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.adapter.FolderAdapter;
import pl.edu.wat.notebookv3.model.adapter.NoteAdapter;
import pl.edu.wat.notebookv3.viewmodel.DashboardViewModel;

import java.util.List;


public class DashboardFragment extends Fragment {
    private final String MAIN_FOLDER = "Main";
    private DashboardViewModel viewModel;
    private ProgressDialog progressDialog;
    private RecyclerView noteRecycler;
    private RecyclerView folderRecycler;
    @Getter
    static MutableLiveData<List<Note>> notes;
    private static NoteAdapter noteAdapter;
    private FolderAdapter folderAdapter;
    private MutableLiveData<Note> lastNote;
    private ImageView imageView;
    private DashboardFragmentArgs args;
    MaterialToolbar materialToolbar;
    @Getter
    @Setter
    private static String currentFolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCurrentFolder(MAIN_FOLDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        materialToolbar = view.findViewById(R.id.topAppBar);


        if (getArguments() != null && !getArguments().isEmpty()) {
            args = DashboardFragmentArgs.fromBundle(getArguments());
            if (args.getFolder() != null) setCurrentFolder(args.getFolder());
        }


        /*
           ============================================================
                               DRAWER
           ============================================================
        */

        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.nav_view);
        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        view.findViewById(R.id.accountInfo).setOnClickListener(v -> {
            DashboardFragmentDirections.ActionDashboardFragmentToAccountFragment direction;
            if (lastNote != null && lastNote.getValue() != null) {
                direction =
                        DashboardFragmentDirections.actionDashboardFragmentToAccountFragment()
                                .setLastNoteDate(lastNote.getValue().getUpdateTime());
            } else {
                direction =
                        DashboardFragmentDirections.actionDashboardFragmentToAccountFragment()
                                .setLastNoteDate("None");
            }
            Navigation.findNavController(view)
                    .navigate(
                            direction
                    );
        });
        view.findViewById(R.id.navLogout).setOnClickListener(v -> {
            viewModel.logout(view);
        });
        view.findViewById(R.id.add).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Tworzenie nowego folderu");
            View customLayout = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
            builder.setView(customLayout);
            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Utwórz", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText text = customLayout.findViewById(R.id.foldersName);
                    viewModel.existsFolder(text.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (!documentSnapshot.exists()) {
                                        dialog.dismiss();
                                        viewModel.addFolder(text.getText().toString());
                                        Snackbar.make(view, "Folder został utworzony.", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        dialog.dismiss();
                                        Snackbar.make(view, "Folder o podanej nazwie już istnieje.", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            });
            builder.create().show();
        });

        //Folders
        view.findViewById(R.id.mainFolder).setOnClickListener(v -> {
            DashboardFragmentDirections.ActionDashboardFragmentSelf direction =
                    DashboardFragmentDirections.actionDashboardFragmentSelf()
                            .setFolder("1");
            Navigation.findNavController(view)
                    .navigate(
                            direction
                    );
        });
//

        //Recycler
        RecyclerView.LayoutManager folderLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        folderRecycler = view.findViewById(R.id.folderRecyclerView);
        folderRecycler.setLayoutManager(folderLayoutManager);
        folderRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        folderRecycler.setHasFixedSize(true);
        viewModel.getListFolder().observe(getViewLifecycleOwner(), folderList -> {
            folderAdapter = new FolderAdapter(folderList);
            folderRecycler.setAdapter(folderAdapter);
            folderAdapter.notifyDataSetChanged();
        });

        /*
           ============================================================
                               Toolbar menu
           ============================================================
        */

        materialToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.sort_title_asc) {
                    noteAdapter.sortByTitle(true);
                } else if (item.getItemId() == R.id.sort_title_desc) {
                    noteAdapter.sortByTitle(false);
                } else if (item.getItemId() == R.id.sort_date_asc) {
                    noteAdapter.sortByDate(true);
                } else if (item.getItemId() == R.id.sort_date_desc) {
                    noteAdapter.sortByDate(false);
                }
                return false;
            }
        });
        /*
           ============================================================
                             Note displaying
           ============================================================
        */


        lastNote = new MutableLiveData<>();
        progressDialog = new ProgressDialog(getContext());

        noteRecycler = view.findViewById(R.id.noteRecyclerView);
        noteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        noteRecycler.setHasFixedSize(true);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(noteRecycler);
        imageView = view.findViewById(R.id.empty_recycler);

        viewModel.getListNote(getCurrentFolder()).observe(getViewLifecycleOwner(), noteList -> {
            progressDialog.dismiss();
            noteAdapter = new NoteAdapter(noteList);
            noteRecycler.setAdapter(noteAdapter);
            Log.d("TEST", "aktualny folder: " + getCurrentFolder());


            if (noteList.isEmpty()) {
                noteRecycler.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                Log.d("TEST::lista", "Aktualnie pobranych notatek: " + noteList.size());
            } else {
                noteRecycler.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                lastNote.postValue(noteList.stream().findFirst().orElse(null));
                Log.d("TEST::lista", "Aktualnie pobranych notatek: " + noteList.size());
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
                    .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_theme_dark_error))
                    .addActionIcon(R.drawable.delete)
                    .addCornerRadius(1, 3)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };

}