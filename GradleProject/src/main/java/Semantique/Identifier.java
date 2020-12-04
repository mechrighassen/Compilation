package Semantique;

import java.util.ArrayList;
import java.util.List;

public abstract class Identifier {
    public abstract IdentifierKind getIdentifierKind();
    public String getIdentifierKindString() {
        switch (getIdentifierKind()) {
            case FUNCTION:
                return "FUNCTION";
            case VAR:
                return "VAR";
            case TYPE:
                return "TYPE";
            case UNKNOWN:
                return "UNKNOWN";
        }
        return "";
    }
    public abstract String getStringId();
    public abstract String toStringForDot();
    public abstract String toStringWithoutDot();

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getStringId());
        builder.append(" | ");
        builder.append(getIdentifierKindString());
        builder.append(" | ");
        builder.append(this.toStringWithoutDot());
        return builder.toString();
    }
    public String toDot() {
        ArrayList<String> array = new ArrayList<>();
        array.add(getStringId());
        array.add(getIdentifierKindString());
        array.add(this.toStringForDot());
        return toDot(array);
    }
    public String toDot(List<String> args) {
        StringBuilder builder = new StringBuilder();
        builder.append("<tr>");
        for (String s : args) {
            builder.append("<td>");
            builder.append(s);
            builder.append("</td>");
        }
        builder.append("</tr>");
        return builder.toString();
    }
}
