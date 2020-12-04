package AST;

import org.antlr.runtime.tree.CommonTree;

public class TraitementNodeFactory {

    public static TraitementNode createTraitementNode(XTree tree) {
         //TraitementNode t;
         int type = tree.getType();

         switch(type) {
             case AST.TigerLanguageLexer.PROG:
                 return t->t.traitementProg(tree);
             case AST.TigerLanguageLexer.TYPEDEC:
                 return t->t.traitementTypeDec(tree);
             case AST.TigerLanguageLexer.ARRAYOF:
                 return t->t.traitementArrayOf(tree);
             case AST.TigerLanguageLexer.RECDEC:
                 return t->t.traitementRecDec(tree);
             case AST.TigerLanguageLexer.FIELDDEC:
                 return t->t.traitementFieldDec(tree);
             case AST.TigerLanguageLexer.FUNCTION:
                 return t->t.traitementFunction(tree);
             case AST.TigerLanguageLexer.VARDEC:
                 return t->t.traitementVarDec(tree);
             case AST.TigerLanguageLexer.LET:
                 return t->t.traitementLet(tree);
                 //break;
             case AST.TigerLanguageLexer.CONDITION:
                 return t->t.traitementCondition(tree);
                 //break;
             case AST.TigerLanguageLexer.IF:
                 return t->t.traitementIf(tree);
             case AST.TigerLanguageLexer.THEN:
                 return t->t.traitementThen(tree);
             case AST.TigerLanguageLexer.ELSE:
                 return t->t.traitementElse(tree);
             case AST.TigerLanguageLexer.WHILE:
                 return t->t.traitementWhile(tree);
             case AST.TigerLanguageLexer.FOR:
                 return t->t.traitementFor(tree);
             case AST.TigerLanguageLexer.AFFECT:
                 return t->t.traitementAffect(tree);
             case AST.TigerLanguageLexer.NEGATION:
                 return t->t.traitementNegation(tree);
             case AST.TigerLanguageLexer.SEQEXP:
                 return t->t.traitementSeqExp(tree);
             case AST.TigerLanguageLexer.TABACCESS:
                 return t->t.traitementTabAcess(tree);
             case AST.TigerLanguageLexer.FIELDACCESS:
                 return t->t.traitementFieldAcess(tree);
             case AST.TigerLanguageLexer.ARRCREATE:
                 return t->t.traitementArrCreate(tree);
             case AST.TigerLanguageLexer.CALLEXP:
                 return t->t.traitementCallExp(tree);
             case AST.TigerLanguageLexer.RECCREATE:
                 return t->t.traitementRecCreate(tree);
             case AST.TigerLanguageLexer.FIELD:
                 return t->t.traitementField(tree);
             case AST.TigerLanguageLexer.INTEGER:
                 return t->t.traitementInteger(tree);
             case AST.TigerLanguageLexer.IDENTIFIER:
                 return t->t.traitementIdentifier(tree);
             case AST.TigerLanguageLexer.TYPESTRING:
                 return t->t.traitementTypeString(tree);
             case AST.TigerLanguageLexer.STRING:
                 return t->t.traitementString(tree);
             case AST.TigerLanguageLexer.TYPEINT:
                 return t->t.traitementTypeInt(tree);
             case AST.TigerLanguageLexer.PLUS:
                 return t->t.traitementPlus(tree);
             case AST.TigerLanguageLexer.MOINS:
                 return t->t.traitementMoin(tree);
             case AST.TigerLanguageLexer.DIV:
                 return t->t.traitementDiv(tree);
             case AST.TigerLanguageLexer.MULT:
                 return t->t.traitementMult(tree);
             case AST.TigerLanguageLexer.ANDToken:
                 return t->t.traitementAnd(tree);
             case AST.TigerLanguageLexer.ORToken:
                 return t->t.traitementOr(tree);
             case AST.TigerLanguageLexer.InfToken:
                 return t->t.traitementInf(tree);
             case AST.TigerLanguageLexer.InfEqualToken:
                 return t->t.traitementInfEqual(tree);
             case AST.TigerLanguageLexer.SupToken:
                 return t->t.traitementSup(tree);
             case AST.TigerLanguageLexer.SupEqualToken:
                 return t->t.traitementSupEqual(tree);
             case AST.TigerLanguageLexer.DiffToken:
                 return t->t.traitementUnequal(tree);
             case AST.TigerLanguageLexer.EqualToken:
                 return t->t.traitementEqual(tree);
             case AST.TigerLanguageLexer.NIL:
                 return t->t.traitementNil(tree);
         }
         return null;
    }
}
