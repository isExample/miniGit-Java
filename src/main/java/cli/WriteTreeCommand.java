package cli;

import data.MiniGitCore;
import picocli.CommandLine;

@CommandLine.Command(name = "write-tree", description = "List all files and directories recursively")
public class WriteTreeCommand implements Runnable {
    @Override
    public void run() {
        MiniGitCore.writeTree("."); // 현재 디렉터리를 기준으로 탐색
    }
}
