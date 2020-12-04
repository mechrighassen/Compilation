package Generation;

import AST.XTree;
import Generation.Instruction.IdentifierAccess;
import Generation.Instruction.PrintIFunction;
import Generation.Instruction.TrapInstruction;
import Generation.Instruction.ReadInstruction;
import Semantique.IdentifierFunction;
import Semantique.IdentifierParam;
import Semantique.TDS;

public class FunctionClass {

    private ProgrammeMaker prgMaker;
    private TDS tds;


    public FunctionClass(TDS tds, ProgrammeMaker prgMaker){
        this.prgMaker=prgMaker;
        this.tds=tds;
    }


    private void beginningFunction(String funName) {
        int regionParente = 0;
        int regionCourante = 0;
        int n = tds.getRegion(regionCourante).getImbricationNumber()-tds.getRegion(regionParente).getImbricationNumber()+1;


        prgMaker.addEtiquette("fun_"+funName+"_"+regionParente);
        prgMaker.addSimpleInstruction("ADQ -2, SP");
        prgMaker.addSimpleInstruction("STW BP, (SP)");
        prgMaker.addSimpleInstruction("LDW BP, SP");
        prgMaker.addSimpleInstruction("ADQ -4, SP");
    }

    private void endFunction(String funName) {
        prgMaker.addSimpleInstruction("ADQ 4, SP");
        prgMaker.addSimpleInstruction("LDW BP, (SP)");
        prgMaker.addSimpleInstruction("ADQ 2, SP");
        prgMaker.addSimpleInstruction("RTS");
    }

    public void printFunction() {
        beginningFunction("print");

        IdentifierFunction printFun = (IdentifierFunction)tds.getIdentifier("print", 0);
        IdentifierParam strParam = (IdentifierParam)printFun.findIdentifierVar("str");

        quickGetParam(strParam.getOffset());

        prgMaker.addInstruction(new TrapInstruction(66));

        endFunction("print");
    }

    public void printIFunction() {
        beginningFunction("printi");

        prgMaker.addInstruction(new PrintIFunction());

        endFunction("printi");
    }

    public void readInstruction() {
        beginningFunction("read");

        prgMaker.addInstruction(new ReadInstruction());

        endFunction("read");
    }

    public void beginningFunction(XTree tree) {
        int regionParente = tree.getRegion();
        int regionCourante = tree.getChildXTree(tree.getChildCount()-1).getRegion();
        int n = tds.getRegion(regionCourante).getImbricationNumber()-tds.getRegion(regionParente).getImbricationNumber()+1;

        //System.out.println(n);
        prgMaker.addEtiquette("fun_"+tree.getChildXTree(0).getText()+"_"+regionParente);
        prgMaker.addSimpleInstruction("ADQ -2, SP");
        prgMaker.addSimpleInstruction("STW BP, (SP)");
        prgMaker.addSimpleInstruction("LDW BP, SP");
        prgMaker.addSimpleInstruction("ADQ -4, SP");
    }

    public void endFunction(XTree tree) {
        prgMaker.addSimpleInstruction("ADQ 4, SP");
        prgMaker.addSimpleInstruction("LDW BP, (SP)");
        prgMaker.addSimpleInstruction("ADQ 2, SP");
        prgMaker.addSimpleInstruction("RTS");
    }

    public void quickGetParam(int offset) {
        prgMaker.addInstruction(new IdentifierAccess(0, offset));
    }

}
