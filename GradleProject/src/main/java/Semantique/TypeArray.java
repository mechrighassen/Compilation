package Semantique;


public class TypeArray extends Type{

//int size
	private String nameOfType;
	private Type typeOfTab;

	public TypeArray(Type typeOfTab, String nameOfType) {
		this.typeOfTab = typeOfTab;
		this.nameOfType=nameOfType;
	}

	public TypeKind getTypeKind() {
		return TypeKind.ARRAY;
	}
	public Type getTypeOfTab() {
		return typeOfTab;
	}

	public int getSize(){
		return 2;
	}

	public String toStringForDot() {
		return "array:" + nameOfType + "(<FONT POINT-SIZE=\"10\">" + typeOfTab.hashCode() + "</FONT>)";
	}

	public String toStringWithoutDot() {
		return "array:" + nameOfType + "(" + typeOfTab.hashCode() + ")";
	}
/*
	public void setSize(int size){

		this.size=size;

	}
	*/

	public Type resolveForward(TDS tds) throws TypeDefinitionCycle, IdentifierMismatchKind, IdentifierNotFound {
		typeOfTab=typeOfTab.resolveForward(tds);
		return this;
	}
}
