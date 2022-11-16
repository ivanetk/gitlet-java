package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit. */
    private String timestamp;
    /** Stores the address of the parent commit. */
    private String parent;
    /** Files that this commit is tracking*/
    private HashMap<String, String> directory;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, String parent) {
        this.message = message;
        this.parent = parent;
        if (parent == null) {
            timestamp = "Thu, 1 Jan 1970 00:00:00 UT";
        } else {
            timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
        }
        directory = new HashMap<>();
    }

    public void trackFiles() {

    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        timestamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    public HashMap<String, String> getDirectory() {
        return directory;
    }
    public void setDirectory(String fileName, String contentHash) {
        directory.put(fileName, contentHash);
    }
}
