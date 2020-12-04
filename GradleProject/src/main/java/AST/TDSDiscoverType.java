package AST;

import AST.TigerLanguageLexer;
import org.antlr.runtime.tree.CommonTree;
import Semantique.TDS;
import Semantique.*;

import java.util.ArrayList;
import java.util.Collection;

public class TDSDiscoverType implements TraitementAST {

    private TDS tds;
    private Type currentType;
    private IdentifierType intType;
    private IdentifierType stringType;
    private boolean error = false;
    private ArrayList<Identifier> typePoubelles = new ArrayList<Identifier>();

    public TDSDiscoverType(TDS t) {
        tds=t;
    }

    @Override
    public void setError() {
        error=true;
    }

    @Override
    public void traitementProg(XTree tree) {
        tds.entrerRegion();
        tree.setRegion(tds.getCurrentRegionId());
        intType = new IdentifierType("int", new TypeIntPrimitif());
        stringType = new IdentifierType("string", new TypeStringPrimitif());
        IdentifierType voidType = new IdentifierType("void", new TypeVoid());
        try {
            tds.addIdentifier(intType, 0);
            tds.addIdentifier(stringType, 0);
            tds.addIdentifier(voidType, 0);
        }
        catch (IdentifierAlreadyUsed e) {
            System.out.println("A very weird error happened.");
            error = true;
        }
        tree.getChildXTree(0).traitement(this);
        tds.sortirRegion();
        gererForward();
    }

    @Override
    public void traitementTypeDec(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        currentType=null;
        String name = tree.getChildXTree(0).getText();
        if (tree.getChildXTree(1).getType() != TigerLanguageLexer.IDENTIFIER && tree.getChildXTree(1).getType() != TigerLanguageLexer.TYPEINT && tree.getChildXTree(1).getType() != TigerLanguageLexer.TYPESTRING) {
            tree.getChildXTree(1).traitement(this);
            if (currentType != null) {
                error = tds.tryAddIdentifier(new IdentifierType(name, currentType), tree.getChildXTree(0).getLine(), typePoubelles) || error;
            }
        }
        else {
            String id = tree.getChildXTree(1).getText();
            currentType = new TypeForward(id,tds.getCurrentRegionId(),tree.getChildXTree(0).getLine());
            error = tds.tryAddIdentifier(new IdentifierType(name, currentType), tree.getChildXTree(0).getLine(), typePoubelles) || error;
        }
    }

    @Override
    public void traitementArrayOf(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        String id = tree.getChildXTree(0).getText();
        Type typeOfTab = new TypeForward(id, tds.getCurrentRegionId(), tree.getChildXTree(0).getLine());
        currentType = new TypeArray(typeOfTab, id);
    }

    @Override
    public void traitementRecDec(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        currentType = new TypeRecord();
        genericTraitement(tree);
    }

    @Override
    public void traitementFieldDec(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        if (currentType.getTypeKind() != TypeKind.RECORD) {

        }
        else {
            TypeRecord t = (TypeRecord)currentType;
            String id = tree.getChildXTree(0).getText();
            String idType = tree.getChildXTree(1).getText();
            IdentifierType identifier = new IdentifierType(idType, new TypeForward(idType,tds.getCurrentRegionId() ,tree.getChildXTree(0).getLine()));
            try {
                    t.addField(new IdentifierVar(id, identifier), tree.getChildXTree(0).getLine());
            }
            catch (IdentifierAlreadyUsed e) {
                    System.out.println(e.getMessage());
                    error = true;
            }
        }
    }

    @Override
    public void traitementFunction(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        tds.entrerRegion();
        int nbChild = tree.getChildCount();
        tree.getChildXTree(nbChild-1).traitement(this);
        tds.sortirRegion();
    }

    @Override
    public void traitementVarDec(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementLet(XTree tree) {
        tds.entrerRegion();
        tree.setRegion(tds.getCurrentRegionId());

        for (int i = 0 ; i < tree.getChildCount(); i++) {
            tree.getChildXTree(i).traitement(this);
        }
        tds.sortirRegion();
    }

    @Override
    public void traitementCondition(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementIf(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementThen(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        tree.getChildXTree(0).traitement(this);
    }

    @Override
    public void traitementElse(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        tree.getChildXTree(0).traitement(this);
    }

    @Override
    public void traitementWhile(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementFor(XTree tree) {
        //tree.setRegion(tds.getCurrentRegionId());
        tds.entrerRegion();
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
        //tree.getChildXTree(3).traitement(this);
        tds.sortirRegion();
    }

    @Override
    public void traitementAffect(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
       // tree.getChildXTree(1).traitement(this);
        genericTraitement(tree);
    }

    @Override
    public void traitementNegation(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        tree.getChildXTree(0).traitement(this);
    }

    @Override
    public void traitementSeqExp(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementTabAcess(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementFieldAcess(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementArrCreate(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementCallExp(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementRecCreate(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementField(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementIdentifier(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementTypeInt(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementString(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementTypeString(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementInteger(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementPlus(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementMoin(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementMult(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementDiv(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementOr(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementAnd(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);

    }

    @Override
    public void traitementInf(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);

    }

    @Override
    public void traitementInfEqual(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);

    }

    @Override
    public void traitementSup(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementSupEqual(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementUnequal(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementEqual(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    @Override
    public void traitementNil(XTree tree) {
        tree.setRegion(tds.getCurrentRegionId());
        genericTraitement(tree);
    }

    public void genericTraitement(XTree tree) {
        for (int i = 0 ; i < tree.getChildCount() ; i++) {
            tree.getChildXTree(i).traitement(this);
        }
    }

    public void gererForward() {
        //On regarde ce qu'on avait effectivement mis dans la TDS...
        for (Region r : tds.getRegions()) {
            gererForward(r.getIdentifier());
        }

        //On regarde les types qu'on avait pas ajouté dans la tds...
        gererForward(typePoubelles);
    }

    public void gererForward(Collection<Identifier> identifiers) {
        for (Identifier i : identifiers) {
            if (i.getIdentifierKind() == IdentifierKind.TYPE) {
                try {
                    ((IdentifierType) i).resolveForward(tds);
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                    error = true;
                }
            }
        }
    }

    public boolean hasError() {
        return error;
    }
}
