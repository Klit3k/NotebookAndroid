package pl.edu.wat.notebookv3.model;

import android.app.PendingIntent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class Reminder implements Serializable {
    private String message;
    private String id;
    private String remindDate;
    private Map<String, Object> pendingIntent;
}
