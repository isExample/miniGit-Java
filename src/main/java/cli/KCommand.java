package cli;

import base.Commit;
import base.MiniGitCore;
import base.RefValue;
import picocli.CommandLine;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CommandLine.Command(name = "k", description = "show all refs")
public class KCommand implements Runnable {
    @Override
    public void run() {
        Map<String, RefValue> refs = MiniGitCore.listRefs();
        Set<String> oids = new HashSet<>();
        System.out.println("--Refs--");
        for (Map.Entry<String, RefValue> ref : refs.entrySet()) {
            System.out.println(ref.getKey() + " " + ref.getValue().value());
            if (!ref.getValue().symbolic()) {
                oids.add(ref.getValue().value());
            }
        }

        List<String> commits = MiniGitCore.listCommits(oids);
        System.out.println("\n--Commits--");
        for (String oid : commits) {
            System.out.println("Commit: " + oid);
        }
    }
}
