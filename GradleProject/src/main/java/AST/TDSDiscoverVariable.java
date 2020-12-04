package AST;

import Semantique.*;

public class TDSDiscoverVariable implements TraitementAST {
    private TDS tds;
    private TDSDiscoverType discoverType;
    private TypeRecord currentRecord;
    private IdentifierType intType;
    private IdentifierType stringType;
    private IdentifierType voidType;
    private boolean error = false;
    private Identifier currentIdentifier;
    private IdentifierType nilType;

    public TDSDiscoverVariable(TDS t) {
        tds=t;
        tds.nouveauParcours();
    }



    @Override
    public void traitementProg(XTree tree) {
        tds.entrerRegion();
        intType = (IdentifierType)tds.getIdentifier("int");
        stringType = (IdentifierType)tds.getIdentifier("string");
        voidType = (IdentifierType)tds.getIdentifier("void");
        nilType = new IdentifierType("nil", new TypeRecord());
        genericTraitement(tree,0,tree.getChildCount());
        tds.sortirRegion();
    }

    @Override
    public void setError() {
        error=true;
    }


    @Override
    public void traitementTypeDec(XTree tree) {
    }

    @Override
    public void traitementArrayOf(XTree tree) {
    }

    @Override
    public void traitementRecDec(XTree tree) {
    }

    @Override
    public void traitementFieldDec(XTree tree) {
     //   genericTraitement(tree,0,tree.getChildCount());
    }

    @Override
    public void traitementFunction(XTree tree) {
        Identifier id = tds.getIdentifier(tree.getChildXTree(0).getText());
        tds.entrerRegion();
        tds.getCurrentRegion().setNewEnvironnement(true);
        genericTraitement(tree, tree.getChildCount()-1, tree.getChildCount());

        if (id != null && id instanceof IdentifierFunction) {
            IdentifierFunction func = (IdentifierFunction)id;

            IdentifierType expected = func.getTypeRetour();
            IdentifierType actual = tree.getChildXTree(tree.getChildCount()-1).getTypeOfNode();
            if (actual != null && expected.getType() != actual.getType()) {
                int line = tree.getLine();
                System.out.println("Line " + line + " : wrong return type. Found " + actual.getStringId() + " expected " + expected.getStringId());
                error = true;
            }
        }

        tds.sortirRegion();
    }

    @Override
    public void traitementVarDec(XTree tree) {
        IdentifierType typeOfVariable = null;
        int line = tree.getChildXTree(0).getLine();
        String variableName = tree.getChildXTree(0).getText();
        if((tree.getChildCount()==3)){
            genericTraitement(tree,2,tree.getChildCount());
            typeOfVariable = Util.getTypeFromTDS(this, tree.getChildXTree(1).getText(), tds, line);
        }
        else {
            genericTraitement(tree,1,tree.getChildCount());
        }
        IdentifierType trueTypeOfVariable = tree.getChildXTree(tree.getChildCount()-1).getTypeOfNode();
        //Si typeOfVariable != null, alors il faudra vérifier à un moment que ça correspond...sauf si on a un nil!
        //Cas particulier où on a un nil///
        if (trueTypeOfVariable != null && typeOfVariable != null && trueTypeOfVariable.getType() == nilType.getType()) {
            if (typeOfVariable.getType().getTypeKind() != TypeKind.RECORD) {
                error = true;
                System.out.println("Line " + line + " : nil type can only be affected to a record.");
            }
            trueTypeOfVariable = typeOfVariable;
        }
        else if (trueTypeOfVariable != null && trueTypeOfVariable.getType() == nilType.getType() && typeOfVariable == null) {
            error = true;
            System.out.println("Line " + line + " : we can't affect nil without knowing the type of the record.");
        }
        else if (trueTypeOfVariable != null && typeOfVariable != null && (typeOfVariable.getType() != trueTypeOfVariable.getType())) {
            System.out.println((new IdentifierMismatchKind(trueTypeOfVariable.getStringId(),typeOfVariable.getStringId(),line)).getMessage());
            error=true;
        }

        IdentifierVar variable = new IdentifierVar(variableName,trueTypeOfVariable);
        try {
                tds.addIdentifier(variable,line);
            } catch (IdentifierAlreadyUsed identifierAlreadyUsed) {
                System.out.println(identifierAlreadyUsed.getMessage());
            }
        tree.setTypeOfNode(voidType);
    }

    @Override
    public void traitementLet(XTree tree) {
        IdentifierVar lastVar = tds.getCurrentRegion().getLastAdded();
        tds.entrerRegion();
        tds.getCurrentRegion().setLastAdded(lastVar);
        genericTraitement(tree,0,tree.getChildCount());

    //    lastVar = tds.getCurrentRegion().getLastAdded();
        tds.sortirRegion();
    //    tds.getCurrentRegion().setLastAdded(lastVar);
    }

    @Override
    public void traitementCondition(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        IdentifierType id = tree.getChildXTree(0).getTypeOfNode();
        int line = tree.getLine();
        if(id != null && id.getType()!=intType.getType()){
            String txt = Util.getFullText(tree.getChildXTree(0));
            System.out.println("Line " + line + " : " + txt + " must be integer.");
            error=true;
        }
        int nbChildren=tree.getChildCount();
        IdentifierType then = tree.getChildXTree(1).getTypeOfNode();
        if (then != null) {
            if (nbChildren == 3) {
                IdentifierType els = tree.getChildXTree(2).getTypeOfNode();
                if (els != null && (els.getType() == nilType.getType() || then.getType() == nilType.getType())) {
                    if (then.getType() != els.getType() && els.getType().getTypeKind() != TypeKind.RECORD && then.getType().getTypeKind() != TypeKind.RECORD) {
                        error = true;
                        System.out.println("Line " + line + " : nil type can only be affected to a record.");
                    }
                }
                else if (els != null && then.getType() != els.getType()) {
                    System.out.println("Line " + line + " : the then and else branch must have same type");
                    error = true;
                }
            } else {
                if (then.getType() != voidType.getType()) {
                    System.out.println("Line " + line + " : the then branch is expected to be void.");
                    error = true;
                }
            }
            tree.setTypeOfNode(then);
        }
    }

    @Override
    public void traitementIf(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        tree.setTypeOfNode(tree.getChildXTree(0).getTypeOfNode());
    }

    @Override
    public void traitementThen(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        tree.setTypeOfNode(tree.getChildXTree(0).getTypeOfNode());
    }

    @Override
    public void traitementElse(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        tree.setTypeOfNode(tree.getChildXTree(0).getTypeOfNode());
    }

    @Override
    public void traitementWhile(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        if(tree.getChildXTree(1).getTypeOfNode() != null && tree.getChildXTree(1).getTypeOfNode().getType()!=voidType.getType()) {
            System.out.println("Line " + tree.getLine() + " : while expression must be void.");
            error = true;
        }
        if(tree.getChildXTree(0).getTypeOfNode() != null && tree.getChildXTree(0).getTypeOfNode().getType()!=intType.getType()) {
            System.out.println("Line " + tree.getLine() + " : while condition must be integer.");
            error = true;
        }
    }

    @Override
    public void traitementFor(XTree tree) {
        IdentifierVar lastVar = tds.getCurrentRegion().getLastAdded();
        tds.entrerRegion();
        tds.getCurrentRegion().setLastAdded(lastVar);        //Add identifier...
        IdentifierVar index = new IdentifierVar(tree.getChildXTree(0).getText(), intType);
        try {
            tds.addIdentifier(index, tree.getChildXTree(0).getLine());
        } catch (IdentifierAlreadyUsed identifierAlreadyUsed) {
            System.out.println("Unexpected problem in traitement for.");
        }

        genericTraitement(tree,1,tree.getChildCount());
        if(tree.getChildXTree(3).getTypeOfNode() != null && tree.getChildXTree(3).getTypeOfNode().getType()!=voidType.getType()) {
            System.out.println("Line " + tree.getLine() + " : for expression must be void.");
            error = true;
        }
        //Verifier que les noeuds 1 et 2 sont de type int
        if(tree.getChildXTree(1).getTypeOfNode() != null && tree.getChildXTree(1).getTypeOfNode().getType()!=intType.getType()) {
            System.out.println("Line " + tree.getLine() + " : for start must be integer.");
            error = true;
        }
        if(tree.getChildXTree(2).getTypeOfNode() != null && tree.getChildXTree(2).getTypeOfNode().getType()!=intType.getType()) {
            System.out.println("Line " + tree.getLine() + " : for end must be integer.");
            error = true;
        }
      //  lastVar = tds.getCurrentRegion().getLastAdded();
        tds.sortirRegion();
      //  tds.getCurrentRegion().setLastAdded(lastVar);
        tree.setTypeOfNode(voidType);
    }

    @Override
    public void traitementAffect(XTree tree) {
        /*genericTraitement(tree,0,tree.getChildCount());
        if(tree.getChildXTree(0).getTypeOfNode().getType()!=tree.getChildXTree(1).getTypeOfNode().getType()) {
            System.out.println("Type mismatch");
            error = true;
        }*/
        genericTraitement(tree, 0, tree.getChildCount());
        binaryOperationNEqual(tree, voidType,"affect");
        int type=tree.getChildXTree(0).getType();
        if (!(type == AST.TigerLanguageLexer.IDENTIFIER || type == AST.TigerLanguageLexer.TABACCESS || type == AST.TigerLanguageLexer.FIELDACCESS)) {
            int line = tree.getLine();
            System.out.println("Line " + line + " : we need a variable on the left of :=.");
            error = true;
        }
    }

    @Override
    public void traitementNegation(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        IdentifierType b = tree.getChildXTree(0).getTypeOfNode();
        int line = tree.getLine();
        if (b != null && b.getType() != intType.getType()) {
            System.out.println("Line " + line + " : negation can only be applied to int.");
        }
        else if (b != null) tree.setTypeOfNode(intType);
    }

    @Override
    public void traitementSeqExp(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        if (tree.getChildCount() > 0) tree.setTypeOfNode(tree.getChildXTree(tree.getChildCount()-1).getTypeOfNode());
        else tree.setTypeOfNode(voidType);
    }

    @Override
    public void traitementTabAcess(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());

        IdentifierType typeOfNode = tree.getChildXTree(0).getTypeOfNode();
        Type type = null;
        if (typeOfNode != null) type = typeOfNode.getType();
        if(type != null && type.getTypeKind()!=TypeKind.ARRAY){
            System.out.println("Line " + tree.getLine() + " : type mismatch : we are trying to access an array even though it isn't one");
            error = true;
        }
        else if (type != null) {
            TypeArray array = (TypeArray) type;
            tree.setTypeOfNode(new IdentifierType("", array.getTypeOfTab()));
        }
        IdentifierType typeOfIndex = tree.getChildXTree(1).getTypeOfNode();
        if(typeOfIndex != null && typeOfIndex.getType()!=intType.getType()){
            System.out.println("Line " + tree.getLine() + " : we are trying to access an array but the index isn't an integer");
            error=true;
        }

    }

    @Override
    public void traitementFieldAcess(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount()-1);
        IdentifierType typeOfNode = tree.getChildXTree(0).getTypeOfNode();
        Type type = null;
        if (typeOfNode != null) type = typeOfNode.getType();
        int line = tree.getLine();
        if(type != null && type.getTypeKind()!=TypeKind.RECORD){
            System.out.println("Line " + line + " : tried to access a field on something that isn't a record.");
            error = true;
            return;
        }
        TypeRecord record = (TypeRecord)type;
        IdentifierVar field = record.findIdentifierVar(tree.getChildXTree(1).getText());
        if (field == null){
            System.out.println("Line " + line + " : tried to access a field on a record, but field doesn't exist");
            error = true;
            return;
        }
        tree.setTypeOfNode(field.getIdentifierType());
    }

    @Override
    public void traitementArrCreate(XTree tree) {
        genericTraitement(tree,1,tree.getChildCount());

        String typeSOfTab = tree.getChildXTree(0).getText();
        int line = tree.getChildXTree(0).getLine();
        IdentifierType typeTab = Util.getTypeFromTDS(this, typeSOfTab, tds, line);

        IdentifierType typeSize = tree.getChildXTree(1).getTypeOfNode();
        IdentifierType typeOf = tree.getChildXTree(2).getTypeOfNode();

        if (typeSize != null && typeSize.getType() != intType.getType()) {
            System.out.println("Line " + line + " : an array size must be an integer.");
            error=true;
        }

        Type typeOfTab = null;
        if (typeTab != null) {
            try {
                TypeArray array = (TypeArray) typeTab.getType();
                typeOfTab = array.getTypeOfTab();
            }
            catch (ClassCastException e) {
                System.out.println("Line " + line + " : when instancing an array, the instancing type must be a type of array but " + typeSOfTab + " is not.");
                error = true;
            }
        }
        if (typeOfTab != null && typeOf != null && typeOf.getType() == nilType.getType()) {
            if (typeOfTab.getTypeKind() != TypeKind.RECORD) {
                error = true;
                System.out.println("Line " + line + " : nil type can only be affected to a record.");
            }
        }
        else if (typeOfTab != null && typeOf != null && typeOf.getType() != typeOfTab) {
            System.out.println("Line " + line + " : array was initialized with a wrong type");
        }
        tree.setTypeOfNode(typeTab);
    }

    @Override
    public void traitementCallExp(XTree tree) {
        genericTraitement(tree,1,tree.getChildCount());
        IdentifierFunction idf = Util.getFunctionFromTDS(this,tree.getChildXTree(0).getText(),tds,tree.getChild(0).getLine());
        if (idf !=null) {
            if (idf.getNbParam() != tree.getChildCount() -1) {
                int line = tree.getChildXTree(0).getLine();
                String funcName = tree.getChildXTree(0).getText();
                System.out.println("Line " + line + " : calling function " + funcName + " with wrong number of parameters.");
                error = true;
            }
            else {
                for (int i = 0; i < idf.getNbParam(); i++) {
                    IdentifierType typeExpected = idf.getParam().get(i).getIdentifierType();
                    IdentifierType trueType = tree.getChildXTree(i + 1).getTypeOfNode();
                    if (typeExpected != null && trueType != null && typeExpected.getType() != trueType.getType()) {
                        error = true;
                        String txt = Util.getFullText(tree.getChildXTree(i + 1));
                        System.out.println(new IdentifierMismatchKind(txt, typeExpected.getStringId(), tree.getChildXTree(0).getLine()).getMessage());
                    }

                }
            }
            tree.setTypeOfNode(idf.getTypeRetour());
        }
    }

    @Override
    public void traitementRecCreate(XTree tree) {
        //Ici on fait un parcours infixe
        String name = tree.getChildXTree(0).getText();
        Identifier id = tds.getIdentifier(name);
        int line = tree.getChildXTree(0).getLine();

        //On a pas un IdentifierType...
        if (!(id instanceof IdentifierType)) {
            System.out.println((new IdentifierMismatchKind(id.getStringId(), "type", line)).getMessage());
            return;
        }

        IdentifierType idType = (IdentifierType) id;

        //On a pas un record...
        if (!(idType.getType() instanceof TypeRecord)) {
            System.out.println((new IdentifierMismatchKind(idType.getStringId(), "record", line)).getMessage());
            return;
        }

        TypeRecord ancienRecord = currentRecord;
        currentRecord = (TypeRecord)idType.getType();

        if (currentRecord.getNbFields() != tree.getChildCount() - 1) {
            System.out.println("Line " + line + " : wrong number of field input");
            error = true;
        }
        //On, ici on a le currentRecord
        else genericTraitement(tree,1,tree.getChildCount());
        //les fils sont un id et des Field (field create)

        currentRecord = ancienRecord;
        tree.setTypeOfNode(idType);

    }

    @Override
    public void traitementField(XTree tree) {
        genericTraitement(tree,1,tree.getChildCount());
        String name = tree.getChildXTree(0).getText();
        int line = tree.getChildXTree(0).getLine();

        //IdentifierVar v = currentRecord.findIdentifierVar(name);
        IdentifierVar v = currentRecord.getIdentifierVar(tree.getChildIndex()-1);

        if(v==null || !v.getStringId().equals(name)){
            System.out.println("Line " + line + " : unknown " + name + " field");
            error = true;
            return;
        }

        if (tree.getChildXTree(1).getTypeOfNode() != null && tree.getChildXTree(1).getTypeOfNode().getType() == nilType.getType()) {
            if (v.getIdentifierType().getType().getTypeKind() != TypeKind.RECORD) {
                error = true;
                System.out.println("Line " + line + " : nil type can only be affected to a record.");
            }
        }
        else if(tree.getChildXTree(1).getTypeOfNode() != null && v.getIdentifierType().getType()!=tree.getChildXTree(1).getTypeOfNode().getType()){
            System.out.println((new IdentifierMismatchKind(tree.getChildXTree(1).getTypeOfNode().getStringId(),v.getIdentifierType().getStringId(),line)).getMessage());
            error = true;
            return;
        }
        tree.setTypeOfNode(tree.getChildXTree(1).getTypeOfNode());
    }

    @Override
    public void traitementIdentifier(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        IdentifierVar id = Util.getVarFromTDS(this, tree.getText(), tds, tree.getLine());
        if (id != null) {
            tree.setTypeOfNode(id.getIdentifierType());
        }
        /*
        currentIdentifier = tds.getIdentifier(tree.getText());
        if(currentIdentifier==null){
            System.out.println("Error Identifier" + tree.getLine() + " " + tree.getText());
            error = true;
            return;
        }
        if (currentIdentifier instanceof IdentifierType) {
            tree.setTypeOfNode((IdentifierType)currentIdentifier);
        }
        else if (currentIdentifier instanceof IdentifierFunction) {
            tree.setTypeOfNode(((IdentifierFunction) currentIdentifier).getTypeRetour());
        }
        else if (currentIdentifier instanceof IdentifierVar) {
            tree.setTypeOfNode(((IdentifierVar) currentIdentifier).getIdentifierType());
        }
        */

    }

    @Override
    public void traitementTypeInt(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        tree.setTypeOfNode(intType);
    }

    @Override
    public void traitementString(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        tree.setTypeOfNode(stringType);
    }

    @Override
    public void traitementTypeString(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        tree.setTypeOfNode(stringType);
    }

    @Override
    public void traitementInteger(XTree tree) {
        genericTraitement(tree,0,tree.getChildCount());
        tree.setTypeOfNode(intType);
    }

    @Override
    public void traitementPlus(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinary(tree, intType, true, intType, "+");
    }

    @Override
    public void traitementMoin(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinary(tree, intType, true, intType, "-");
    }

    @Override
    public void traitementMult(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinary(tree, intType, true, intType, "*");
    }

    @Override
    public void traitementDiv(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinary(tree, intType, true, intType, "/");
    }

    @Override
    public void traitementOr(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinary(tree, intType, true, intType, "|");
    }

    @Override
    public void traitementAnd(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinary(tree, intType, true, intType, "&");
    }

    @Override
    public void traitementInf(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinaryIntString(tree, "<");
    }

    @Override
    public void traitementInfEqual(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinaryIntString(tree, "<=");
    }

    @Override
    public void traitementSup(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinaryIntString(tree, ">");
    }

    @Override
    public void traitementSupEqual(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        genericBinaryIntString(tree, ">=");
    }

    @Override
    public void traitementUnequal(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        binaryOperationNEqual(tree, intType,"<>");
    }

    @Override
    public void traitementEqual(XTree tree) {
        genericTraitement(tree, 0, tree.getChildCount());
        binaryOperationNEqual(tree, intType,"==");
    }

    @Override
    public void traitementNil(XTree tree) {
        tree.setTypeOfNode(nilType);
    }

    public void genericTraitement(XTree tree, int deb, int fin) {
        for (int i = deb; i < Math.min(fin,tree.getChildCount()); i++) {
            tree.getChildXTree(i).traitement(this);
        }
    }

    public boolean hasError() {
        return error;
    }

    public void genericApplyType(XTree tree) {
        IdentifierType type = Util.getTypeFromTDS(this, tree.getText(), tds, tree.getLine());
        tree.setTypeOfNode(type);
    }

    private void genericBinary(XTree tree, IdentifierType tOfNode, boolean mustBeEqual, IdentifierType mustBe, String op) {
        IdentifierType g = tree.getChildXTree(0).getTypeOfNode();
        IdentifierType d = tree.getChildXTree(1).getTypeOfNode();

        String txtg = Util.getFullText(tree.getChildXTree(0));
        String txtd = Util.getFullText(tree.getChildXTree(1));
        if (mustBe != null) {
            boolean err = false;
            if (g != null && g.getType() != mustBe.getType()) {
                System.out.println((new IdentifierMismatchKind(txtg, mustBe.getStringId(), tree.getLine())).getMessage());
                error = true;
                err = true;
            }
            if (d != null && d.getType() != mustBe.getType()) {
                System.out.println((new IdentifierMismatchKind(txtd, mustBe.getStringId(), tree.getLine())).getMessage());
                error = true;
                return;
            }
            if (err) return;
        }
        else if (mustBeEqual) {
            if (g != null && d != null && g.getType() != d.getType()) {

                System.out.println("Line " + tree.getLine() + " : we have a " + op + " operator but " + txtg + " and " + txtd + " doesn't have the same type.");
                error = true;
                return;
            }
        }
        if (g != null && d != null) tree.setTypeOfNode(tOfNode);
    }

    private void genericBinaryIntString(XTree tree, String op) {
        IdentifierType g = tree.getChildXTree(0).getTypeOfNode();
        IdentifierType d = tree.getChildXTree(1).getTypeOfNode();
        boolean err = false;
        int line = tree.getLine();


        if (g != null && g.getType() != intType.getType() && g.getType() != stringType.getType()) {
            error = true;
            err = true;
        }
        if (d != null && d.getType() != intType.getType() && d.getType() != stringType.getType()) {
            error = true;
            err = true;
        }
        if (g != null && d != null && g.getType() != d.getType()) {
            error = true;
            err = true;
        }

        if (err) {
            System.out.println("Line " + line + " : we have " + Util.getFullText(tree) + " but operation " + op + " has been applied to incorrect types");
        }else if (g != null && d !=null) {
            tree.setTypeOfNode(intType);
        }
    }

    private void binaryOperationNEqual(XTree tree, IdentifierType toType, String op) {
        IdentifierType g = tree.getChildXTree(0).getTypeOfNode();
        IdentifierType d = tree.getChildXTree(1).getTypeOfNode();
        int line = tree.getLine();

        //Ici, clairement, on a besoin que g et d sont non nul...)
        if (g != null && d != null) {
            //Cas particulier où on a un nil
            if (g.getType() == nilType.getType() || d.getType() == nilType.getType()) {
                if (g.getType().getTypeKind() != TypeKind.RECORD || d.getType().getTypeKind() != TypeKind.RECORD) {
                    error = true;
                    System.out.println("Line " + line + " : nil type can only be compared/affected to a record.");
                }
                else {
                    tree.setTypeOfNode(intType);
                }
            }
            else {
                //Dans ce cas, il faut que le type a gauche soit le même qu'à droite...
                genericBinary(tree, toType, true, null, op);
            }
        }
    }

}
