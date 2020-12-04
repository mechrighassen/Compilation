import AST.*;
import Generation.ASTGeneration;
import Semantique.TDS;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.StringTemplate;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.cli.*;

public class Main {

    public static int compareError(String s1, String s2) {
        if (s1.startsWith("Line ") && s2.startsWith("Line ")) {
            int s1i = new Scanner(s1).useDelimiter("\\D+").nextInt();
            int s2i = new Scanner(s2).useDelimiter("\\D+").nextInt();
            return Integer.compare(s1i, s2i);
        }
        else return 0;
    }

    public static String readFile(String fileName) throws FileNotFoundException {
        return (new Scanner(new File(fileName)).useDelimiter("\\Z")).next();
    }

    public static void main(String[] args) throws Exception {

        //On crée une classe Options qui contient les options à reconnaître
        Options options = new Options();

        Option pngOption = new Option("png",true,"Save AST as png.");
        pngOption.setRequired(false);
        options.addOption(pngOption);

        Option spngOption = new Option("spng",false,"Show AST as png.");
        spngOption.setRequired(false);
        options.addOption(spngOption);

        Option printOption = new Option("print",false, "Print ast as string");
        printOption.setRequired(false);
        options.addOption(printOption);

        Option tdsOption = new Option("tds", false, "Print the TDS");
        tdsOption.setRequired(false);
        options.addOption(tdsOption);


        //On parse la ligne de commande
        CommandLineParser lineParser = new DefaultParser();
        CommandLine cmd;


        //On récupère le fichier en entré (cad l'argument qui n'est pas dans une option)
        try {
            cmd = lineParser.parse(options, args);
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (cmd.getArgs().length == 0) {
            System.out.println("Missing input file.");
            return;
        }
        String inputFile = cmd.getArgs()[0];

        //On lit le fichier d'entré et on met son contenu dans src
        String src;
        try {
            src = readFile(inputFile);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found : " + e.getMessage());
            return;
        }

        //On crée l'AST correspondant au programme source
        XTree astTree;
        try {
            astTree = TreeBuilder.getxtree(src);
        }
        catch (RecognitionException e) {
            System.out.println(e.getMessage());
            return;
        }
        catch (ClassCastException e) {
            return;
        }

        //Si on a l'option print, alors on affiche l'AST sous forme de string
        if (cmd.hasOption("print")) {
            System.out.println("AST as String : " + astTree.toStringTree() + "\n");
        }

        //On convertit l'AST en notation .dot
        DOTTreeGenerator gen = new DOTTreeGenerator();
        StringTemplate st = gen.toDOT(astTree);

        String pngOptionValue = cmd.getOptionValue("png");
        boolean spngOptionValue = cmd.hasOption("spng");

        if (pngOptionValue != null || spngOptionValue) {
            //Dans ce if, on a soit l'option -png soit l'option -spng soit les deux
            //Dans les deu cas, il faut écrire la notation dot dans un fichier .dot
            PrintWriter writer = new PrintWriter("/tmp/2849289.dot");
            writer.print(st.toString());
            writer.close();

            Process p;
            if (pngOptionValue != null) {
                //Dans ce cas, on a l'option -png, donc on crée le png dans le fichier demandé en argument
                p = Runtime.getRuntime().exec("dot -Tpng " + "/tmp/2849289.dot" + " -o " + pngOptionValue);
                p.waitFor();
            }
            else {
                //Dans ce cas, on a pas l'option -png, donc on crée le png dans un fichier temporaire
                p = Runtime.getRuntime().exec("dot -Tpng " + "/tmp/2849289.dot" + " -o " + "/tmp/2849289.png");
                p.waitFor();
            }

            //On supprime le .dot temporaire
            p=Runtime.getRuntime().exec("rm /tmp/2849289.dot");
            p.waitFor();

            if (pngOptionValue == null) {
                //Dans ce cas, on a juste l'option -spng, donc on affiche le png et on le détruit
                p=Runtime.getRuntime().exec("xviewer /tmp/2849289.png");
                p.waitFor();
                p=Runtime.getRuntime().exec("rm /tmp/2849289.png");
                p.waitFor();
            }
            else if (spngOptionValue) {
                //Dans ce cas, on a l'option -png et l'option -spng, donc on affiche le png et on le détruit pas
                p=Runtime.getRuntime().exec("xviewer " + pngOptionValue);
                p.waitFor();
            }
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer);
        PrintStream console = System.out;
        System.setOut(stream);

        TDS tds = new TDS();
        TDSDiscoverType visitor = new TDSDiscoverType(tds);
        astTree.traitement(visitor);
        TDSDiscoverFunction visitor2 = new TDSDiscoverFunction(tds);
        astTree.traitement(visitor2);
        TDSDiscoverVariable visitor3 = new TDSDiscoverVariable(tds);
        astTree.traitement(visitor3);

        System.setOut(console);
        stream.close();
        buffer.close();

        if (visitor.hasError() || visitor2.hasError() || visitor3.hasError()) {
            String errors[] = buffer.toString().split("\n");
            String lines[] = src.split("\n");
            Arrays.sort(errors, (s1, s2) -> compareError(s1, s2));
            for (String s : errors) {
                int l = new Scanner(s).useDelimiter("\\D+").nextInt();
                System.out.println(s);
                System.out.print("    ");
                System.out.print(lines[l-1]);
                System.out.println('\n');
            }
            System.out.println("Errors were found!");
            return;
        }
        if (cmd.hasOption("tds")) {
            System.out.println(tds.toString());

            PrintWriter writer = new PrintWriter("/tmp/2849289.dot");
            writer.print(tds.toDot());
            writer.close();
            Process p;
            p = Runtime.getRuntime().exec("dot -Tpng " + "/tmp/2849289.dot" + " -o " + "/tmp/2849289.png");
            p.waitFor();
            p=Runtime.getRuntime().exec("xviewer /tmp/2849289.png");
            p.waitFor();
            p=Runtime.getRuntime().exec("rm /tmp/2849289.png");
            p.waitFor();
        }

        //Finally...Finally!

        ASTGeneration astGeneration = new ASTGeneration(tds);
        astTree.traitement(astGeneration);
        String codeAss = astGeneration.getPrgMaker().getASM();

        PrintWriter printWriter = new PrintWriter(new File(inputFile + ".src"));
        printWriter.println(codeAss);
        printWriter.close();


        String arg[] = new String[2];
        arg[0]="-ass";
        arg[1]=inputFile+".src";
        projetIUP.Lanceur.main(arg);

        System.setOut(console);
        arg[0]="-batch";
        arg[1]=inputFile+".iup";
        projetIUP.Lanceur.main(arg);
    }
}