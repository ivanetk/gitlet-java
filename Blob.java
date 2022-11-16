package gitlet;

import java.io.File;
import java.io.Serializable;


public class Blob implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Blob class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used.
     */
    /** References the content of the blob**/
    private byte[] content;

    /* TODO: fill in the rest of this class. */
    public Blob(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }


}
