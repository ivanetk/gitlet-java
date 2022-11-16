package gitlet;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?®
        if (args.length == 0) {
            System.out.println("Please enter a command");
            return;
        }
        String command = args[0];
        switch(command) {
            case "init":
                // TODO: handle the `init` command
                Repository.setupPersistence();
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                //call add method
                //use args[1] as the filename
                Repository.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    break;
                }
                Repository.commit(args[1]);
                break;
            case "log":
                Repository.log();
                break;
            case "checkout":
                Repository.checkout(args[1], args[2]);
                break;
            default:
                System.out.println("Invalid command. Please enter a valid command");
                break;
        }
    }
}
