package integration;

import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class InitCommandTest {
    @TempDir
    Path tempDir;

    @Test
    @DisplayName(".miniGit과 objects 디렉터리를 생성한다.")
    void testInitCreatesMiniGitRepository() {
        // Given
        Path basePath = tempDir.resolve("repo");

        // When
        MiniGitCore.init(basePath.toString());

        // Then
        Path gitDir = basePath.resolve(".miniGit");
        Path objects = gitDir.resolve("objects");

        assertThat(gitDir).exists().isDirectory();
        assertThat(objects).exists().isDirectory();
    }
}
