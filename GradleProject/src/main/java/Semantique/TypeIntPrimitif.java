package Semantique;

public class TypeIntPrimitif extends Type{


	@Override
	public TypeKind getTypeKind() {
		return TypeKind.INT;
	}

	public int getSize(){

		return 2;
	}

	public String toStringForDot() {
		return "int";
	}

	public String toStringWithoutDot() { return toStringForDot(); }
	public Type resolveForward(TDS tds) throws TypeDefinitionCycle, IdentifierMismatchKind, IdentifierNotFound
	{
		return this;
	}
}
