package cli;

import data.Repository;
import picocli.CommandLine;

@CommandLine.Command(name = "init", description = "Initialize a new repository")
public class InitCommand implements Runnable {
    @Override
    public void run() {
        Repository.init();
        System.out.println("Initialized empty miniGit repository in " + Repository.getRepoPath());
    }
}
