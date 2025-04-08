package integration;

import base.BaseGitTest;
import base.MiniGitCore;
import base.RefValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TagCommandTest extends BaseGitTest {

    @Test
    @DisplayName("지정한 이름으로 commit에 tag를 생성한다.")
    void testTagIsCreatedForGivenCommit() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "hello file!");
        String commitOid = MiniGitCore.commit("initial commit");

        // When
        MiniGitCore.createTag("v1.0", commitOid);

        // Then
        RefValue tagRef = MiniGitCore.listRefs().get("refs/tags/v1.0");

        assertThat(tagRef).isNotNull();
        assertThat(tagRef.symbolic()).isFalse();
        assertThat(tagRef.value()).isEqualTo(commitOid);
    }

    @Test
    @DisplayName("존재하지 않는 OID에 tag를 생성하면 예외가 발생한다.")
    void testCreatingTagWithInvalidOidThrows() {
        // Given
        String invalidOid = "abcdef1234567890abcdef1234567890abcdef12";

        // When & Then
        assertThatThrownBy(() -> MiniGitCore.createTag("v1.0", invalidOid))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Commit not found");
    }

    @Test
    @DisplayName("이미 존재하는 tag 이름으로 생성하면 예외가 발생한다.")
    void testCreatingDuplicateTagThrows() throws Exception {
        // Given
        FileUtils.writeFile(workingDir, "file.txt", "hello file!");
        String commitOid = MiniGitCore.commit("initial commit");

        MiniGitCore.createTag("v1.0", commitOid);

        // When & Then
        assertThatThrownBy(() -> MiniGitCore.createTag("v1.0", commitOid))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }
}
