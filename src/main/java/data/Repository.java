package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Repository {
    private static final String GIT_DIR = ".miniGit";
    private static final String OBJECTS_DIR = GIT_DIR + "/objects";

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

    public static String hashObject(String filePath, String type) {
        try {
            // 파일 읽기
            byte[] data = Files.readAllBytes(Paths.get(filePath));

            // object = type + NULL byte + 실제 데이터
            byte[] typeBytes = (type + "\0").getBytes();
            byte[] objectData = new byte[typeBytes.length + data.length];

            System.arraycopy(typeBytes, 0, objectData, 0, typeBytes.length);
            System.arraycopy(data, 0, objectData, typeBytes.length, data.length);

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

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
