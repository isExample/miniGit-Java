package cli;

import base.MiniGitCore;
import cli.converter.OidConverter;
import data.Repository;
import picocli.CommandLine;

@CommandLine.Command(name = "log", description = "Show commit history")
public class LogCommand implements Runnable {
    @CommandLine.Parameters(index = "0", description = "Commit OID or ref", defaultValue = "@", converter = OidConverter.class)
    private String oid;

    @Override
    public void run() {
        MiniGitCore.log(oid);
    }
}
