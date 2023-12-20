package pl.edu.wat.notebookv3.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityLog implements Serializable {
    private OperationType operationType;
    private String time;
}
