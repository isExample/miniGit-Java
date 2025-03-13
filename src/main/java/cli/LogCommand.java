package cli;

import base.MiniGitCore;
import data.Repository;
import picocli.CommandLine;

@CommandLine.Command(name = "log", description = "Show commit history")
public class LogCommand implements Runnable {
    @CommandLine.Parameters(index = "0", description = "Starting commit OID", defaultValue = "")
    private String oid;

    @Override
    public void run() {
        MiniGitCore.log(oid.isEmpty() ? Repository.getHEAD() : oid);
    }
}
