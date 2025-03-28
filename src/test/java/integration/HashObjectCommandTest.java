package integration;

import base.BaseGitTest;
import base.MiniGitCore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;
import util.ObjectUtils;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class HashObjectCommandTest extends BaseGitTest {

    @Test
    @DisplayName("Blob 객체는 .miniGit/objects에 저장된다.")
    void testBlobFileIsStoredInObjectDirectory() throws Exception {
        // Given
        String filename = "file.txt";
        String content = "blob test-file";
        FileUtils.writeFile(workingDir, filename, content);

        // When
        String oid = MiniGitCore.hashObject(workingDir.resolve(filename).toString());

        // Then
        Path blobPath = workingDir.resolve(".miniGit/objects").resolve(oid);
        assertThat(blobPath).exists();
    }

    @Test
    @DisplayName("반환된 OID는 SHA-1의 해시값과 일치한다.")
    void testReturnedOidMatchesExpectedHash() throws Exception {
        // Given
        String filename = "file.txt";
        String content = "blob test-file";
        FileUtils.writeFile(workingDir, filename, content);

        // When
        String oid = MiniGitCore.hashObject(workingDir.resolve(filename).toString());

        // Then
        byte[] object = ObjectUtils.buildFormat(content.getBytes(), "blob");
        String expectedOid = ObjectUtils.hashObject(object);
        assertThat(oid).isEqualTo(expectedOid);
    }

    @Test
    @DisplayName("존재하지 않는 파일 경로를 입력하면 null을 반환한다.")
    void testHashObjectReturnsNullOnInvalidPath() {
        // Given
        Path invalidPath = workingDir.resolve("non-existent.txt");

        // When
        String oid = MiniGitCore.hashObject(invalidPath.toString());

        // Then
        assertThat(oid).isNull();
    }
}
