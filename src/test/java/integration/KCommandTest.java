package integration;

import base.BaseGitTest;
import base.MiniGitCore;
import base.RefValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.FileUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [의존 명령어]: branch, tag
 * [기능 설명]: 모든 refs와 관련 commit들을 출력
 * 참고: 결과 검증을 위해 checkout 명령어 기능을 보조적으로 사용
 */
public class KCommandTest extends BaseGitTest {

    @Test
    @DisplayName("모든 refs와 commit을 출력한다.")
    void testKDisplaysAllRefsAndCommits() throws IOException {
        // Given
        FileUtils.writeFile(workingDir, "file1.txt", "hello file1!");
        String commitOid1 = MiniGitCore.commit("initial commit");

        MiniGitCore.createTag("v1.0", commitOid1);
        MiniGitCore.createBranch("feature", commitOid1);

        MiniGitCore.checkout("feature");
        FileUtils.writeFile(workingDir, "file2.txt", "hello file2!");
        String commitOid2 = MiniGitCore.commit("feature commit");

        // When
        Map<String, RefValue> refs = MiniGitCore.listRefs();
        Set<String> refTargets = new HashSet<>();
        refs.values().forEach(ref -> {
            if (!ref.symbolic()) {
                refTargets.add(ref.value());
            }
        });
        List<String> commits = MiniGitCore.listCommits(refTargets);

        // Then: 모든 ref와 commit이 추적되는지 확인
        assertThat(refs).containsKeys("HEAD", "refs/heads/feature", "refs/heads/master", "refs/tags/v1.0");
        assertThat(commits).contains(commitOid1, commitOid2);
    }
}
