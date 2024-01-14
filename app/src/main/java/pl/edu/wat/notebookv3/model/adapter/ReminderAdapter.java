package pl.edu.wat.notebookv3.model.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Reminder;
import pl.edu.wat.notebookv3.repository.FirebaseReminderRepository;
import pl.edu.wat.notebookv3.util.AlarmReceiver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    List<Reminder> reminderList;
    FirebaseReminderRepository reminderRepository;

    public ReminderAdapter(List<Reminder> reminderList) {

        this.reminderList = reminderList.stream()
                .filter(e -> LocalDateTime.parse(e.getRemindDate()).isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Reminder::getRemindDate))
                .collect(Collectors.toList());
        this.reminderRepository = new FirebaseReminderRepository();
    }

    @NonNull
    @NotNull
    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification, parent, false);
        return new ReminderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReminderAdapter.ViewHolder holder, int position) {
        holder.view.setTag(reminderList.get(position).getId());
        LocalDateTime cuteDate = LocalDateTime.parse(reminderList.get(position).getRemindDate());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        holder.time.setText(cuteDate.format(formatter).toString());
        holder.title.setTitle(reminderList.get(position).getMessage());

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Czy chcesz usunąć powiadomienie ? ")
                        .setMessage(Html.fromHtml(String.format("Zaplanowane na: <b>%s</b> \nDotyczy notatki: <b>%s</b>",
                                holder.time.getText().toString(), holder.title.getTitle().toString())))
                        .setPositiveButton("Usuń", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reminderRepository.remove(holder.view.getTag().toString());
                                notifyItemRemoved(position);
                                Reminder reminder = reminderList.stream().filter(e -> e.getId().equals(holder.view.getTag().toString()))
                                        .findFirst()
                                        .get();
                                reminderList.remove(reminder);
                                Snackbar.make(v, "Usunięto powiadomienie", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        if (reminderList != null) {
            return reminderList.size();
        } else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        MaterialToolbar title;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            title = view.findViewById(R.id.title);
            time = view.findViewById(R.id.body_text);

        }
    }

    public void cancelAlarm(Context context, AlarmManager alarmManager) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);

        Log.d("Test:cancelAlarm", "Anulowano notyfikacje");
    }
}
