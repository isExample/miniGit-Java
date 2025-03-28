package integration;

import data.Repository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class InitCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void testInitCreatesMiniGitRepository() {
        Path basePath = tempDir.resolve("repo");

        Repository.init(basePath.toString());

        Path gitDir = basePath.resolve(".miniGit");
        Path objects = gitDir.resolve("objects");

        assertThat(gitDir).exists().isDirectory();
        assertThat(objects).exists().isDirectory();
    }
}
