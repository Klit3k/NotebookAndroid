package pl.edu.wat.notebookv3.model;

import com.google.firebase.firestore.Exclude;
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
public class Note implements Serializable {
    private String title;
    private String body;
    private String updateTime;
    private boolean starred;
    private List<String> imageUrl;
    private List<String> fileUrl;
    @Exclude
    private String uuid;
}
