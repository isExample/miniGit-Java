package cli;

import base.MiniGitCore;
import picocli.CommandLine;

@CommandLine.Command(name = "checkout", description = "Checkout a commit and update HEAD")
public class CheckoutCommand implements Runnable{
    @CommandLine.Parameters(index = "0", description = "Commit OID to checkout")
    private String oid;

    @Override
    public void run() {
        MiniGitCore.checkout(oid);
    }
}
