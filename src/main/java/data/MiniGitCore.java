package data;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class MiniGitCore {
    public static void init() {
        System.out.println("Initialized empty miniGit repository.");
    }

    public static void writeTree(String directory) {
        Path rootPath = Paths.get(directory);
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    System.out.println(file.toAbsolutePath());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    System.out.println(dir.toAbsolutePath());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error: Could not traverse directory.");
            e.printStackTrace();
        }
    }
}
