package pl.edu.wat.notebookv3.model.safenote;

public class SafenoteRequest {

    private String note;
    private int liftime;
    private String password;
    private int readCount;

    public SafenoteRequest(String note, int liftime, String password, int readCount) {
        this.note = note;
        this.liftime = liftime;
        this.password = password;
        this.readCount = readCount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getLiftime() {
        return liftime;
    }

    public void setLiftime(int liftime) {
        this.liftime = liftime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }
}
