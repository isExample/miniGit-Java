package cli;

import base.MiniGitCore;
import cli.converter.OidConverter;
import picocli.CommandLine;

@CommandLine.Command(name = "checkout", description = "Checkout a commit and update HEAD")
public class CheckoutCommand implements Runnable{
    @CommandLine.Parameters(index = "0", description = "Commit OID or ref")
    private String oid;

    @Override
    public void run() {
        MiniGitCore.checkout(oid);
    }
}
