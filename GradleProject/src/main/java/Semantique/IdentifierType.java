package Semantique;

public class IdentifierType extends Identifier{
	private Type ty;
    private String stringID;

	public IdentifierType(String s, Type t){
		stringID = s;
		this.ty=t;
	}

	public boolean isForward(){
		return false;
	}

	public Type getType(){
		return ty;
	}

	public void setType(Type t) {
		this.ty=t;
	}

	public IdentifierKind getIdentifierKind() {
	    return IdentifierKind.TYPE;
    }

    public String getStringId() {
	    return stringID;
    }

    public int getSize() {
	    return ty.getSize();
    }


    public String toStringForDot() {
	    return "(<FONT POINT-SIZE=\"10\">" + ty.hashCode() + "</FONT>)" + ty.toStringForDot() + " | size = " + this.getSize();
    }
	public String toStringWithoutDot() {
		return "(" + ty.hashCode() + ")" + ty.toStringWithoutDot() + " | size = " + this.getSize();
	}
	public String toShortStringForDot() {
		return this.getStringId() + "(<FONT POINT-SIZE=\"10\">" + getType().hashCode() + "</FONT>)";
	}

	public String toShortStringWithoutDot() {
		return this.getStringId() + "(" + ty.hashCode() + ")";
	}

    public void resolveForward(TDS tds) throws IdentifierNotFound, IdentifierMismatchKind, TypeDefinitionCycle {
		Type t = this.ty.resolveForward(tds);
		this.ty = t;
	}



}