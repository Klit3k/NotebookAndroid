package pl.edu.wat.notebookv3.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class User implements Serializable {
    private UUID uuid;
    private String email;
    private String password;
    private List<Note> posts;

    public User(UUID uuid, String email, String password, List<Note> posts) {
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.posts = posts;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Note> getPosts() {
        return posts;
    }

    public void setPosts(List<Note> posts) {
        this.posts = posts;
    }
}
