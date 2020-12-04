package Semantique;


import java.util.Collection;
import java.util.HashMap;

public class Region {

    HashMap<String,Identifier> mapIdentifier;
    private Region parent;
    private int id;
    private IdentifierVar lastAdded = null;
    private boolean newEnvironnement = false;

    public Region(int id) {
        this(id,null);
    }

    public Region(int id, Region parent) {
        mapIdentifier=new HashMap<>();
        this.parent=parent;
        this.id=id;
    }

    public void setLastAdded(IdentifierVar v) {
        lastAdded = v;
        if (lastAdded != null && lastAdded.getOffset() < 0) {
            lastAdded = null;
        }
    }

    public IdentifierVar getLastAdded() {
        return lastAdded;
    }

    public void setNewEnvironnement(boolean b) {
        newEnvironnement = b;
    }

    public int getTotalSize() {
        int S=0;
        for (Identifier id : this.getIdentifier()) {
            if (id instanceof IdentifierVar && !(id instanceof IdentifierParam)) {
                S+=((IdentifierVar)id).getSize();
            }
        }
        return S;
    }

    public boolean getNewEnvironnement() {
        return newEnvironnement;
    }

    public void addIdentifier(Identifier id, int line) throws IdentifierAlreadyUsed {
        if (mapIdentifier.get(id.getStringId()) == null) {
            mapIdentifier.put(id.getStringId(),id);
            if (id.getIdentifierKind() == IdentifierKind.VAR) {
                ((IdentifierVar)id).setPrecedentVar(lastAdded);
                lastAdded=(IdentifierVar)id;
            }
        }
        else {
            throw new IdentifierAlreadyUsed(id.getStringId(), line);
        }
    }

    public Collection<Identifier> getIdentifier() {
        return mapIdentifier.values();
    }

    public int getRegionNumber() {
        return id;
    }

    public Identifier getIdentifier(String id) {
        return mapIdentifier.get(id);
    }

    public Region getParent() {

        return parent;
    }

    public int getImbricationNumber(){
        if (parent==null) {
            return 0;
        }else {
           if (newEnvironnement) return parent.getImbricationNumber()+1;
            else return parent.getImbricationNumber();
        }

    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Identifier i : mapIdentifier.values()) {
            builder.append(i.toString());
            builder.append('\n');
        }
        return builder.toString();
    }

    public String toDot(){

        StringBuilder dotation = new StringBuilder();

        dotation.append("Region");
        dotation.append(this.getRegionNumber());
        dotation.append("[label=<\n" +
                "<table border=\"0\" cellborder=\"1\" cellspacing=\"0\">");
        dotation.append("<tr>");
        dotation.append("<td>");
        dotation.append("Region"+this.getRegionNumber());
        dotation.append("_");
        dotation.append(this.getImbricationNumber());
        dotation.append("</td>");
        dotation.append("</tr>");
        for (Identifier i : mapIdentifier.values()) {
            dotation.append(i.toDot());
            dotation.append('\n');
        }
        dotation.append("</table>>];");
        return dotation.toString();
    }
}
