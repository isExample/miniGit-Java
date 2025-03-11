package cli;

import data.Repository;
import picocli.CommandLine;

@CommandLine.Command(name = "cat-file", description = "Print stored object by OID")
public class CatFileCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Object ID to retrieve")
    private String oid;

    @Override
    public void run() {
        byte[] data = Repository.getObject(oid);
        if (data != null) {
            System.out.write(data, 0, data.length);
        } else {
            System.err.println("Error: Object not found.");
        }
    }
}
