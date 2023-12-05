package pl.edu.wat.notebookv3.model;

import com.google.firebase.firestore.Exclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User implements Serializable {
    private String uuid;
    private String email;
//    private List<Note> posts;

}
