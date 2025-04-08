package cli;

import base.MiniGitCore;
import cli.converter.OidConverter;
import picocli.CommandLine;

@CommandLine.Command(name = "branch", description = "Create a new branch")
public class BranchCommand implements Runnable {
    @CommandLine.Parameters(index = "0", description = "Branch name")
    private String name;

    @CommandLine.Parameters(index = "1", description = "Start point", defaultValue = "@", converter = OidConverter.class)
    private String startPoint;

    @Override
    public void run() {
        try {
            MiniGitCore.createBranch(name, startPoint);
            System.out.println("Branch " + name + " created at " + startPoint);
        } catch (IllegalStateException e) {
            System.err.println("fatal: " + e.getMessage());
        }
    }
}
