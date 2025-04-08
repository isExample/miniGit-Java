package integration;

import base.BaseGitTest;
import base.MiniGitCore;
import base.RefValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BranchCommandTest extends BaseGitTest {

    @Test
    @DisplayName("브랜치 시작점을 지정하지 않으면 HEAD를 기준으로 생성된다.")
    void testBranchIsCreatedAtHEAD() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "hello file!");
        String headCommit = MiniGitCore.commit("initial commit");

        // When
        MiniGitCore.createBranch("new-branch", headCommit);

        // Then
        Map<String, RefValue> refs = MiniGitCore.listRefs();
        RefValue newBranch = refs.get("refs/heads/new-branch");

        assertThat(newBranch).isNotNull();
        assertThat(newBranch.value()).isEqualTo(headCommit);
    }

    @Test
    @DisplayName("브랜치 시작점을 지정하면 해당 commit을 기준으로 브랜치가 생성된다.")
    void testBranchIsCreatedAtGivenOid() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file1.txt", "hello file1!");
        String firstCommit = MiniGitCore.commit("first commit");

        FileUtils.writeFile(workingDir, "file2.txt", "hello file2!");
        MiniGitCore.commit("second commit");

        // When
        MiniGitCore.createBranch("new-branch", firstCommit);

        // Then
        Map<String, RefValue> refs = MiniGitCore.listRefs();
        RefValue newBranch = refs.get("refs/heads/new-branch");

        assertThat(newBranch).isNotNull();
        assertThat(newBranch.value()).isEqualTo(firstCommit);
    }

    @Test
    @DisplayName("동일한 브랜치 이름이 존재하면 예외가 발생한다.")
    void creatingBranchWithExistingNameThrowsException() {
        // Given
        String branchName = "feature";
        String commitOid = MiniGitCore.commit("first commit");
        MiniGitCore.createBranch(branchName, commitOid);

        // When & Then
        assertThatThrownBy(() -> MiniGitCore.createBranch(branchName, commitOid))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }
}

