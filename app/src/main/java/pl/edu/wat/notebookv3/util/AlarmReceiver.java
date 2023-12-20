package pl.edu.wat.notebookv3.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import pl.edu.wat.notebookv3.MainActivity;
import pl.edu.wat.notebookv3.R;
import pl.edu.wat.notebookv3.model.Reminder;
import pl.edu.wat.notebookv3.repository.FirebaseReminderRepository;

import java.time.LocalDateTime;

public class AlarmReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        Reminder reminder = new Reminder();
        reminder.setMessage(intent.getStringExtra("Message"));
        reminder.setRemindDate(intent.getStringExtra("RemindDate"));
        reminder.setId(String.valueOf(intent.getIntExtra("id",0)));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "general")
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle("Przypomnienie")
                .setContentText(reminder.getMessage())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());
    }
}
