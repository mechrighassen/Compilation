import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import org.antlr.runtime.debug.*;
import java.io.*;
import java.util.Scanner; 


public class TigerLanguageTestAST {

  public static void parcourirTree(Tree t) {
    System.out.println("Type : "+t.getType() + "; Text : " + t.getText() + "\n");
    for (int i = 0 ; i < t.getChildCount(); i++) {
      parcourirTree(t.getChild(i));
    }
  }

  public static void main(String[] args) throws Exception {
  	if (args.length < 1) {
  		throw new Exception("No input file given.");
  	}
  	String src = new Scanner(new File(args[0])).useDelimiter("\\Z").next();
    System.out.println(src);

    ParseTreeBuilder builder = new ParseTreeBuilder("prog");

    TigerLanguageLexer lexer = new TigerLanguageLexer(new ANTLRStringStream(src));
    TigerLanguageParser parser = new TigerLanguageParser(new CommonTokenStream(lexer),builder);
    CommonTree tree = (CommonTree)parser.prog().getTree();

    DOTTreeGenerator gen = new DOTTreeGenerator();
    StringTemplate st = gen.toDOT(tree);

    PrintWriter writer = new PrintWriter(args[0] + "_AST.dot");
    writer.print(st.toString());
    writer.close();

//    parcourirTree(tree);

//    System.out.println(builder.getTree().toStringTree());

    Process p;
    p=Runtime.getRuntime().exec("dot -Tpng " + args[0] + "_AST.dot" + " -o " + args[0] + "_AST.png");
    p.waitFor();
    p=Runtime.getRuntime().exec("rm " + args[0] + "_AST.dot");
    p.waitFor();


    st = gen.toDOT(builder.getTree());

    writer = new PrintWriter(args[0] + "_PARSE.dot");
    writer.print(st.toString());
    writer.close();

//    parcourirTree(tree);

//    System.out.println(builder.getTree().toStringTree());

    p=Runtime.getRuntime().exec("dot -Tpng " + args[0] + "_PARSE.dot" + " -o " + args[0] + "_PARSE.png");
    p.waitFor();
    p=Runtime.getRuntime().exec("rm " + args[0] + "_PARSE.dot");
    p.waitFor();
  }
}
