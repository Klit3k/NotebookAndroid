package pl.edu.wat.notebookv3.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Reminder;
import pl.edu.wat.notebookv3.model.adapter.ReminderAdapter;
import pl.edu.wat.notebookv3.viewmodel.ReminderViewModel;

import java.util.List;

public class ReminderFragment extends Fragment {

    private RecyclerView notificationRecycler;
    private ReminderViewModel viewModel;
    private ReminderAdapter reminderAdapter;
    private List<Reminder> notificationList;

    public ReminderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReminderViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);
        MaterialToolbar toolbar = view.findViewById(R.id.materialToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_reminderFragment_to_dashboardFragment);
            }
        });


        RecyclerView notificationRecycler = view.findViewById(R.id.notification_recycler);
        RecyclerView.LayoutManager notificationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        notificationRecycler.setLayoutManager(notificationLayoutManager);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
//        notificationRecycler.setHasFixedSize(true);

        TextView empty = view.findViewById(R.id.empty_recycler);

        viewModel.getReminders().observe(getViewLifecycleOwner(), notifications -> {
            reminderAdapter = new ReminderAdapter(notifications);
            notificationRecycler.setAdapter(reminderAdapter);
            

            if(reminderAdapter.getItemCount() == 0) {
                notificationRecycler.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            } else {
                notificationRecycler.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
            }
        });

        return view;
    }
}