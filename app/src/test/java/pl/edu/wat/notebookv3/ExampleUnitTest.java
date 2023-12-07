package pl.edu.wat.notebookv3;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import org.junit.Test;
import pl.edu.wat.notebookv3.model.Note;
import pl.edu.wat.notebookv3.repository.FirebaseNoteRepository;
import pl.edu.wat.notebookv3.repository.FirebaseUserRepository;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }



}