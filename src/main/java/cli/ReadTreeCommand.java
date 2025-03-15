package cli;

import base.MiniGitCore;
import cli.converter.OidConverter;
import picocli.CommandLine;

@CommandLine.Command(name = "read-tree", description = "Restore working directory from a tree object")
public class ReadTreeCommand implements Runnable {

    @CommandLine.Parameters(index = "0", description = "Commit OID or ref", converter = OidConverter.class)
    private String treeOid;

    @Override
    public void run() {
        MiniGitCore.readTree(treeOid);
    }
}
