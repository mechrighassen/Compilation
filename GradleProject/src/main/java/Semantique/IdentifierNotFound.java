package Semantique;

public class IdentifierNotFound extends Exception {
    String id;
    int ligne;
    public IdentifierNotFound(String id, int ligne) {
        this.id = id;
        this.ligne = ligne;
    }
    @Override
    public String getMessage() {
        return "Line " + ligne + " : " + id  + " not found";
    }
}
