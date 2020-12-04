package Generation;

import AST.TigerLanguageLexer;
import AST.TigerLanguageParser;
import AST.TraitementAST;
import AST.XTree;
import Generation.Instruction.*;
import Semantique.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ASTGeneration implements TraitementAST {




    //WARNING WARNING WARNING
    //Ici, on ne fait pas de entrerRegion, sortirRegion sur la tds.
    //Si on veut accéder à un truc dans la TDS, par exemple un identifier, voir fonction exemple


    private TDS tds;
    private RegisterController rController;
    private ProgrammeMaker prgMaker;
    private boolean error = false;
    Deque<XTree> functionNode; // Pile des régions ouvertes
    private FunctionClass fclass;

    public ASTGeneration(TDS tds) {
        this.tds = tds;
        rController = new RegisterController();
        prgMaker = new ProgrammeMaker();
        functionNode = new ArrayDeque<>();
        fclass= new FunctionClass(tds,prgMaker);
    }


    @Override
    public void setError() {
        error = true;
    }

    public ProgrammeMaker getPrgMaker() {
        return prgMaker;
    }

    @Override
    public void traitementProg(XTree tree) {

        rController.entrerRegion();
        String etiqmain = "main_";
        prgMaker.addEtiquette(etiqmain);

        prgMaker.addSimpleInstruction("LDW TA, #STTA");
        prgMaker.addSimpleInstruction("LDW BP, SP");

        prgMaker.addSimpleInstruction("LDW R0, #0");
        prgMaker.addSimpleInstruction("STW R0, (SP)");
        prgMaker.addSimpleInstruction("ADQ -2, SP");
        prgMaker.addSimpleInstruction("STW R0, (SP)");
        prgMaker.addSimpleInstruction("ADQ -2, SP");



        prgMaker.addSimpleInstruction("\n");


        tree.getChildXTree(0).traitement(this);

        prgMaker.addSimpleInstruction("LDW SP, BP");
        prgMaker.addSimpleInstruction("LDW BP, (SP)");
        prgMaker.addSimpleInstruction("ADQ 2, SP");
        prgMaker.addSimpleInstruction("LDQ 64,R14");
        prgMaker.addSimpleInstruction("TRP R14");

        while (!functionNode.isEmpty()) {
            XTree xTree = functionNode.pop();
            xTree.traitement(this);
        }

        fclass.printFunction();
        fclass.printIFunction();
        fclass.readInstruction();

        rController.sortirRegion();
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

    }

    @Override
    public void traitementFunction(XTree tree) {
        fclass.beginningFunction(tree);

        tree.getChildXTree(tree.getChildCount()-1).traitement(this);

        fclass.endFunction(tree);
    }

    @Override
    public void traitementVarDec(XTree tree) {
        traitemen_affect(tree.getChildXTree(0),tree.getChildXTree(tree.getChildCount()-1));
    }

    @Override
    public void traitementLet(XTree tree) {
        Region regionCourante=tds.getRegion( tree.getRegion());
        int size = regionCourante.getTotalSize();
        //LDW R0,#size
        //SUB SP,R0,SP
        prgMaker.addSimpleInstruction("LDW R0,#"+size);
        prgMaker.addSimpleInstruction("SUB SP,R0,SP");

        for(int i=0;i<tree.getChildCount();i++){
            if (tree.getChildXTree(i).getType() == TigerLanguageParser.FUNCTION) functionNode.push(tree.getChildXTree(i));
            else tree.getChildXTree(i).traitement(this);
        }

        //LDW R0,#size
        //ADD SP,R0,SP

        prgMaker.addSimpleInstruction("LDW UT,#"+size);
        prgMaker.addSimpleInstruction("ADD SP,UT,SP");
    }

    @Override
    public void traitementCondition(XTree tree) {
        String idCondition = Integer.toString(tree.hashCode());
        tree.getChildXTree(0).traitement(this);
        String etiqfin;
        if (tree.getChildCount() == 2) { //IF THEN
            ReplaceInstruction gen1 = new ReplaceInstruction("JEQ #" + "---" + " -$-2"); //etiqfin
            prgMaker.addInstruction(gen1);
            tree.getChildXTree(1).traitement(this);

            etiqfin="ins_"+prgMaker.getNextIndex();
            prgMaker.addEtiquette(etiqfin);

            gen1.setReplaceWith(etiqfin);
        }
        else { // IF THEN ELSE
            String etiqelse;
            ReplaceInstruction gen2 = new ReplaceInstruction("JEQ #" + "---" + " -$-2"); //etiqelse
            prgMaker.addInstruction(gen2);
            tree.getChildXTree(1).traitement(this);
            ReplaceInstruction gen3 = new ReplaceInstruction("JMP #" + "---" + " -$-2"); //etiqfin
            prgMaker.addInstruction(gen3);

            etiqelse="ins_"+prgMaker.getNextIndex();
            prgMaker.addEtiquette(etiqelse);
            tree.getChildXTree(2).traitement(this);

            etiqfin="ins_"+prgMaker.getNextIndex();
            prgMaker.addEtiquette(etiqfin);

            gen2.setReplaceWith(etiqelse);
            gen3.setReplaceWith(etiqfin);
        }
    }

    @Override
    public void traitementIf(XTree tree) {
        tree.getChildXTree(0).traitement(this);
    }

    @Override
    public void traitementThen(XTree tree) {
        tree.getChildXTree(0).traitement(this);

    }

    @Override
    public void traitementElse(XTree tree) {
        tree.getChildXTree(0).traitement(this);
    }

    @Override
    public void traitementWhile(XTree tree) {
        String idWhile = Integer.toString(tree.hashCode());
        tree.getChildXTree(0).traitement(this);
        String etiqdebut;
        String etiqfin;

        etiqdebut="ins_"+prgMaker.getNextIndex();
        prgMaker.addEtiquette(etiqdebut);
        tree.getChildXTree(0).traitement(this);
        ReplaceInstruction gen2 = new ReplaceInstruction("JEQ #--- -$-2");
        prgMaker.addInstruction(gen2);
        tree.getChildXTree(1).traitement(this);
        ReplaceInstruction gen3 = new ReplaceInstruction("JMP #--- -$-2");
        prgMaker.addInstruction(gen3);
        etiqfin="ins_"+prgMaker.getNextIndex();
        prgMaker.addEtiquette(etiqfin);

        gen2.setReplaceWith(etiqfin);
        gen3.setReplaceWith(etiqdebut);
    }

    @Override
    public void traitementFor(XTree tree) {

        String idfor = Integer.toString(tree.hashCode());
        tree.getChildXTree(0).traitement(this);
        String etiqdebut = "for_debut_" + idfor;
        String etiqfin = "for_fin_" + idfor;

       //ADQ -2,SP
        prgMaker.addSimpleInstruction("ADQ -2,SP");

        //Aller à gauche fils 1
        tree.getChildXTree(1).traitement(this);

        // STW R0,(SP)+2
        prgMaker.addSimpleInstruction("STW R0,(SP)2");

        //aller à droite (2)
        tree.getChildXTree(2).traitement(this);

        //réserver un registre j
        boolean newRegion = false;
        int j = rController.getFreeRegistre();
        newRegion = j == -1;
        if (newRegion) {
            newRegisterRegion();
            j = rController.getFreeRegistre();
        }
        rController.useRegistre(j);



        //LDW Rj,R0
        prgMaker.addSimpleInstruction("LDW R"+j+",R0");

        // LDW R0,(SP)+2   etiq_debut
        prgMaker.addEtiquette(etiqdebut);
        prgMaker.addSimpleInstruction("LDW R0,(SP)2");

        //CMP R0,Rj
        prgMaker.addSimpleInstruction("CMP R0,R"+j);

        //JGT $ -2 -fin_boucle
        prgMaker.addSimpleInstruction("JGT #" + etiqfin+"-$-2");

        //Aller dans le noeud 3
        tree.getChildXTree(3).traitement(this);

        //incrémenter i
        prgMaker.addSimpleInstruction("LDW R0, (SP)2");
        prgMaker.addSimpleInstruction("ADQ 1, R0");
        prgMaker.addSimpleInstruction("STW R0, (SP)2");


        //JMP $ -2 -debut_boucle
        prgMaker.addSimpleInstruction("JMP #" + etiqdebut + "-$-2");

        //dépile le comteur etiq_fin ADQ 2, SP
        prgMaker.addEtiquette(etiqfin);
        prgMaker.addSimpleInstruction("ADQ 2,SP");
        rController.freeRegistre(j);
        if (newRegion) {
            stopRegisterRegion();
        }

    }

    @Override
    public void traitementAffect(XTree tree) {
        traitemen_affect(tree.getChildXTree(0),tree.getChildXTree(tree.getChildCount()-1));
    }

    @Override
    public void traitementNegation(XTree tree) {
        tree.getChildXTree(0).traitement(this);
        prgMaker.addSimpleInstruction("LDQ 0,UT");
        prgMaker.addSimpleInstruction("SUB UT,R0,R0");
    }

    @Override
    public void traitementSeqExp(XTree tree) {
        if (tree.getChildCount() == 0) {
            prgMaker.addSimpleInstruction("LDW R0, #0");
        }
        else{
            for (int i=0;i<tree.getChildCount() ; i++){
                tree.getChildXTree(i).traitement(this);
            }
        }
    }

    @Override
    public void traitementTabAcess(XTree tree) {
        tree.getChildXTree(0).traitement(this);

        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);


        prgMaker.addSimpleInstruction("LDW R"+i+",R0");
        tree.getChildXTree(1).traitement(this);

        prgMaker.addSimpleInstruction("LDW UT, #2");
        prgMaker.addSimpleInstruction("MUL UT,R0,R0");
        prgMaker.addSimpleInstruction("ADD R"+i+",R0,UT");
        prgMaker.addSimpleInstruction("LDW R0,(UT)");

        rController.freeRegistre(i);
        if (newRegion) {
            stopRegisterRegion();
        }

    }

    @Override
    public void traitementFieldAcess(XTree tree) {
        //IdentifierVar idField = (IdentifierVar)tds.getIdentifier(tree.getChildXTree(0).getText(),tree.getRegion());
        TypeRecord tyRec = (TypeRecord)tree.getChildXTree(0).getTypeOfNode().getType();
       // TypeRecord tyRec = (TypeRecord)idField.getIdentifierType().getType();
        IdentifierVar reco = tyRec.findIdentifierVar(tree.getChildXTree(1).getText());

        int offset = reco.getOffset()-4;

        tree.getChildXTree(0).traitement(this);

        prgMaker.addSimpleInstruction("LDW UT, R0");
        prgMaker.addSimpleInstruction("LDW R0, #"+offset);
        prgMaker.addSimpleInstruction("ADD R0, UT, UT");
        prgMaker.addSimpleInstruction("LDW R0 , (UT)");
    }

    @Override
    public void traitementArrCreate(XTree tree) {
        //Let's first find which kind of array?
        IdentifierType identifierType = (IdentifierType)tds.getIdentifier(tree.getChildXTree(0).getText(), tree.getRegion());

        TypeArray typeArray = (TypeArray)identifierType.getType();
        int sizeOfType = typeArray.getTypeOfTab().getSize();

        sizeOfType = 2; //since all types must have 2...

        //Let's first determine how many elements.
        tree.getChildXTree(1).traitement(this);

        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);


        prgMaker.addSimpleInstruction("LDW R"+i+",R0");
        prgMaker.addSimpleInstruction("LDW UT, TA");
        prgMaker.addSimpleInstruction("LDW R0, #2");
        prgMaker.addSimpleInstruction("MUL R0, R"+i+",R"+i);
        prgMaker.addSimpleInstruction("ADD R"+i+", TA, TA");
        prgMaker.addSimpleInstruction("LDW R"+i+",UT");

        tree.getChildXTree(2).traitement(this);


        String debutLoop = "loop_s_"+tree.hashCode();
        String finLoop = "loop_f_"+tree.hashCode();

        prgMaker.addEtiquette(debutLoop);
        prgMaker.addSimpleInstruction("CMP R"+i+",TA");
        prgMaker.addSimpleInstruction("BGE "+finLoop+"-$-2");
        prgMaker.addSimpleInstruction("STW R0, (R"+i+")");
        prgMaker.addSimpleInstruction("ADQ 2, R"+i);
        prgMaker.addSimpleInstruction("BMP "+debutLoop+"-$-2");

        prgMaker.addEtiquette(finLoop);
        prgMaker.addSimpleInstruction("LDW R0, UT");
        rController.freeRegistre(i);
        if (newRegion) {
            stopRegisterRegion();
        }

    }

    @Override
    public void traitementCallExp(XTree tree) {
        String funcName = tree.getChildXTree(0).getText();
        int idRegionCourante = tree.getRegion();
        IdentifierFunction identifierFunction = (IdentifierFunction)tds.getIdentifier(funcName, idRegionCourante);

        int idRegionFunction = tds.getRegionOfIdentifier(funcName, idRegionCourante);

        //ve enter a new region...
        newRegisterRegion();

        int S = 0;
        for (int i = identifierFunction.getNbParam()-1 ; i >= 0; i--) {
            IdentifierParam identifierParam = identifierFunction.getParam().get(i);


            tree.getChildXTree(i+1).traitement(this);
            prgMaker.addSimpleInstruction("STW R0, (SP)");
            prgMaker.addSimpleInstruction("ADQ -2, SP");

            S+=identifierParam.getSize();
        }

        String etiquetteFonction = "fun_" +  identifierFunction.getStringId() + "_" + idRegionFunction;

        //Let's do static chain
        if (identifierFunction.regionNumber > 0) {
            prgMaker.addSimpleInstruction("ADQ -4, SP");
            int n = -tds.getRegion(identifierFunction.regionNumber).getImbricationNumber() + tds.getRegion(tree.getRegion()).getImbricationNumber()+1;
            prgMaker.addSimpleInstruction("LDW UT, BP");
            for (int i = 0 ; i < n ; i++) {
                prgMaker.addSimpleInstruction("ADQ -2, UT");
                prgMaker.addSimpleInstruction("LDW UT, (UT)");
            }
            prgMaker.addSimpleInstruction("STW UT, (SP)");
            prgMaker.addSimpleInstruction("ADQ 4, SP");
        }
        prgMaker.addSimpleInstruction("ADQ 2, SP");
        prgMaker.addSimpleInstruction("JSR @" + etiquetteFonction);
        prgMaker.addSimpleInstruction("ADQ -2, SP");
        if (S > 0) {
            prgMaker.addSimpleInstruction("LDW UT, #" + S);
            prgMaker.addSimpleInstruction("ADD UT, SP, SP");
        }

        stopRegisterRegion();
    }

    @Override
    public void traitementRecCreate(XTree tree) {
        IdentifierType identifierType = (IdentifierType)tds.getIdentifier(tree.getChildXTree(0).getText(), tree.getRegion());

        TypeRecord typeRecord = (TypeRecord)identifierType.getType();

        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);


        prgMaker.addSimpleInstruction("LDW R"+i+",TA");
        prgMaker.addSimpleInstruction("LDW R0, #"+typeRecord.getNbFields()*2);
        prgMaker.addSimpleInstruction("ADD R0, TA, TA");


        for (int j = 0 ; j < typeRecord.getNbFields() ; j++) {
            IdentifierVar identifierVar = typeRecord.getIdentifierVar(j);
            tree.getChildXTree(j+1).traitement(this);
            prgMaker.addSimpleInstruction("STW R0, (R"+i+")");
            prgMaker.addSimpleInstruction("ADQ 2, R"+i);
        }

        prgMaker.addSimpleInstruction("LDW R0, #-"+typeRecord.getNbFields()*2);
        prgMaker.addSimpleInstruction("ADD R"+i+", R0, R0");


        rController.freeRegistre(i);
        if (newRegion) {
            stopRegisterRegion();
        }
    }

    @Override
    public void traitementField(XTree tree) {
        tree.getChildXTree(1).traitement(this);
    }



    @Override
    public void traitementIdentifier(XTree tree) {
        //WARNING WARNING WARNING tds.getCurrentRegion() à proscrire!
        IdentifierVar identifier = (IdentifierVar) tds.getIdentifier(tree.getText(), tree.getRegion());
        Region regionCourante = tds.getRegion(tree.getRegion());
        Region regionDeLidentifier = tds.getRegion(tds.getRegionOfIdentifier(tree.getText(), tree.getRegion()));
        int n = regionCourante.getImbricationNumber() - regionDeLidentifier.getImbricationNumber(); //Faux probablement car il faut calculer l'imbrication entree current region  et la region ou on a stocké la variable
        int x = identifier.getOffset();

        if (n == -1) {
            fclass.quickGetParam(x);
        } else {
        prgMaker.addSimpleInstruction("LDW R0, BP");
            for (int i = 0; i < n; i++) {
                prgMaker.addSimpleInstruction("ADQ -2, R0");
                prgMaker.addSimpleInstruction("LDW R0, (R0)");

            }
            prgMaker.addSimpleInstruction("LDW UT, #" + (-x));
            prgMaker.addSimpleInstruction("ADD R0, UT, UT");
            prgMaker.addSimpleInstruction("LDW R0, (UT)");
        }
    }

    @Override
    public void traitementTypeInt(XTree tree) {

    }

    @Override
    public void traitementString(XTree tree) {
        prgMaker.addHeader("STR"+tree.hashCode() + " string \"" + tree.getText() +"\"");
        prgMaker.addSimpleInstruction("LDW R0,#STR"+tree.hashCode());
    }

    @Override
    public void traitementTypeString(XTree tree) {

    }

    @Override
    public void traitementInteger(XTree tree) {
        prgMaker.addSimpleInstruction( "LDW R0, #"+tree.getText());
    }

    @Override
    public void traitementPlus(XTree tree) {
        this.SimpleBinaryInstruction(tree,"ADD");
    }

    @Override
    public void traitementMoin(XTree tree) {
        this.SimpleBinaryInstruction(tree,"SUB");
    }

    @Override
    public void traitementMult(XTree tree) {
        this.SimpleBinaryInstruction(tree,"MUL");
    }

    @Override
    public void traitementDiv(XTree tree) {
        this.SimpleBinaryInstruction(tree,"DIV");

    }

    @Override
    public void traitementOr(XTree tree) {
        this.SimpleBinaryInstruction(tree,"OR");

    }

    @Override
    public void traitementAnd(XTree tree) {
        this.SimpleBinaryInstruction(tree,"AND");

    }

    @Override
    public void traitementInf(XTree tree) {
        CompareBinaryInstruction(tree,"BLW");
    }

    @Override
    public void traitementInfEqual(XTree tree) {
        CompareBinaryInstruction(tree,"BLE");
    }

    @Override
    public void traitementSup(XTree tree) {
        CompareBinaryInstruction(tree,"BGT");
    }

    @Override
    public void traitementSupEqual(XTree tree) {
        CompareBinaryInstruction(tree,"BGE");
    }

    @Override
    public void traitementUnequal(XTree tree) {
        CompareBinaryInstruction(tree,"BNE");
    }

    @Override
    public void traitementEqual(XTree tree) {
        CompareBinaryInstruction(tree,"BEQ");
    }

    @Override
    public void traitementNil(XTree tree) {
        prgMaker.addSimpleInstruction("LDQ 0, R0");
    }

    public void traitemen_affect(XTree gauche, XTree droite){
        gauche.traitement(this);

        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);

        prgMaker.addSimpleInstruction("LDW R"+i+",UT");
        droite.traitement(this);
        prgMaker.addSimpleInstruction("STW R0,(R"+i+")");

        rController.freeRegistre(i);
        if (newRegion) {
            stopRegisterRegion();
        }
    }

    //Exemple où on veut avoir un registre...
    public void exempleRegistre() {
        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);


        //We do whatever we want...


        rController.freeRegistre(i);
        if (newRegion) {
            stopRegisterRegion();
        }
    }

    public void newRegisterRegion() {
        prgMaker.addInstruction(rController.getSaveInstruction());
        rController.entrerRegion();
    }

    public void stopRegisterRegion() {
        rController.sortirRegion();
        prgMaker.addInstruction(rController.getRestoreInstruction());
    }

    public void exempleAccessTDS(XTree tree) {
        String idString = "nom_variable";
        Identifier id = tds.getIdentifier(idString, tree.getRegion());
    }
    public void SimpleBinaryInstruction(XTree tree ,String op){
        tree.getChildXTree(0).traitement(this);
        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == 0;
        if (newRegion) {
            newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);
        prgMaker.addSimpleInstruction( "LDW R"+i+", R0");
        tree.getChildXTree(1).traitement(this);
        prgMaker.addSimpleInstruction( op+ " " +"R"+i+",R0, R0");
        rController.freeRegistre(i);
        if (newRegion) {
            stopRegisterRegion();
        }

    }

    public void CompareBinaryInstruction(XTree tree ,String op){
        tree.getChildXTree(0).traitement(this);
        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);
            prgMaker.addSimpleInstruction("STW R0, R" + Integer.toString(i));
        tree.getChildXTree(1).traitement(this);

        prgMaker.addSimpleInstruction("CMP " + "R" + Integer.toString(i) + ", R0");
            rController.freeRegistre(i);
            prgMaker.addSimpleInstruction(op+" 4");
            prgMaker.addSimpleInstruction("LDQ 0, R0");
            prgMaker.addSimpleInstruction("BMP 2");
            prgMaker.addSimpleInstruction("LDQ 1, R0");
            rController.freeRegistre(i);

        if (newRegion) {
            stopRegisterRegion();
        }

    }




    private RegisterController getRegisterController(){
        return rController;
    }

}
