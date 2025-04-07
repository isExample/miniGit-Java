package integration;

import base.BaseGitTest;
import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WriteTreeCommandTest extends BaseGitTest {

    @Test
    @DisplayName("디렉터리를 Tree 객체로 변환하여 저장하면 OID를 반환한다.")
    void testWriteTreeReturnsOid() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file1.txt", "file1 content");
        FileUtils.writeFile(workingDir.resolve("dir"), "file2.txt", "file2 content");

        // When
        String treeOid = MiniGitCore.writeTree(workingDir.toString());

        // Then
        assertThat(treeOid).isNotNull();
        assertThat(treeOid).hasSize(40); // SHA-1 길이
    }

    @Test
    @DisplayName("Tree 객체에는 파일과 하위 디렉터리 정보가 포함되어 있다.")
    void testTreeObjectContainsFilesAndSubdirs() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "file content");
        FileUtils.writeFile(workingDir.resolve("dirA"), "a.txt", "A file content");
        FileUtils.writeFile(workingDir.resolve("dirB"), "b.txt", "B file content");

        // When
        String treeOid = MiniGitCore.writeTree(workingDir.toString());

        byte[] treeData = MiniGitCore.catFile(treeOid);
        String treeText = new String(treeData);
        List<String> entries = List.of(treeText.split("\n"));

        // Then
        assertThat(entries).anyMatch(e -> e.contains("blob") && e.contains("file.txt"));
        assertThat(entries).anyMatch(e -> e.contains("tree") && e.contains("dirA"));
        assertThat(entries).anyMatch(e -> e.contains("tree") && e.contains("dirB"));
    }

    @Test
    @DisplayName("비어 있는 디렉터리는 빈 Tree 객체로 처리된다.")
    void testEmptyDirectoryCreatesEmptyTreeObject() {
        // Given: workingDir는 비어 있는 상태

        // When
        String treeOid = MiniGitCore.writeTree(workingDir.toString());

        byte[] treeData = MiniGitCore.catFile(treeOid);
        String treeText = new String(treeData);

        // Then
        assertThat(treeOid).isNotNull();
        assertThat(treeOid).hasSize(40); // SHA-1 길이
        assertThat(treeText.trim()).isEmpty();
    }
}
