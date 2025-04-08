package integration;

import base.BaseGitTest;
import base.Commit;
import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [의존 명령어]: write-tree
 * [기능 설명]: commit 객체 생성 및 HEAD 업데이트
 */
public class CommitCommandTest extends BaseGitTest {

    @Test
    @DisplayName("Commit 객체를 생성하여 .miniGit/objects에 저장한다.")
    void testCommitCreatesCommitObjectInObjectsDirectory() throws IOException {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "hello file!");

        // When
        String oid = MiniGitCore.commit("initial commit");

        // Then
        Path objectFile = workingDir.resolve(".miniGit/objects").resolve(oid);
        assertThat(objectFile).exists();
    }

    @Test
    @DisplayName("Commit 객체는 정확한 메시지를 포함한다.")
    void testCommitObjectIncludesExactMessage() throws IOException {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "hello file!");

        // When
        String oid = MiniGitCore.commit("initial commit");

        // Then
        Commit commit = MiniGitCore.getCommit(oid);
        assertThat(commit.message).isEqualTo("initial commit");
    }

    @Test
    @DisplayName("Commit 객체는 정확한 Tree OID를 포함한다.")
    void testCommitObjectIncludesExactTreeOid() throws IOException {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "hello file!");
        String expectedTreeOid = MiniGitCore.writeTree(workingDir.toString());

        // When
        String commitOid = MiniGitCore.commit("initial commit");

        // Then
        Commit commit = MiniGitCore.getCommit(commitOid);
        assertThat(commit.tree).isEqualTo(expectedTreeOid);
    }

    @Test
    @DisplayName("Commit 이후 HEAD는 새로운 커밋을 가리킨다.")
    void testHeadIsUpdatedToNewCommit() throws IOException {
        // Given
        FileUtils.writeFile(workingDir, "file1.txt", "hello file1!");
        String firstCommit = MiniGitCore.commit("initial commit");
        String prevHeadOid = MiniGitCore.getOid("HEAD");

        // When
        FileUtils.writeFile(workingDir, "file2.txt", "hello file2!");
        String secondCommit = MiniGitCore.commit("second commit");
        String currHeadOid = MiniGitCore.getOid("HEAD");

        // Then
        assertThat(prevHeadOid).isEqualTo(firstCommit);
        assertThat(currHeadOid).isEqualTo(secondCommit);
        assertThat(currHeadOid).isNotEqualTo(prevHeadOid);
    }
}
