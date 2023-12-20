package pl.edu.wat.notebookv3.model.safenote;

public final class SafenoteRequestBuilder {
    private String note;
    private int liftime;
    private String password;
    private int readCount;

    private SafenoteRequestBuilder() {
    }

    public static SafenoteRequestBuilder aSafenoteRequest() {
        return new SafenoteRequestBuilder();
    }

    public SafenoteRequestBuilder setNote(String note) {
        this.note = note;
        return this;
    }

    public SafenoteRequestBuilder setLiftime(int liftime) {
        this.liftime = liftime;
        return this;
    }

    public SafenoteRequestBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public SafenoteRequestBuilder setReadCount(int readCount) {
        this.readCount = readCount;
        return this;
    }

    public SafenoteRequest build() {
        return new SafenoteRequest(note, liftime, password, readCount);
    }
}
