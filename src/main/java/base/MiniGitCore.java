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

    private static Map<String, String> getTree(String treeOid, String basePath) {
        Map<String, String> result = new HashMap<>();
        List<String> entries = iterTreeEntries(treeOid);
        for (String entry : entries) {
            String[] parts = entry.split(" ", 3);
            if (parts.length != 3) {
                System.err.println("Invalid tree entry: " + entry);
                continue;
            }
            String type = parts[0];
            String oid = parts[1];
            String name = parts[2];
            String fullPath = basePath + name;

            if (type.equals("blob")) {
                result.put(fullPath, oid);
            } else if (type.equals("tree")) {
                result.putAll(getTree(oid, fullPath + "/"));
            } else {
                throw new RuntimeException("Unknown tree entry type: " + type);
            }
        }
        return result;
    }

    private static List<String> iterTreeEntries(String treeOid) {
        byte[] treeData = Repository.getObject(treeOid, "tree");
        if (treeData == null) {
            return Collections.emptyList();
        }
        String content = new String(treeData);
        return Arrays.asList(content.split("\n"));
    }

    // TODO: 무시할 파일 목록을 별도 설정 파일로 분리 (ex: .minigitignore)
    private static final Set<String> IGNORED_PATHS = new HashSet<>(Arrays.asList(
            ".miniGit",
            ".git",
            "build",
            ".gradle",
            "gradle",
            "src",
            ".idea",
            ".github",
            "scripts"
    ));

    private static boolean isIgnored(Path path) {
        String absolutePath = path.toAbsolutePath().toString();
        for (String ignored : IGNORED_PATHS) {
            if (absolutePath.contains(ignored)) {
                return true;
            }
        }
        return false;
    }
}
