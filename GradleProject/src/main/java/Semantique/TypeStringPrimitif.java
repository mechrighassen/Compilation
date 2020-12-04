package Semantique;


public class TypeStringPrimitif extends Type {

	int size;

	public TypeKind getTypeKind() {
		return TypeKind.STRING;
	}

	public int getSize(){
		return 2;
	}

	public void setSize(int size){

		this.size=size;

	}

	public String toStringForDot() {
		return "string";
	}
	public String toStringWithoutDot() { return toStringForDot(); }


	public Type resolveForward(TDS tds) throws IdentifierNotFound, IdentifierMismatchKind, TypeDefinitionCycle {
		return this;
	}
}
