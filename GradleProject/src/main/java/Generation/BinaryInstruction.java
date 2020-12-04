package Generation;

import AST.TigerLanguageLexer;
import AST.TigerLanguageParser;
import AST.TraitementAST;
import AST.XTree;
import Generation.Instruction.*;

public class BinaryInstruction {
	
	private static RegisterController rController;
    private static ProgrammeMaker prgMaker;
	

	public static void SimpleBinaryInstruction(XTree tree ,String op,ASTGeneration gen){
        tree.getChildXTree(0).traitement(gen);
        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            gen.newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);
        prgMaker.addSimpleInstruction( "LDW R"+i+", R0");
        tree.getChildXTree(1).traitement(gen);
        prgMaker.addSimpleInstruction( op+ " " +"R"+i+",R0, R0");
        rController.freeRegistre(i);
        if (newRegion) {
            gen.stopRegisterRegion();
        }

    }
	
	public static void CompareBinaryInstruction(XTree tree ,String op,ASTGeneration gen){
        tree.getChildXTree(0).traitement(gen);
        boolean newRegion = false;
        int i = rController.getFreeRegistre();
        newRegion = i == -1;
        if (newRegion) {
            gen.newRegisterRegion();
            i = rController.getFreeRegistre();
        }
        rController.useRegistre(i);
            prgMaker.addSimpleInstruction("STW R0, R" + Integer.toString(i));
        tree.getChildXTree(1).traitement(gen);

        prgMaker.addSimpleInstruction("CMP " + "R" + Integer.toString(i) + ", R0");
            rController.freeRegistre(i);
            prgMaker.addSimpleInstruction(op+" 4");
            prgMaker.addSimpleInstruction("LDQ 0, R0");
            prgMaker.addSimpleInstruction("BMP 2");
            prgMaker.addSimpleInstruction("LDQ 1, R0");
            rController.freeRegistre(i);

        if (newRegion) {
            gen.stopRegisterRegion();
        }

    }

}
