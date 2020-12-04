package Semantique;

public class TypeForward extends Type {

    private String toType;
    private int line;
    private int regionToLook;
    private boolean inResolve=false;
    private boolean solved=false;

    public TypeForward(String t, int i, int line) {
        toType=t;
        regionToLook=i;
        this.line=line;
    }

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.FORWARDTYPE;
    }

    @Override
    public int getSize() {
        return 0;
    }

    public String toStringForDot() {
        return "(forward:"+toType+")";
    }

    public String toStringWithoutDot()  { return toStringForDot(); }
    public Type resolveForward(TDS tds) throws TypeDefinitionCycle, IdentifierNotFound, IdentifierMismatchKind{
        if (solved)
            return this;
        if (inResolve) {
            inResolve=false;
            solved=true;
            throw new TypeDefinitionCycle(line);
        }
        inResolve=true;
        Identifier id = tds.getIdentifier(toType, regionToLook);
        if (id == null) {
            inResolve=false;
            solved=true;
            throw new IdentifierNotFound(toType, line);
        }
        if (id.getIdentifierKind() != IdentifierKind.TYPE) {
            inResolve=false;
            solved=true;
            throw new IdentifierMismatchKind(toType,"type",line);
        }
        IdentifierType idType = (IdentifierType)id;
        idType.resolveForward(tds);

        inResolve=false;
        return idType.getType();
    }
}
