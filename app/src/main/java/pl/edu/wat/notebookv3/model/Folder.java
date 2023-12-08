package pl.edu.wat.notebookv3.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Folder implements Serializable {
    private String id;
    private String name;
    private List<Note> notes;
}
