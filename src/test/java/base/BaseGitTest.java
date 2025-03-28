package base;

import data.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public abstract class BaseGitTest {
    @TempDir
    protected Path tempDir;
    protected Path workingDir;

    @BeforeEach
    void initRepo() {
        workingDir = tempDir.resolve("repo");
        MiniGitCore.init(workingDir.toString());
    }
}
