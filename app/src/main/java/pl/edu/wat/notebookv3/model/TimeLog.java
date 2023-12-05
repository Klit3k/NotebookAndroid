package pl.edu.wat.notebookv3.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@Data @AllArgsConstructor
public class TimeLog implements Serializable {
    private String time;
}
