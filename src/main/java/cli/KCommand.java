package cli;

import base.MiniGitCore;
import picocli.CommandLine;

@CommandLine.Command(name = "k", description = "show all refs")
public class KCommand implements Runnable {
    @Override
    public void run() {
        // MiniGitCore 메서드
    }
}
