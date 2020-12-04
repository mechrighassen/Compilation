package AST;

import Semantique.*;

public class Util {

    public static IdentifierType getTypeFromTDS(TraitementAST traitement, String idT, TDS tds, int line) {
        IdentifierType idType=null;
        try {
            idType = (IdentifierType)tds.getIdentifier(idT);
            if (idType == null) throw new IdentifierNotFound(idT, line);
        }
        catch (ClassCastException e) {
            idType = null;
            traitement.setError();
            System.out.println((new IdentifierMismatchKind(idT, "type", line)).getMessage());
        }
        catch (IdentifierNotFound e) {
            traitement.setError();
            System.out.println(e.getMessage());
        }
        return idType;
    }
    public static IdentifierFunction getFunctionFromTDS(TraitementAST traitement, String idT, TDS tds, int line) {
        IdentifierFunction idFunction=null;
        try {
            idFunction = (IdentifierFunction)tds.getIdentifier(idT);
            if (idFunction== null) throw new IdentifierNotFound(idT, line);
        }
        catch (ClassCastException e) {
            idFunction = null;
            traitement.setError();
            System.out.println((new IdentifierMismatchKind(idT, "function", line)).getMessage());
        }
        catch (IdentifierNotFound e) {
            traitement.setError();
            System.out.println(e.getMessage());
        }
        return idFunction;
    }
    public static IdentifierVar getVarFromTDS(TraitementAST traitement, String idT, TDS tds, int line) {
        IdentifierVar idType=null;
        try {
            idType = (IdentifierVar)tds.getIdentifier(idT);
            if (idType == null) throw new IdentifierNotFound(idT, line);
        }
        catch (ClassCastException e) {
            idType = null;
            traitement.setError();
            System.out.println((new IdentifierMismatchKind(idT, "variable", line)).getMessage());
            return null;
        }
        catch (IdentifierNotFound e) {
            traitement.setError();
            System.out.println(e.getMessage());
            return null;
        }
        return idType;
    }

    public static String getFullText(XTree tree) {
        StringBuilder builder = new StringBuilder();
        //System.err.println(tree.getText() + " " + tree.getType());
        switch (tree.getType()) {
            case AST.TigerLanguageLexer.IDENTIFIER:
                builder.append(tree.getText());
                break;
            case AST.TigerLanguageLexer.INTEGER:
                builder.append(tree.getText());
                break;
            case AST.TigerLanguageLexer.STRING:
                builder.append('"');
                builder.append(tree.getText());
                builder.append('"');
                break;
            case AST.TigerLanguageLexer.FIELDACCESS:
                builder.append(getFullText(tree.getChildXTree(0)));
                builder.append('.');
                builder.append(getFullText(tree.getChildXTree(1)));
                break;
            case AST.TigerLanguageLexer.TABACCESS:
                builder.append(getFullText(tree.getChildXTree(0)));
                builder.append('[');
                builder.append(getFullText(tree.getChildXTree(1)));
                builder.append(']');
                break;
            case AST.TigerLanguageLexer.CALLEXP:
                builder.append(getFullText(tree.getChildXTree(0)));
                break;
            case AST.TigerLanguageLexer.SEQEXP:
                builder.append('(');
                builder.append(getFullText(tree.getChildXTree(tree.getChildCount()-1)));
                builder.append(')');
                break;
            default:
                if (tree.getChildCount() == 2 && tree.getText().length()<=2) {
                    builder.append(getFullText(tree.getChildXTree(0)));
                    builder.append(tree.getText());
                    builder.append(getFullText(tree.getChildXTree(1)));
                }
                else if (tree.getChildCount() == 1) {
                    builder.append(getFullText(tree.getChildXTree(0)));
                }
                else {
                    builder.append(tree.getText());
                }
        }
        return builder.toString();
    }
}
