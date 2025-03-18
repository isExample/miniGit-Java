package cli;

import base.Commit;
import base.MiniGitCore;
import picocli.CommandLine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

@CommandLine.Command(name = "k", description = "show all refs")
public class KCommand implements Runnable {
    @Override
    public void run() {
        Map<String, String> refs = MiniGitCore.listRefs();
        System.out.println("--Refs--");
        for (Map.Entry<String, String> ref : refs.entrySet()) {
            System.out.println(ref.getKey() + " " + ref.getValue());
        }

        List<String> commits = MiniGitCore.listCommits(new HashSet<>(refs.values()));
        System.out.println("\n--Commits--");
        for (String oid : commits) {
            System.out.println("Commit: " + oid);
        }
    }
}
