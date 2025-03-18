package cli;

import base.Commit;
import base.MiniGitCore;
import cli.converter.OidConverter;
import data.Repository;
import picocli.CommandLine;

import java.util.List;
import java.util.Set;

@CommandLine.Command(name = "log", description = "Show commit history")
public class LogCommand implements Runnable {
    @CommandLine.Parameters(index = "0", description = "Commit OID or ref", defaultValue = "@", converter = OidConverter.class)
    private String oid;

    @Override
    public void run() {
        List<String> commits = MiniGitCore.listCommits(Set.of(oid));
        for (String commitOid : commits) {
            Commit commit = MiniGitCore.getCommit(commitOid);
            System.out.println("commit " + commitOid);
            System.out.println("    " + commit.message.replace("\n", "\n    "));
            System.out.println();
        }
    }
}
