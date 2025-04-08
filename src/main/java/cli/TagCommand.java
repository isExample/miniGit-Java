package cli;

import base.MiniGitCore;
import cli.converter.OidConverter;
import data.Repository;
import picocli.CommandLine;

@CommandLine.Command(name = "tag", description = "Create a tag for a commit")
public class TagCommand implements Runnable {
    @CommandLine.Parameters(index = "0", description = "Tag name")
    private String name;

    @CommandLine.Parameters(index = "1", description = "Commit OID or ref", defaultValue = "@", converter = OidConverter.class)
    private String oid;

    @Override
    public void run() {
        try {
            MiniGitCore.createTag(name, oid);
            System.out.println("Tag '" + name + "' created at " + oid);
        } catch (IllegalStateException e) {
            System.err.println("fatal: " + e.getMessage());
        }
    }
}
