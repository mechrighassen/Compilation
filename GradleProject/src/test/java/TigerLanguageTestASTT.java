package AST;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.StringTemplate;

import java.io.*;
import java.net.URL;
import java.util.Scanner;
import org.junit.Test;

public class TigerLanguageTestASTT {



    public static void TestProgram(String sample) throws Exception {
            URL url = TigerLanguageTestASTT.class.getResource(sample);
            String src = new Scanner(new File(url.getFile())).useDelimiter("\\Z").next();
            //System.out.println(src);

            AST.TigerLanguageLexer lexer = new AST.TigerLanguageLexer(new ANTLRStringStream(src));
            AST.TigerLanguageParser parser = new AST.TigerLanguageParser(new CommonTokenStream(lexer));
           parser.setTreeAdaptor(new CommonTreeAdaptor(){
            @Override
            public Object create(Token t) {
                return new XTree(t);
            }
            });
            XTree tree = parser.prog().getTree();
            //CommonTree tree = (CommonTree)parser.prog().getTree();
            DOTTreeGenerator gen = new DOTTreeGenerator();
            StringTemplate st = gen.toDOT(tree);


            PrintWriter writer = new PrintWriter(url.getFile() + "_AST.dot");
            writer.print(st.toString());
            writer.close();

            Process p;
            p=Runtime.getRuntime().exec("dot -Tpng " + url.getFile() + "_AST.dot" + " -o " + url.getFile() + "_AST.png");
            p.waitFor();
    }

 /*   public static void parcourirTree(Tree t) {
        System.out.println("Type : "+t.getType() + "; Text : " + t.getText() + "\n");
        for (int i = 0 ; i < t.getChildCount(); i++) {
            parcourirTree(t.getChild(i));
        }
    }
*/
/*
    @Test public void test0() throws Exception {
        System.out.println("Test 0 : if then if then else : ");
        TestProgram("/test.tiger");
    }

    @Test public void test1() throws Exception {
        System.out.println("Test 1 : Programme principal, variables et structures de contrôle");
        TestProgram("/test1.tiger");
    }
    @Test public void test2() throws Exception {
        System.out.println("Test 2 : Appels de fonctions");
        TestProgram("/test2.tiger");
    }
    @Test public void test3() throws Exception {
        System.out.println("Test 3 : Récursivité et imbrication de blocs");
        TestProgram("/test3.tiger");
    }
    @Test public void test4() throws Exception {
        System.out.println("Test 4 : Définitions de types, tableaux et structures");
        TestProgram("/test4.tiger");
    }
    @Test public void test5() throws Exception {
        System.out.println("Test 5 : Bloc contenat un point-vergule après la dernière instruction");
        TestProgram("/testComma.tiger");
    }

    @Test public void test6() throws Exception {
        System.out.println("Test 6 : Le bloc in du let in end est vide");
        TestProgram("/testEmptyIn.tiger");
    }
    @Test public void test7() throws Exception {
        System.out.println("Test 7 : Tableau avec un caractère spécial comme indice d'une case " );
        TestProgram("/testLoop.tiger");
    }
*/
}