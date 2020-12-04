package AST;

//Pattern visiteur

import Semantique.IdentifierAlreadyUsed;
import org.antlr.runtime.tree.CommonTree;

public interface TraitementAST {

    void setError();
    void traitementProg(XTree tree);
    void traitementTypeDec(XTree tree);
    void traitementArrayOf(XTree tree);
    void traitementRecDec(XTree tree);
    void traitementFieldDec(XTree tree);
    void traitementFunction(XTree tree);
    void traitementVarDec(XTree tree);
    void traitementLet(XTree tree);
    void traitementCondition(XTree tree);
    void traitementIf(XTree tree);
    void traitementThen(XTree tree);
    void traitementElse(XTree tree);
    void traitementWhile(XTree tree);
    void traitementFor(XTree tree);
    void traitementAffect(XTree tree);
    void traitementNegation(XTree tree);
    void traitementSeqExp(XTree tree);
    void traitementTabAcess(XTree tree);
    void traitementFieldAcess(XTree tree);
    void traitementArrCreate(XTree tree);
    void traitementCallExp(XTree tree);
    void traitementRecCreate(XTree tree);
    void traitementField(XTree tree);
    void traitementIdentifier(XTree tree);
    void traitementTypeInt(XTree tree);
    void traitementString(XTree tree);
    void traitementTypeString(XTree tree);
    void traitementInteger(XTree tree);
    void traitementPlus(XTree tree);
    void traitementMoin(XTree tree);
    void traitementMult(XTree tree);
    void traitementDiv(XTree tree);
    void traitementOr(XTree tree);
    void traitementAnd(XTree tree);
    void traitementInf(XTree tree);
    void traitementInfEqual(XTree tree);
    void traitementSup(XTree tree);
    void traitementSupEqual(XTree tree);
    void traitementUnequal(XTree tree);
    void traitementEqual(XTree tree);
    void traitementNil(XTree tree);

}
