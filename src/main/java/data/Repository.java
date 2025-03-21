package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Repository {
    private static final String GIT_DIR = ".miniGit";
    private static final String OBJECTS_DIR = GIT_DIR + "/objects";
    private static final String REFS_DIR = GIT_DIR + "/refs";

    public static void init() {
        try {
            Files.createDirectories(Paths.get(GIT_DIR));
            Files.createDirectories(Paths.get(OBJECTS_DIR));
            System.out.println("Initialized empty miniGit repository in " + Paths.get("").toAbsolutePath() + "/" + GIT_DIR);
        } catch (IOException e) {
            System.err.println("Error: Could not create .miniGit directory.");
            e.printStackTrace();
        }
    }

    public static String getRepoPath() {
        return Paths.get(GIT_DIR).toAbsolutePath().toString();
    }

    public static String hashObject(String content, String type) {
        return hashObject(content.getBytes(), type);
    }

    public static String hashObject(byte[] content, String type) {
        try {
            // object = type + NULL byte + 실제 데이터
            byte[] typeBytes = (type + "\0").getBytes();
            byte[] objectData = new byte[typeBytes.length + content.length];

            System.arraycopy(typeBytes, 0, objectData, 0, typeBytes.length);
            System.arraycopy(content, 0, objectData, typeBytes.length, content.length);

            // SHA-1 해싱
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(objectData);
            String oid = bytesToHex(hash);

            // 저장 경로 설정
            Path objectPath = Paths.get(OBJECTS_DIR, oid);

            // 중복 저장 방지
            if (!Files.exists(objectPath)) {
                Files.write(objectPath, objectData);
            }

            return oid;
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error: Could not hash object.");
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getObject(String oid, String expectedType) {
        Path objectPath = Paths.get(OBJECTS_DIR, oid);
        try {
            byte[] objectData = Files.readAllBytes(objectPath);

            // 데이터 분할 (type + NULL byte + 실제 데이터)
            int nullIndex = -1;
            for (int i = 0; i < objectData.length; i++) {
                if (objectData[i] == 0) {
                    nullIndex = i;
                    break;
                }
            }

            if (nullIndex == -1) {
                throw new IOException("Invalid object format");
            }

            String type = new String(objectData, 0, nullIndex);
            byte[] content = Arrays.copyOfRange(objectData, nullIndex + 1, objectData.length);

            if (expectedType != null && !expectedType.equals(type)) {
                throw new IOException("Expected " + expectedType + ", got " + type);
            }

            return content;
        } catch (IOException e) {
            System.err.println("Error: Object not found or Invalid format.");
            return null;
        }
    }

    public static void updateRef(String ref, String oid) {
        try {
            Path refPath = Paths.get(GIT_DIR, ref);
            Files.createDirectories(refPath.getParent());
            Files.writeString(refPath, oid.trim());
        } catch (IOException e) {
            System.out.println("Error: Could not write ref " + ref);
            e.printStackTrace();
        }
    }

    public static String getRef(String ref) {
        Path refPath = Paths.get(GIT_DIR, ref);
        if (!Files.exists(refPath) || Files.isDirectory(refPath)) {
            return null;
        }
        try {
            return Files.readString(refPath).trim();
        } catch (IOException e) {
            System.out.println("Error: Could not read ref " + ref);
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> iterRefs() {
        Map<String, String> refs = new HashMap<>();
        String head = getRef("HEAD");
        if (head != null) {
            refs.put("HEAD", head);
        }

        Path refsPath = Paths.get(REFS_DIR);
        if (Files.exists(refsPath)) {
            try {
                Files.walk(refsPath)
                        .filter(Files::isRegularFile) // 파일만 찾음
                        .forEach(refFile -> {
                            String refName = refsPath.relativize(refFile).toString();
                            String refValue = getRef("refs/" + refName);
                            if (refValue != null) {
                                refs.put("refs/" + refName, refValue);
                            }
                        });
            } catch (IOException e) {
                System.err.println("Error: Could not read refs.");
                e.printStackTrace();
            }
        }
        return refs;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
