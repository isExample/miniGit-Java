package integration;

import base.BaseGitTest;
import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class ReadTreeCommandTest extends BaseGitTest {

    @Test
    @DisplayName("Tree 객체를 읽으면 동일한 디렉터리 구조가 복원된다.")
    void testReadTreeRestoresFileStructure() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file1.txt", "file1 content");
        FileUtils.writeFile(workingDir.resolve("dir"), "file2.txt", "file2 content");
        String treeOid = MiniGitCore.writeTree(workingDir.toString());

        FileUtils.clearDirectory(workingDir);

        // When
        MiniGitCore.readTree(treeOid);

        // Then
        Path file1 = workingDir.resolve("file1.txt");
        Path file2 = workingDir.resolve("dir/file2.txt");

        assertThat(file1).exists();
        assertThat(file2).exists();
        assertThat(Files.readString(file1)).isEqualTo("file1 content");
        assertThat(Files.readString(file2)).isEqualTo("file2 content");
    }

    @Test
    @DisplayName("Tree 객체에 포함되지 않은 기존 파일은 제거된다.")
    void testReadTreeRemovesUnexpectedFiles() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "tracked.txt", "tracked file content");
        String treeOid = MiniGitCore.writeTree(workingDir.toString());

        FileUtils.writeFile(workingDir, "untracked.txt", "untracked file content");

        // When
        MiniGitCore.readTree(treeOid);

        // Then
        Path tracked = workingDir.resolve("tracked.txt");
        Path untracked = workingDir.resolve("untracked.txt");

        assertThat(tracked).exists();
        assertThat(untracked).doesNotExist();
    }

    @Test
    @DisplayName("Tree 객체에 포함되지 않은 기존 디렉터리는 제거된다.")
    void testReadTreeRemovesUnexpectedDirectory() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "tracked.txt", "tracked file content");
        String treeOid = MiniGitCore.writeTree(workingDir.toString());

        Path untrackedDir = workingDir.resolve("untracked-dir");
        FileUtils.writeFile(untrackedDir, "untracked-file.txt", "untracked file content");

        // When
        MiniGitCore.readTree(treeOid);

        // Then
        assertThat(untrackedDir).doesNotExist();
    }

    @Test
    @DisplayName("Tree 객체에 포함된 파일에 변경사항이 있다면 덮어쓰기하여 원래 내용을 복원한다.")
    void testReadTreeOverwritesModifiedFiles() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "original content");
        String treeOid = MiniGitCore.writeTree(workingDir.toString());

        FileUtils.writeFile(workingDir, "file.txt", "modified content");

        // When
        MiniGitCore.readTree(treeOid);

        // Then
        String restored = Files.readString(workingDir.resolve("file.txt"));
        assertThat(restored).isEqualTo("original content");
    }
}
