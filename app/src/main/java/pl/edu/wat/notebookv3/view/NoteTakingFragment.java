package pl.edu.wat.notebookv3.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
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
            if (tag != null)
                if (title.isEmpty() && body.isEmpty()) {
                    noteTakingViewModel.deleteNote(tag);
                    Snackbar.make(v, "Empty note has been removed.", Snackbar.LENGTH_SHORT).show();
                } else noteTakingViewModel.updateNote(tag, title, body);
            else
                noteTakingViewModel.createNote(title, body);
            Navigation.findNavController(v)
                    .navigate(R.id.action_noteTakingFragment_to_dashboardFragment);
        };
    }

    @NonNull
    private Toolbar.OnMenuItemClickListener menuClickItemListener() {
        return item -> {
            if (item.getItemId() == R.id.share_item) {
                noteTakingViewModel.share();
                return true;
            }
            return item.getItemId() == R.id.reminder_item;
        };
    }
}