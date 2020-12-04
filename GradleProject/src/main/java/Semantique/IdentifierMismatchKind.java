package Semantique;

public class IdentifierMismatchKind extends Exception {
    String id;
    String expected;
    String found;
    int ligne;
    public IdentifierMismatchKind(String id, String expected, int ligne) {
        this.id = id;
        this.ligne = ligne;
        this.expected=expected;
    }
    @Override
    public String getMessage() {
        return "Line " + ligne + " : we expect here " + id + " to be a " + expected + ".";
    }

}
