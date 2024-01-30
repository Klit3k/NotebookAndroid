package pl.edu.wat.notebookv3.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
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
import pl.edu.wat.notebookv3.repository.NoteRepos;
import pl.edu.wat.notebookv3.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class DashboardFragment extends Fragment {
    public final static String MAIN_FOLDER = "Nieprzypisane";
    private DashboardViewModel viewModel;
    private ProgressDialog progressDialog;
    private RecyclerView noteRecycler;
    private RecyclerView folderRecycler;

    @Getter
    private MutableLiveData<List<Note>> notes;
    private static NoteAdapter noteAdapter;
    private FolderAdapter folderAdapter;
    private MutableLiveData<Note> lastNote;
    private ImageView imageView;
    MaterialToolbar materialToolbar;
    List<Folder> folderList;
    @Getter
    @Setter
    private static String currentFolder = MAIN_FOLDER;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        notes = viewModel.getListNote();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);


        materialToolbar = view.findViewById(R.id.topAppBar);




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
        view.findViewById(R.id.reminders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_to_reminderFragment);
            }
        });

        view.findViewById(R.id.importantNotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragment.setCurrentFolder(NoteRepos.STARRED_PATH);
                Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_self);
            }
        });
        view.findViewById(R.id.trashedNotes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragment.setCurrentFolder(NoteRepos.TRASH_PATH);

                Navigation.findNavController(v).navigate(R.id.action_dashboardFragment_self);
            }
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
            this.folderList = folderList;
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
        imageView = view.findViewById(R.id.empty_recycler);

        lastNote = new MutableLiveData<>();
        progressDialog = new ProgressDialog(getContext());
        noteRecycler = view.findViewById(R.id.noteRecyclerView);
        noteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        noteRecycler.setHasFixedSize(true);

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(noteRecycler);


        viewModel.getListNote().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> noteList) {
                progressDialog.dismiss();
                noteAdapter = new NoteAdapter(noteList);
                noteRecycler.setAdapter(noteAdapter);
                if (noteAdapter.getItemCount() == 0) {
                    noteRecycler.setVisibility(View.INVISIBLE);
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    noteRecycler.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.INVISIBLE);
                    lastNote.postValue(noteList.stream().findFirst().orElse(null));
                }
                Log.d("TEST::lista", "Aktualnie pobranych notatek: " + noteList.size());
            }
        });


        if (getCurrentFolder().equals(NoteRepos.TRASH_PATH)) {
            view.findViewById(R.id.new_note_button).setVisibility(View.GONE);
            imageView.setVisibility(View.INVISIBLE);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.delete_black, null));
        } else {
            view.findViewById(R.id.new_note_button).setVisibility(View.VISIBLE);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.no_content, null));
        }
        view.findViewById(R.id.new_note_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardFragmentDirections.ActionDashboardFragmentToNoteTakingFragment direction =
                        DashboardFragmentDirections.actionDashboardFragmentToNoteTakingFragment(
                                null, null, null, getCurrentFolder(), false
                        );

                Navigation.findNavController(view)
                        .navigate(
                                direction
                        );
            }
        });
        return view;
    }


    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                Log.d("Test:onSwiped", "Left");
                String item = viewHolder.itemView.getTag().toString();
                if (getCurrentFolder().equals(NoteRepos.TRASH_PATH)) {
                    viewModel.removeNote(item, getCurrentFolder());
                    Snackbar.make(getView(), "Notatka została usunięta.", Snackbar.LENGTH_SHORT).show();
                } else {
                    viewModel.moveToTrash(item);
                    Snackbar.make(getView(), "Notatka została przeniesiona do kosza.", Snackbar.LENGTH_SHORT).show();
                }
            } else
            {
                Log.d("Test:onSwiped", "Right");
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                ;
                List<String> s = folderList.stream()
                        .map(e -> e.getName())
                        .filter(e -> !e.equals(NoteRepos.TRASH_PATH))
                        .filter(e -> !e.equals(NoteRepos.STARRED_PATH))
                        .collect(Collectors.toList());

                final ArrayAdapter<String> adp = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, s);
                View spinnerView = getLayoutInflater().inflate(R.layout.choose_folder, null);
                Spinner spinner = spinnerView.findViewById(R.id.spinner);
                spinner.setAdapter(adp);
                builder
                        .setTitle("Wybierz folder")
                        .setView(spinnerView)
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Navigation.findNavController(getView()).navigate(R.id.action_dashboardFragment_self);

                            }
                        })
                        .setPositiveButton("Przenieś", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("Test:onClick", "transfer: " + getCurrentFolder() + " -> " + spinner.getSelectedItem().toString() + " tag: " + viewHolder.itemView.getTag().toString());

                                viewModel.transferNote(viewHolder.itemView.getTag().toString(), getCurrentFolder(), spinner.getSelectedItem().toString());

                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Navigation.findNavController(getView()).navigate(R.id.action_dashboardFragment_self);

                            }
                        });
                builder.show();
            }
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            if (dX < 0 && isCurrentlyActive) {
                RecyclerViewSwipeDecorator.Builder builder = new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                builder.addBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_theme_light_tertiary))
                        .addActionIcon(R.drawable.delete)
                        .addCornerRadius(1, 3)
                        .create()
                        .decorate();

            } else if (dX > 0 && isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_theme_light_surfaceTint))
                        .addActionIcon(R.drawable.drive_file_move)
                        .addCornerRadius(1, 3)
                        .create()
                        .decorate();
            } else {

            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };

}