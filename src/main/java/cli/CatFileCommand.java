package cli;

import base.MiniGitCore;
import cli.converter.OidConverter;
import picocli.CommandLine;

@CommandLine.Command(name = "cat-file", description = "Print stored object by OID")
public class CatFileCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Commit OID or ref", converter = OidConverter.class)
    private String oid;

    @Override
    public void run() {
        byte[] data = MiniGitCore.catFile(oid);
        if (data != null) {
            System.out.write(data, 0, data.length);
        } else {
            System.err.println("Error: Object not found.");
        }
    }
}
