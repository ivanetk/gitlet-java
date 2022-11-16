package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  Contains methods for the the creation of files e.g. commit files, blob files.
 *
 *  @author Ivan
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File COMMIT_DIR = join(CWD,".gitlet", "commits");
    public static final File BLOB_DIR = join(CWD, ".gitlet", "blobs");
    public static final File STAGE_DIR = join (CWD, ".gitlet", "stage");

    /* TODO: fill in the rest of this class. */
    public static void setupPersistence() {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            COMMIT_DIR.mkdir();
            BLOB_DIR.mkdir();
            STAGE_DIR.mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
    }

    public static void init() throws IOException {
        //creates an initial commit, saves it to a file.
        Commit initialCommit = new Commit("Initial commit", null);
        String initFileName = writeFile(initialCommit, COMMIT_DIR);

        //create master branch
        File masterPointer = join(GITLET_DIR, "master");
        masterPointer.createNewFile();
        writeContents(masterPointer, initFileName);

        //create HEAD pointer
        File headPointer = join(GITLET_DIR, "HEAD");
        headPointer.createNewFile();
        writeContents(headPointer, initFileName);
    }

    public static void add(String file) throws IOException {
        //read file
        byte[] currFileContent = readContents(join(CWD, file));
        //SHA1 contents of the file
        String currContent = sha1(currFileContent);

        /* TODO: remove from staging area if current working version
                 is identical to that in the current commit
         */

        //Check if current commit (head) contains this SHA1.
        //1. read commit
        String currCommitId = readContentsAsString(join(GITLET_DIR, "master"));
        Commit headCommit = readObject(join(COMMIT_DIR, currCommitId), Commit.class);
        //check sha1 hash content of current commit with working file
        String commitContent = headCommit.getDirectory().get(file);

        //If no, write file to stage, overwrite file with same name. Use filename, do not hash the title.
        if (commitContent == null || !commitContent.equals(currContent)) {
            File stageFile = join(STAGE_DIR, file);
            stageFile.createNewFile();
            writeContents(stageFile, currFileContent);
            return;
        }
        System.out.println("File to be added is the same as the one in commit");
    }

    public static void commit(String message) throws IOException {
        //get files in the staging area
        List<String> files = plainFilenamesIn(STAGE_DIR);
        if (files.size() == 0) {
            System.out.println("No changes added to the commit.");
            return;
        }
        //get head commit name
        String head = readContentsAsString(join(GITLET_DIR, "master"));
        Commit headCommit = readObject(join(COMMIT_DIR, head), Commit.class);
        //instantiate new commit with master as the parent
        Commit newCommit = headCommit;
        newCommit.setParent(head);
        newCommit.setTimestamp();
        newCommit.setMessage(message);

        //write files in the staging area to blobs. get the hash title and add to the commit directory.
        for (String fileName: files) {
            byte[] currFileContent = readContents(Utils.join(STAGE_DIR, fileName));
            String contentHash = sha1(currFileContent);
            newCommit.setDirectory(fileName, contentHash);
            Blob newBlob = new Blob(currFileContent);
            File newFile = join(BLOB_DIR, contentHash);
            newFile.createNewFile();
            writeObject(newFile, newBlob);
            restrictedDelete(Utils.join(STAGE_DIR, fileName));
        }
        String commitName = writeFile(newCommit, COMMIT_DIR);
        //update master branch
        File masterPointer = join(GITLET_DIR, "master");
        writeContents(masterPointer, commitName);

        //update HEAD pointer
        File headPointer = join(GITLET_DIR, "HEAD");
        writeContents(headPointer, commitName);
    }

    public static void log() {
        //get head commit
        if (!GITLET_DIR.exists()) {
            System.out.println("git is not initialized in this directory");
            return;
        }
        String commitId = readContentsAsString(join(GITLET_DIR, "master"));
        Commit currCommit = readObject(join(COMMIT_DIR, commitId), Commit.class);
        while (true) {
            System.out.println("===");
            System.out.println("commit " + commitId);
            System.out.println("Date: " + currCommit.getTimestamp());
            System.out.println(currCommit.getMessage());
            System.out.println();
            if (currCommit.getParent() == null) {
                break;
            }
            commitId = currCommit.getParent();
            currCommit = readObject(join(COMMIT_DIR, commitId), Commit.class);
        }
    }

    public static void checkout(String commitId, String fileName) {
        try {
            //get commitid directory
            Commit targetCommit = readObject(join(COMMIT_DIR, commitId), Commit.class);
            HashMap<String, String> fileDirectory = targetCommit.getDirectory();

            //get blob content from commit
            String blobId = fileDirectory.get(fileName);
            File targetFile = join(CWD, fileName);
            Blob targetBlob = readObject(join(BLOB_DIR, blobId), Blob.class);

            //write blob content to target file
            byte[] content = targetBlob.getContent();
            writeContents(targetFile, content);

            //update HEAD pointer
            File headPointer = join(GITLET_DIR, "HEAD");
            writeContents(headPointer, commitId);
        } catch (IllegalArgumentException e) {
            System.out.println("Commit or file not found");
        }

    }


    private static String writeFile(Serializable object, File dir) throws IOException {
        String fileName = sha1(serialize(object));
        File newFile = join(dir, fileName);
        newFile.createNewFile();
        writeObject(newFile, object);
        return fileName;
    }

}
