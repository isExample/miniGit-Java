package cli;

import base.MiniGitCore;
import data.Repository;
import picocli.CommandLine;

@CommandLine.Command(name = "tag", description = "Create a tag for a commit")
public class TagCommand implements Runnable{
    @CommandLine.Parameters(index = "0", description = "Tag name")
    private String name;

    @CommandLine.Parameters(index = "1", description = "Commit OID", arity = "0..1")
    private String oid;

    @Override
    public void run(){
        String targetOid = (oid != null) ? oid : Repository.getHEAD();
        MiniGitCore.createTag(name, targetOid);
    }
}
