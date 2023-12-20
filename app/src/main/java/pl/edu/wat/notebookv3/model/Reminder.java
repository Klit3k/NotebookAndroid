package pl.edu.wat.notebookv3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
@NoArgsConstructor @Builder
public class Reminder implements Serializable {
    private String message;
    private String id;
    private String remindDate;
}
