package Semantique;

public class IdentifierAlreadyUsed extends Exception {
    String id;
    int ligne;
    public IdentifierAlreadyUsed(String id, int ligne) {
        this.id = id;
        this.ligne = ligne;
    }
    @Override
    public String getMessage() {
        return "Line " + ligne + " : " + id  + " already used.";
    }
}