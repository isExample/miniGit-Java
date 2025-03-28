package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static void writeFile(Path dir, String name, String content) throws IOException {
        Path file = dir.resolve(name);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }

    public static String readFile(Path dir, String name) throws IOException {
        Path file = dir.resolve(name);
        return Files.readString(file);
    }

    public static boolean fileExists(Path dir, String name) {
        return Files.exists(dir.resolve(name));
    }

    public static void deleteFile(Path dir, String name) throws IOException {
        Files.deleteIfExists(dir.resolve(name));
    }
}
