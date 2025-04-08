package integration;

import base.BaseGitTest;
import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckoutCommandTest extends BaseGitTest {

    @Test
    @DisplayName("Commit을 Checkout하면 해당 commit 시점의 파일 내용이 복원된다.")
    void testCheckoutRestoresFiles() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "first");
        String commit1 = MiniGitCore.commit("first commit");

        FileUtils.writeFile(workingDir, "file.txt", "second");
        String commit2 = MiniGitCore.commit("second commit");

        // When
        MiniGitCore.checkout(commit1);

        // Then
        Path file = workingDir.resolve("file.txt");

        assertThat(file).exists();
        assertThat(Files.readString(file)).isEqualTo("first");
    }

    @Test
    @DisplayName("브랜치를 checkout하면 해당 브랜치가 가리키는 commit 상태로 복원된다.")
    void testCheckoutWithBranchName() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "first");
        String baseCommit = MiniGitCore.commit("first commit");

        MiniGitCore.createBranch("first-branch", baseCommit);

        FileUtils.writeFile(workingDir, "file.txt", "second");
        MiniGitCore.commit("second commit");

        // When
        MiniGitCore.checkout("first-branch");

        // Then
        Path file = workingDir.resolve("file.txt");
        assertThat(file).exists();
        assertThat(Files.readString(file)).isEqualTo("first");
    }
}

