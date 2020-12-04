package Semantique;

public class TypeVoid extends Type {
    @Override
    public TypeKind getTypeKind() {
        return TypeKind.INT;
    }

    public int getSize(){

        return 4;
    }

    public String toStringForDot() {
        return "void";
    }
    public String toStringWithoutDot() { return toStringForDot(); }


    public Type resolveForward(TDS tds) throws TypeDefinitionCycle, IdentifierMismatchKind, IdentifierNotFound
    {
        return this;
    }
}
