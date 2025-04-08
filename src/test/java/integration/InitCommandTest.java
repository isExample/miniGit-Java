package integration;

import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [의존 명령어]: 없음(최초 명령어)
 * [기능 설명]: miniGit 저장소(.miniGit 디렉터리) 초기화
 * 참고: 다른 모든 명령어의 전제
 */
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
