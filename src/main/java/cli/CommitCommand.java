package cli;

import base.MiniGitCore;
import picocli.CommandLine;

@CommandLine.Command(name = "commit", description = "Create a new commit")
public class CommitCommand implements Runnable {

    @CommandLine.Option(names = {"-m", "--message"}, description = "Commit message", required = true)
    private String message;

    @Override
    public void run() {
        String commitOid = MiniGitCore.commit(message);
        System.out.println(commitOid);
    }
}
