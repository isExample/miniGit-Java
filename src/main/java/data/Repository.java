package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Repository {
    private static final String GIT_DIR = ".miniGit";

    public static void init() {
        Path gitDirPath = Paths.get(GIT_DIR);
        try {
            Files.createDirectories(gitDirPath);
            System.out.println("Created .miniGit directory at " + gitDirPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error: Could not create .miniGit directory.");
            e.printStackTrace();
        }
    }

    public static String getRepoPath() {
        return Paths.get(GIT_DIR).toAbsolutePath().toString();
    }

}
