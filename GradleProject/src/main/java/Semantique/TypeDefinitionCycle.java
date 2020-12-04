package Semantique;

public class TypeDefinitionCycle extends Exception {
    int line;

    public TypeDefinitionCycle(int ligne) {
        this.line = ligne;
    }
    @Override
    public String getMessage() {
        return "Line " + line + " : type definition cycle";
    }
}
