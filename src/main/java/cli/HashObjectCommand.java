package cli;

import picocli.CommandLine;
import data.Repository;

@CommandLine.Command(name = "hash-object", description = "Store file content and return SHA-1 hash")
public class HashObjectCommand implements Runnable {
    @CommandLine.Parameters(index = "0", description = "File to hash")
    private String filePath;

    @Override
    public void run() {
        String oid = Repository.hashObject(filePath);
        if (oid != null) {
            System.out.println(oid);
        } else {
            System.err.println("Error: Could not hash file.");
        }
    }
}
