package base;

public record RefValue(boolean symbolic, String value) {
    public static RefValue symbolic(String refName) {
        return new RefValue(true, refName);
    }

    public static RefValue direct(String oid) {
        return new RefValue(false, oid);
    }
}
