package  AST;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;


public class TreeBuilder {

  /**
   * Create the AST Tree of a given Tiger program in input
   * @param source Source code of the Tiger program
   * @return A AST Tree of the Tiger program given in input, given as an XTree
   * @throws RecognitionException
   */
  public static XTree getxtree(String source) throws RecognitionException  {
    AST.TigerLanguageLexer lexer = new AST.TigerLanguageLexer(new ANTLRStringStream(source));
    AST.TigerLanguageParser parser = new AST.TigerLanguageParser(new CommonTokenStream(lexer));
    parser.setTreeAdaptor(new CommonTreeAdaptor(){
      @Override
      public Object create(Token t) {
        return new XTree(t);
      }
    });

    XTree root = parser.prog().getTree();
    return root;

  }

}
