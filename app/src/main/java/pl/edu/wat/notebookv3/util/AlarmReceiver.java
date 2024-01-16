package pl.edu.wat.notebookv3.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
        reminder.setId(intent.getStringExtra("id"));

        Log.d("Test", "onReceive: message="+reminder.getMessage() + " id="+reminder.getId());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "general")
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle("Przypomnienie")
                .setContentText(reminder.getMessage())
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(Integer.parseInt(reminder.getId()), builder.build());
    }
}
