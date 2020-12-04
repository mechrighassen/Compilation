package AST;

import AST.TigerLanguageLexer;
import org.antlr.runtime.tree.CommonTree;
import Semantique.TDS;
import Semantique.*;

import java.util.ArrayList;
import java.util.Collection;

public class TDSDiscoverFunction implements TraitementAST{
    private TDS tds;
    private IdentifierFunction currentFunction;
    private IdentifierType intType;
    private IdentifierType stringType;
    private IdentifierType voidType;

    private boolean error = false;
    private boolean functionCorrect = true;

    public TDSDiscoverFunction(TDS t) {
        this.tds=t;
        tds.nouveauParcours();
    }

    @Override
    public void setError() {
        error=true;
    }

    @Override
    public void traitementProg(XTree tree) {
        tds.entrerRegion();
        intType = (IdentifierType)tds.getIdentifier("int");
        stringType = (IdentifierType)tds.getIdentifier("string");
        voidType = (IdentifierType)tds.getIdentifier("void");
        try {
            addDefaultFunctions();
        } catch (IdentifierAlreadyUsed identifierAlreadyUsed) {
            identifierAlreadyUsed.printStackTrace();
        }
        genericTraitement(tree,0,tree.getChildCount());
        tds.sortirRegion();
    }

    private void addDefaultFunctions() throws IdentifierAlreadyUsed {
        //print
        IdentifierFunction print = new IdentifierFunction("print",voidType,0);
        print.addParam(new IdentifierParam("str", stringType),0);
        tds.addIdentifier(print, 0);

        //printi
        IdentifierFunction printi = new IdentifierFunction("printi",voidType,0);
        printi.addParam(new IdentifierParam("str", intType),0);
        tds.addIdentifier(printi, 0);

        //readInstruction
        IdentifierFunction readinstruction = new IdentifierFunction("read",intType,0);
        tds.addIdentifier(readinstruction, 0);

        IdentifierFunction flush = new IdentifierFunction("flush",voidType,0);
        tds.addIdentifier(flush, 0);

        IdentifierFunction getchar = new IdentifierFunction("getchar",stringType,0);
        tds.addIdentifier(getchar, 0);

        IdentifierFunction ord = new IdentifierFunction("ord",intType,0);
        ord.addParam(new IdentifierParam("str", stringType),0);
        tds.addIdentifier(ord, 0);

        IdentifierFunction chr = new IdentifierFunction("chr",stringType,0);
        chr.addParam(new IdentifierParam("str", intType),0);
        tds.addIdentifier(chr, 0);

        IdentifierFunction size = new IdentifierFunction("size",intType,0);
        size.addParam(new IdentifierParam("str", stringType),0);
        tds.addIdentifier(size, 0);

        IdentifierFunction not = new IdentifierFunction("not",intType,0);
        not.addParam(new IdentifierParam("str", intType),0);
        tds.addIdentifier(not, 0);

        IdentifierFunction exit = new IdentifierFunction("exit",voidType,0);
        exit.addParam(new IdentifierParam("str", intType),0);
        tds.addIdentifier(exit, 0);

        IdentifierFunction substring = new IdentifierFunction("substring",stringType,0);
        substring.addParam(new IdentifierParam("s", stringType),0);
        substring.addParam(new IdentifierParam("f",intType),1);
        substring.addParam(new IdentifierParam("n",intType),2);
        tds.addIdentifier(substring, 0);

        IdentifierFunction concat = new IdentifierFunction("concat",stringType,0);
        concat.addParam(new IdentifierParam("s1", stringType),0);
        concat.addParam(new IdentifierParam("s2",stringType),1);
        tds.addIdentifier(concat, 0);


    }

    @Override
    public void traitementTypeDec(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementArrayOf(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementRecDec(XTree tree) {

    }

    @Override
    public void traitementFieldDec(XTree tree) {
        if (currentFunction == null) {
            error=true;
            System.out.println("In TDSDiscoverFunction : got in FieldDec without function definition.");
            return;
        }
        String id = tree.getChildXTree(0).getText();
        int line = tree.getChildXTree(0).getLine();
        String idT = tree.getChildXTree(1).getText();
        IdentifierType idType = Util.getTypeFromTDS(this, idT, tds, line);
        IdentifierParam param = new IdentifierParam(id, idType);
        try {
            currentFunction.addParam(param, line);
        } catch (IdentifierAlreadyUsed identifierAlreadyUsed) {
            System.out.println(identifierAlreadyUsed.getMessage());
            error=true;
        }
    }

    @Override
    public void traitementFunction(XTree tree) {
     String name =tree.getChildXTree(0).getText();
     int n = tree.getChildCount();
     int line = tree.getChildXTree(0).getLine();
     IdentifierType idType = null;

     //Avons-nous un type spécifié?
     boolean typeSpecifie = false;
     if (n >= 3) //Déjà, il faut au moins trois truc...
     {
         XTree t = tree.getChildXTree(n-2); //On récupère l'avant-dernier
         typeSpecifie = t.getType() != TigerLanguageLexer.FIELDDEC;
     }

     if (typeSpecifie) { // On a un type spécifié {
         String idT = tree.getChildXTree(n-2).getText();
         idType = Util.getTypeFromTDS(this, idT, tds, line);
     }
     else { //On a pas de type spécifié : on a un type void
         idType = voidType;
     }
     currentFunction = new IdentifierFunction(name, idType,0);
     functionCorrect=true;
     genericTraitement(tree, 0, n-1);
     if (functionCorrect) {
         try {
             tds.addIdentifier(currentFunction, line);
         } catch (IdentifierAlreadyUsed identifierAlreadyUsed) {
             error = true;
             System.out.println(identifierAlreadyUsed.getMessage());
         }
     }
     tds.entrerRegion();
    currentFunction.regionNumber=tds.getCurrentRegionId();
     //Here, we need to add parameters to the tds
        for(IdentifierParam p: currentFunction.param){
            try{
                tds.addIdentifier(p,line);
            }catch (IdentifierAlreadyUsed identifierAlreadyUsed){

            }
        }


     genericTraitement(tree,n-1, n);
     tds.sortirRegion();
    }

    @Override
    public void traitementVarDec(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementLet(XTree tree) {
        tds.entrerRegion();
        genericTraitement(tree, 0, tree.getChildCount());
        tds.sortirRegion();
    }

    @Override
    public void traitementCondition(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
       // if(!tree.getChildXTree(0).getText().equals("string")){ // ?????????
       //     error=true;
       // }
    }

    @Override
    public void traitementIf(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementThen(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementElse(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementWhile(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementFor(XTree tree) {
        tds.entrerRegion();
        genericTraitement(tree,0,tree.getChildCount());
        tds.sortirRegion();
    }

    @Override
    public void traitementAffect(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementNegation(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementSeqExp(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementTabAcess(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementFieldAcess(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementArrCreate(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementCallExp(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementRecCreate(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementField(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementIdentifier(XTree tree) {
    }

    @Override
    public void traitementTypeInt(XTree tree) {
    }

    @Override
    public void traitementString(XTree tree) {
    }

    @Override
    public void traitementTypeString(XTree tree) {

    }

    @Override
    public void traitementInteger(XTree tree) {

    }

    @Override
    public void traitementPlus(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementMoin(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementMult(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementDiv(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementOr(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementAnd(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementInf(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementInfEqual(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementSup(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementSupEqual(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementUnequal(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementEqual(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    @Override
    public void traitementNil(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

    }

    public void genericTraitement(XTree tree, int deb, int fin) {
        for (int i = deb; i < Math.min(fin,tree.getChildCount()); i++) {
            tree.getChildXTree(i).traitement(this);
        }
    }

    public boolean hasError() {
        return error;
    }

}