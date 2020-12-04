package Semantique;

import java.util.ArrayList;

public class TypeRecord extends Type{

	ArrayList<IdentifierVar> fields;

	public TypeRecord() {
		fields = new ArrayList<>();
	}

	public int getNbFields() {
		return fields.size();
	}

	@Override
	public TypeKind getTypeKind() {
		return TypeKind.RECORD;
	}

	public IdentifierVar findIdentifierVar(String s) {
		for (IdentifierVar v : fields) {
			if (v.getStringId().equals(s)) {
				return v;
			}
		}
		return null;
	}

	public IdentifierVar getIdentifierVar(int i) {
		try {
			return fields.get(i);
		}
		catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void addField(IdentifierVar v, int line) throws IdentifierAlreadyUsed {
		if (findIdentifierVar(v.getStringId()) != null) {
			fields.add(v); //On l'ajoute malgré tout...pour vérifier plus tard si le type existe
			throw new IdentifierAlreadyUsed(v.getStringId(),line);
		}
		else {
			if (fields.size() > 0) {
				v.setPrecedentVar(fields.get(fields.size() - 1));
			}
			fields.add(v);
		}
	}

	public int getSize() {
		//int S = 0;
		//for (IdentifierVar v : fields) {
		//	S+=v.getSize();
		//}
		//return S;
		return 2;
	}

	public String toStringForDot(){
		StringBuilder builder = new StringBuilder();
		builder.append("record | [");
		for (IdentifierVar v : fields) {
			builder.append(v.getStringId());
			builder.append(":");
			builder.append(v.getIdentifierType().toShortStringForDot());
			builder.append(';');
		}
		builder.append("]");
		return builder.toString();
	}
	public String toStringWithoutDot(){
		StringBuilder builder = new StringBuilder();
		builder.append("record | [");
		for (IdentifierVar v : fields) {
			builder.append(v.getStringId());
			builder.append(":");
			builder.append(v.getIdentifierType().toShortStringWithoutDot());
			builder.append("(offset=");
			builder.append(v.getOffset()).append(")");
			builder.append(';');
		}
		builder.append("]");
		return builder.toString();
	}

	public Type resolveForward(TDS tds) throws IdentifierMismatchKind, IdentifierNotFound {
		for (IdentifierVar t : fields) {
			try {
				if (t.getIdentifierType().getType().getTypeKind()==TypeKind.FORWARDTYPE) {
					t.getIdentifierType().resolveForward(tds);
				}
			} catch (TypeDefinitionCycle typeDefinitionCycle) {
			}
		}
		return this;
	}
}
