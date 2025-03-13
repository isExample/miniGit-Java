package cli;

import base.MiniGitCore;
import picocli.CommandLine;

@CommandLine.Command(name = "log", description = "Show commit history")
public class LogCommand implements Runnable {
    @Override
    public void run() {
        MiniGitCore.log();
    }
}
