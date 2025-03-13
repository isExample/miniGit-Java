package base;

public class Commit {
    public final String tree;
    public final String parent;
    public final String message;

    public Commit(String tree, String parent, String message) {
        this.tree = tree;
        this.parent = parent;
        this.message = message;
    }
}
