package cli;

import base.MiniGitCore;
import picocli.CommandLine;

import java.util.Map;

@CommandLine.Command(name = "k", description = "show all refs")
public class KCommand implements Runnable {
    @Override
    public void run() {
        Map<String, String> refs = MiniGitCore.listRefs();
        for (Map.Entry<String, String> ref : refs.entrySet()) {
            System.out.println(ref.getKey() + " " + ref.getValue());
        }
    }
}
