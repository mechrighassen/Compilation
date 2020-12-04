
package Semantique;

public abstract class Type{

	public abstract TypeKind getTypeKind();
	public abstract int getSize();
	public abstract Type resolveForward(TDS tds) throws TypeDefinitionCycle, IdentifierMismatchKind, IdentifierNotFound;
	public abstract String toStringForDot();
	public abstract String toStringWithoutDot();
}
