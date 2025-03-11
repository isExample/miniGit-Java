package cli;

import base.MiniGitCore;
import data.Repository;
import picocli.CommandLine;

@CommandLine.Command(name = "init", description = "Initialize a new miniGit repository")
public class InitCommand implements Runnable {
    @Override
    public void run() {
        MiniGitCore.init();
    }
}
