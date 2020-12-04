package AST;

import Semantique.IdentifierType;
import Semantique.Type;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

public class XTree extends CommonTree {

  private TraitementNode traitementNode;
  private IdentifierType typeOfNode;
  private int region;

  public XTree(Token t) {
    super(t);
    traitementNode = TraitementNodeFactory.createTraitementNode(this);
  }
  public void traitement(TraitementAST v) {
    traitementNode.traitement(v);
  }

  public XTree getChildXTree(int i) {
    return (XTree)this.getChild(i);
  }

  public IdentifierType getTypeOfNode() {
    return typeOfNode;
  }


  public void setTypeOfNode(IdentifierType type) {
    typeOfNode = type;
  }

  public int getRegion() {
    return region;
  }

  public void setRegion(int region) {
    this.region = region;
  }
}