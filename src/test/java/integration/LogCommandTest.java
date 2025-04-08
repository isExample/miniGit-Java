package integration;

import base.BaseGitTest;
import base.Commit;
import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class LogCommandTest extends BaseGitTest {
    @Test
    @DisplayName("단일 commit의 log는 해당 메시지 하나만 반환한다.")
    void testSingleCommitLog() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "hello file!");
        String commitOid = MiniGitCore.commit("initial commit");
        String headOid = MiniGitCore.getOid("HEAD");

        // When
        List<String> logOids = MiniGitCore.listCommits(Set.of(headOid));

        // Then
        Commit commit = MiniGitCore.getCommit(commitOid);

        assertThat(logOids).containsExactly(commitOid);
        assertThat(commit.message).isEqualTo("initial commit");
    }

    @Test
    @DisplayName("여러 커밋의 로그는 최신 커밋부터 역순으로 반환된다.")
    void testMultipleCommitsLog() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file1.txt", "content1");
        String commit1 = MiniGitCore.commit("first commit");

        FileUtils.writeFile(workingDir, "file2.txt", "content2");
        String commit2 = MiniGitCore.commit("second commit");

        FileUtils.writeFile(workingDir, "file3.txt", "content3");
        String commit3 = MiniGitCore.commit("third commit");

        // When
        String headOid = MiniGitCore.getOid("HEAD");
        List<String> logOids = MiniGitCore.listCommits(Set.of(headOid));

        // Then
        assertThat(logOids).containsExactly(commit3, commit2, commit1);
    }

    @Test
    @DisplayName("특정 커밋을 기준으로 로그를 조회할 수 있다.")
    void testLogFromSpecificCommit() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file1.txt", "content1");
        String commit1 = MiniGitCore.commit("first commit");

        FileUtils.writeFile(workingDir, "file2.txt", "content2");
        String commit2 = MiniGitCore.commit("second commit");

        FileUtils.writeFile(workingDir, "file3.txt", "content3");
        MiniGitCore.commit("third commit");

        // When: commit2를 기준으로 로그를 요청
        List<String> logOids = MiniGitCore.listCommits(Set.of(commit2));

        // Then
        assertThat(logOids).containsExactly(commit2, commit1);
    }
}
