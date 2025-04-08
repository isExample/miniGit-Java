package integration;

import base.BaseGitTest;
import base.MiniGitCore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [의존 명령어]: hash-object
 * [기능 설명]: OID를 통해 저장된 blob 객체의 내용을 출력함
 */
public class CatFileCommandTest extends BaseGitTest {
    @Test
    @DisplayName("Blob 객체를 OID로 정상적으로 조회할 수 있다.")
    void testCanReadBlobObjectByOid() throws Exception {
        // Given
        String filename = "cat.txt";
        String content = "MiniGit cat-file test";
        FileUtils.writeFile(workingDir, filename, content);
        String oid = MiniGitCore.hashObject(workingDir.resolve(filename).toString());

        // When
        byte[] blob = MiniGitCore.catFile(oid);

        // Then
        assertThat(blob).isNotNull();
        assertThat(new String(blob)).isEqualTo(content);
    }

    @Test
    @DisplayName("존재하지 않는 OID로 조회하면 null을 반환한다.")
    void testReturnsNullWhenOidDoesNotExist() {
        // Given
        String invalidOid = "abcdef1234567890abcdef1234567890abcdef12";

        // When
        byte[] blob = MiniGitCore.catFile(invalidOid);

        // Then
        assertThat(blob).isNull();
    }

    @Test
    @DisplayName("적절하지 않은 참조(tag 등)를 입력하면 null을 반환한다.")
    void testReturnsNullWhenInvalidRefIsPassed() {
        // Given
        String invalidTagOrRef = "non-existent-tag";

        // When
        String resolvedOid;
        try {
            resolvedOid = MiniGitCore.getOid(invalidTagOrRef);
        } catch (IllegalArgumentException e) {
            resolvedOid = null;
        }

        // Then
        assertThat(resolvedOid).isNull();
    }
}
