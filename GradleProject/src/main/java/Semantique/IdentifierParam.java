package Semantique;


public class IdentifierParam extends IdentifierVar {

    public IdentifierParam(String id, IdentifierType type, IdentifierParam precedent){
        super(id, type, precedent);
    }
    public IdentifierParam(String id, IdentifierType type) {
        super(id, type, null);
    }
    public int getOffset(){
        //if (getPrecedentVar() == null) return -this.getSize()-2;
        if (getPrecedentVar() == null) return -2-2;
        else if (getPrecedentVar().getIdentifierType() == null) return -1;
        //else return getPrecedentVar().getOffset()-this.getSize();
        else return getPrecedentVar().getOffset()-2;
    }

/*
    private IdentifierType typeOfVariable;
    private String stringID;
    private IdentifierParam precedentVar;

     public IdentifierParam(String id, IdentifierType type, IdentifierParam precedent){
        stringID=id;
        typeOfVariable=type;
        precedentVar = precedent;
    }


   
    public IdentifierParam(String id, IdentifierType type) {
        this(id, type, null);
    }

    public IdentifierKind getIdentifierKind(){
        return IdentifierKind.VAR;
    }

    public int getOffset(){
        if (precedentVar.getIdentifierType() == null) return -1;
        if (precedentVar == null) return -2;
        else return precedentVar.getOffset()-precedentVar.getIdentifierType().getSize();
    }

    public void setPrecedentVar(IdentifierParam var) {
        precedentVar=var;
    }

    public IdentifierType getIdentifierType() {
        return typeOfVariable;
    }

    public String getStringId() {
        return stringID;
    }

    public int getSize() {
        return typeOfVariable.getSize();
    }

    public String toStringForDot() {
        return typeOfVariable.getStringId() + "|size=" + this.getSize() + "| offset = " + this.getOffset() + "|";
    }
    public String toStringWithoutDot() {
        return typeOfVariable.getStringId() + "|size=" + this.getSize() + "| offset = " + this.getOffset() + "|";
    }

    public String toDot() {
         return "";
    }
*/

}
