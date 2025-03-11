package cli;

import data.MiniGitCore;
import picocli.CommandLine;

@CommandLine.Command(name = "write-tree", description = "Convert directory to tree object and store")
public class WriteTreeCommand implements Runnable {
    @Override
    public void run() {
        String treeOid = MiniGitCore.writeTree("."); // 현재 디렉터리를 기준으로 탐색
        if (treeOid != null) {
            System.out.println(treeOid);
        } else {
            System.err.println("Error: Failed to write tree.");
        }
    }
}
