package Semantique;

public class IdentifierVar extends Identifier {
    private IdentifierType typeOfVariable;
    private String stringID;
    private IdentifierVar precedentVar;

    public IdentifierVar(String id, IdentifierType type, IdentifierVar precedent){
        stringID=id;
        typeOfVariable=type;
        precedentVar = precedent;
    }

    public IdentifierVar(String id, IdentifierType type) {
        this(id, type, null);
    }

    public IdentifierKind getIdentifierKind(){
        return IdentifierKind.VAR;
    }

    public int getOffset(){
        if (precedentVar == null) return 4;
        else if (precedentVar.getIdentifierType() == null) return -1;
        //else return precedentVar.getOffset()+precedentVar.getIdentifierType().getSize();
        else return precedentVar.getOffset()+2;
    }

    public void setPrecedentVar(IdentifierVar var) {
        precedentVar=var;
    }
    public IdentifierVar getPrecedentVar() {
        return precedentVar;
    }

    public IdentifierType getIdentifierType() {
        return typeOfVariable;
    }

    public String getStringId() {
        return stringID;
    }

    public int getSize() {
        if (typeOfVariable == null) return -1;
        else return typeOfVariable.getSize();
    }

    public String toStringForDot() {
        return typeOfVariable.toShortStringForDot() + "|size=" + this.getSize() + "| offset = " + this.getOffset();
    }
    public String toStringWithoutDot() {
        return typeOfVariable.toShortStringWithoutDot() + "|size=" + this.getSize() + "| offset = " + this.getOffset();
    }

}
