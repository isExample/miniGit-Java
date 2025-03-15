package base;

import data.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

    public static void readTree(String treeOid) {
        clearWorkingDirectory();
        Map<String, String> tree = getTree(treeOid, "./");

        for (Map.Entry<String, String> entry : tree.entrySet()) {
            String path = entry.getKey();
            String oid = entry.getValue();

            try {
                Files.createDirectories(Paths.get(path).getParent());
                Files.write(Paths.get(path), Repository.getObject(oid, "blob"));
            } catch (IOException e) {
                System.err.println("Error: Could not write file " + path);
            }
        }
    }

    public static String commit(String message) {
        String treeOid = writeTree(".");
        String parentOid = Repository.getRef("HEAD");
        String commitOid = commitObject(treeOid, parentOid, message);
        Repository.updateRef("HEAD", commitOid);
        return commitOid;
    }

    public static void log(String startOid) {
        String oid = startOid;
        if (startOid == null || startOid.isEmpty()) {
            oid = Repository.getRef("HEAD");
        }
        while (oid != null) {
            Commit commit = getCommit(oid);

            System.out.println("commit " + oid);
            System.out.println("    " + commit.message.replace("\n", "\n    "));
            System.out.println();

            oid = commit.parent;
        }
    }

    public static Commit getCommit(String oid) {
        byte[] commitData = Repository.getObject(oid, "commit");
        if (commitData == null) {
            throw new IllegalStateException("Commit not found: " + oid);
        }

        String[] lines = new String(commitData).split("\n");
        String tree = null;
        String parent = null;
        StringBuilder message = new StringBuilder();
        boolean isMessage = false;
        for (String line : lines) {
            if (line.isEmpty()) {
                isMessage = true; // 빈 줄 이후부터 commit message 시작
                continue;
            }

            if (!isMessage) {
                String[] parts = line.split(" ", 2);
                if (parts.length < 2) {
                    continue;
                }
                switch (parts[0]) {
                    case "tree":
                        tree = parts[1];
                        break;
                    case "parent":
                        parent = parts[1];
                        break;
                    case "author":
                    case "time":
                        break;
                    default:
                        throw new IllegalStateException("Unknown commit field: " + parts[0]);
                }
            } else {
                message.append(line).append("\n");
            }
        }
        return new Commit(tree, parent, message.toString().trim());
    }

    public static void checkout(String oid) {
        Commit commit = getCommit(oid);
        readTree(commit.tree);
        Repository.updateRef("HEAD", oid);
    }

    public static void createTag(String name, String oid) {
        Repository.updateRef("tags/" + name, oid);
    }

    private static String commitObject(String treeOid, String parentOid, String message) {
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        StringBuilder commitData = new StringBuilder();
        commitData.append("tree ").append(treeOid).append("\n");
        if (parentOid != null) {
            commitData.append("parent ").append(parentOid).append("\n");
        }
        commitData.append("author MiniGit User\n");
        commitData.append("time ").append(timestamp).append("\n\n");
        commitData.append(message).append("\n");

        return Repository.hashObject(commitData.toString(), "commit");
    }

    private static void clearWorkingDirectory() {
        try {
            Files.walkFileTree(Paths.get("."), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!isIgnored(file)) {
                        Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                    if (!isIgnored(dir) && !dir.equals(Paths.get("."))) {
                        try {
                            Files.delete(dir);
                        } catch (DirectoryNotEmptyException ignored) {
                            // 디렉터리에 무시된 파일이 있을 경우 삭제 실패해도 괜찮음
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Error: Could not clear working directory.");
            e.printStackTrace();
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
