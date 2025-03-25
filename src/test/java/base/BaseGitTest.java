package base;

import data.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public abstract class BaseGitTest {
    @TempDir
    protected Path tempDir;       // 임시 디렉터리
    protected Path workingDir;    // 실제 작업 디렉터리

    @BeforeEach
    void initRepo() {
        workingDir = tempDir.resolve("repo");
        System.setProperty("user.dir", workingDir.toString());  // 작업 디렉토리를 강제로 설정
        Repository.init();
    }
}
