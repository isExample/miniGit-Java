package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String hashObject(String filePath) {
        try {
            // 파일 읽기
            byte[] data = Files.readAllBytes(Paths.get(filePath));

            // SHA-1 해싱
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest(data);
            String oid = bytesToHex(hash);

            // 저장 경로 설정
            Path objectPath = Paths.get(OBJECTS_DIR, oid);

            // 중복 저장 방지
            if(!Files.exists(objectPath)) {
                Files.write(objectPath, data);
            }

            return oid;
        } catch (IOException | NoSuchAlgorithmException e) {
            System.err.println("Error: Could not hash object.");
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getObject(String oid) {
        Path objectPath = Paths.get(OBJECTS_DIR, oid);
        try {
            return Files.readAllBytes(objectPath);
        } catch (IOException e) {
            System.err.println("Error: Object not found.");
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
