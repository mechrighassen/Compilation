package Semantique;
import java.util.ArrayList;

public class IdentifierFunction extends Identifier {
    private String stringID;
    public ArrayList<IdentifierParam> param= new ArrayList<IdentifierParam>();
    public int nbParam;
    public IdentifierType typeRetour;
    public int regionNumber;


    public IdentifierFunction(String id, IdentifierType type, int regionNumber) {
        stringID=id;
        typeRetour=type;
        this.regionNumber=regionNumber;
    }

    public IdentifierKind getIdentifierKind(){
        return IdentifierKind.FUNCTION;
    }

    public String getStringId(){
        return stringID;
    }

    public IdentifierType getTypeRetour(){
        return this.typeRetour;
    }

    public void setStringId(String s){
        this.stringID=s;
    }


    public int getNbParam(){

        return param.size();
    }

    public IdentifierParam findIdentifierVar(String s) {
        for (IdentifierParam v : param) {
            if (v.getStringId().equals(s)) {
                return v;
            }
        }
        return null;
    }

    public void addParam(IdentifierParam v, int line) throws IdentifierAlreadyUsed {
        if (findIdentifierVar(v.getStringId()) != null) {
            param.add(v); //On l'ajoute malgré tout...pour vérifier plus tard si le type existe
            throw new IdentifierAlreadyUsed(v.getStringId(),line);
        }
        else {
            if (param.size() > 0) {
                v.setPrecedentVar(param.get(param.size() - 1));
            }
            param.add(v);
        }
    }

    public String toStringForDot(){
        this.nbParam=param.size();
        String res;
        res="Params:";
        for (IdentifierParam p : param){

            res=res+ "[name:"+p.getStringId()+", type:"+p.getIdentifierType().toShortStringForDot()+"]"+",";
        }
        res=res +"]";
        return this.getTypeRetour().toShortStringForDot() + " | " + res;
    }
    public String toStringWithoutDot(){
        this.nbParam=param.size();
        String res;
        res="Params:";
        for (IdentifierParam p : param){

            res=res+ "[name:"+p.getStringId()+", type:"+p.getIdentifierType().toShortStringWithoutDot()+"]"+",";
        }
        res=res +"]";
        return this.getTypeRetour().toShortStringWithoutDot() + " | " + res;
    }

    public ArrayList<IdentifierParam> getParam() {
        return param;
    }
}
