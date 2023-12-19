package pl.edu.wat.notebookv3.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.viewmodel.NoteTakingViewModel;

public class NoteTakingFragment extends Fragment {
    private NoteTakingFragmentArgs args;
    private TextInputEditText bodyInputText;
    private TextInputEditText titleInputText;
    private NoteTakingViewModel noteTakingViewModel;
    private String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public AlertDialog createShareDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String[] choices = {"Item One", "Item Two", "Item Three"};

        builder.setTitle("Test")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setSingleChoiceItems(choices, 0, (dialog, which) -> {

                });



        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_taking, container, false);
        bodyInputText = view.findViewById(R.id.body_input_text);
        titleInputText = view.findViewById(R.id.title_input_text);
        noteTakingViewModel = new ViewModelProvider(this).get(NoteTakingViewModel.class);
        if( getArguments() != null && !getArguments().isEmpty() ) {
            args = NoteTakingFragmentArgs.fromBundle(getArguments());
            if (args.getBody() != null) bodyInputText.setText(args.getBody());
            if (args.getTitle() != null) titleInputText.setText(args.getTitle());
            if (args.getTag() != null) tag = args.getTag();
        }

        MaterialToolbar materialToolbar = view.findViewById(R.id.materialToolbar);
        materialToolbar.setOnMenuItemClickListener(menuClickItemListener());
        materialToolbar.setNavigationOnClickListener(onNavBackListener());


        return view;
    }

    @NonNull
    private View.OnClickListener onNavBackListener() {
        return v -> {
            String title = titleInputText.getText().toString();
            String body = bodyInputText.getText().toString();
            Log.d("Test", "currentFolder = "+args.getCurrentFolder());
            if (!body.isEmpty()) {
                if (tag != null)
                    noteTakingViewModel.updateNote(tag, title, body);
                else
                    noteTakingViewModel.createNote(title, body);
            }

            NoteTakingFragmentDirections.ActionNoteTakingFragmentToDashboardFragment direction =
                    NoteTakingFragmentDirections.actionNoteTakingFragmentToDashboardFragment(DashboardFragment.getCurrentFolder());


            Navigation.findNavController(v)
                    .navigate(
                            direction
                    );
        };
    }

    @NonNull
    private Toolbar.OnMenuItemClickListener menuClickItemListener() {
        return item -> {
            if (item.getItemId() == R.id.share_item) {
                createShareDialog(getActivity()).show();
                return true;
            }
            return item.getItemId() == R.id.reminder_item;
        };
    }
}
