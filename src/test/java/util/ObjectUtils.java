package util;

import java.security.MessageDigest;

public class ObjectUtils {

    public static byte[] buildFormat(byte[] content, String type) {
        byte[] typeBytes = (type + "\0").getBytes();
        byte[] objectData = new byte[typeBytes.length + content.length];

        System.arraycopy(typeBytes, 0, objectData, 0, typeBytes.length);
        System.arraycopy(content, 0, objectData, typeBytes.length, content.length);

        return objectData;
    }

    public static String hashObject(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(data);

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
