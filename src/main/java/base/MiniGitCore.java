package base;

import data.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class MiniGitCore {
    public static void init() {
        Repository.init();
    }

    public static String hashObject(String filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            return Repository.hashObject(fileContent, "blob");
        } catch (IOException e) {
            System.err.println("Error: Could not read file " + filePath);
            return null;
        }
    }

    public static byte[] catFile(String oid) {
        return Repository.getObject(oid, null);
    }

    public static String writeTree(String directory) {
        Path rootPath = Paths.get(directory);
        List<String> entries = new ArrayList<>();
        try {
            Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (isIgnored(file)) {
                        return FileVisitResult.CONTINUE;
                    }
                    try {
                        String oid = Repository.hashObject(Files.readAllBytes(file), "blob");
                        entries.add(String.format("blob %s %s", oid, file.getFileName()));
                    } catch (IOException e) {
                        System.err.println("Error: Could not read file " + file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (isIgnored(dir) || dir.equals(rootPath)) {
                        return FileVisitResult.CONTINUE;
                    }
                    String oid = writeTree(dir.toString());
                    entries.add(String.format("tree %s %s", oid, dir.getFileName()));
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });

            // tree object 생성 및 저장
            Collections.sort(entries);
            String treeData = String.join("\n", entries) + "\n";
            return Repository.hashObject(treeData, "tree");

        } catch (IOException e) {
            System.err.println("Error: Could not traverse directory.");
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> iterTreeEntries(String treeOid) {
        byte[] treeData = Repository.getObject(treeOid, "tree");
        if (treeData == null) {
            return Collections.emptyList();
        }
        String content = new String(treeData);
        return Arrays.asList(content.split("\n"));
    }

    private static boolean isIgnored(Path path) {
        return path.toAbsolutePath().toString().contains(".miniGit");
    }
}
