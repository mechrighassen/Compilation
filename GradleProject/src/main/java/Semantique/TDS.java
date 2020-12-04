package Semantique;

import java.util.*;

public class TDS {
    Deque<Integer> regionOuvertes; // Pile des régions ouvertes
    ArrayList<Region> regions; //Régions
    int lastRegion; //L'index de la dernière région visité dans this.regions
                    ///N'est pas forcément égale à regions.size()-1 puisque l'on
                    //peut parcourir plusieurs fois la TDS

    public TDS() {
        regionOuvertes = new ArrayDeque<>();
        regions = new ArrayList<>();
        lastRegion=-1;
    }

    /**
     * Entre dans une région, en supposant que la dernière région visité est d'indice lastRegion.
     * Incrémente lastRegion et, si la région que l'on visite maintenant n'a pas encore été visitée
     * on crée cette nouvelle région.
     */
    public void entrerRegion() {
        lastRegion++;
        if (lastRegion >= regions.size()) {
            if (regionOuvertes.isEmpty()) regions.add(new Region(lastRegion));
            else regions.add(new Region(lastRegion, regions.get(regionOuvertes.peek())));
        }
        regionOuvertes.push(lastRegion);
    }

    public void sortirRegion() {
        regionOuvertes.pop();
    }

    public void nouveauParcours() {
        lastRegion=-1;
        regionOuvertes.clear();
    }

    public void addIdentifier(Identifier id, int line) throws IdentifierAlreadyUsed {
        if (regionOuvertes.isEmpty()) throw new EmptyStackException();
        else addIdentifier(id, line, regionOuvertes.peek());
    }

    public void addIdentifier(Identifier id, int line, int idRegion) throws IdentifierAlreadyUsed {
       regions.get(idRegion).addIdentifier(id, line);
    }

    public boolean tryAddIdentifier(Identifier id, int line, List<Identifier> poubelle) {
        try {
            addIdentifier(id, line);
            return false;
        }
        catch (IdentifierAlreadyUsed e) {
            System.out.println(e.getMessage());
            poubelle.add(id);
        }
        return true;
    }

    public Identifier getIdentifier(String id) {
        if (regionOuvertes.isEmpty()) return null;
        else return getIdentifier(id, regionOuvertes.peek());
    }

    public Region getRegion(int idRegion) {
        return regions.get(idRegion);
    }

    public int getCurrentRegionId() {
        if (regionOuvertes.isEmpty()) return -1;
        else return regionOuvertes.peek();
    }

    public Region getCurrentRegion() {
        if (regionOuvertes.isEmpty()) return null;
        else return regions.get(regionOuvertes.peek());
    }

    public Identifier getIdentifier(String id, int idRegion) {
        Identifier identifier=null;
        Region region = regions.get(idRegion);

        while (identifier == null && region != null) {
            identifier = region.getIdentifier(id);
            region=region.getParent();
        }

        return identifier;
    }

    public int getRegionOfIdentifier(String id, int idRegion) {
        int i = idRegion;
        Region region = regions.get(i);

        while (region != null) {
            Identifier identifier = region.getIdentifier(id);
            if (identifier != null) return  i;
            region = region.getParent();
            if (region != null) i = region.getRegionNumber();
        }

        return -1;
    }

    public Collection<Region> getRegions() {
        return regions;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Region r : regions) {
            builder.append("Region " + r.getRegionNumber() + " " + r.getImbricationNumber()+"\n");
            builder.append(r.toString());
            builder.append('\n');
        }
        return builder.toString();
    }

    public String toDot() {
        StringBuilder dotNotation = new StringBuilder();

        dotNotation.append("digraph {\n" +
                "    graph [pad=\"0.5\", nodesep=\"0.5\", ranksep=\"2\"];\n" +
                "    node [shape=plain]\n" +
                "    rankdir=TB;");

        for (Region r : regions) {
            dotNotation.append(r.toDot());
        }

        for (Region r : regions) {
            if (r.getParent() != null) {
                dotNotation.append("Region"+r.getParent().getRegionNumber()+
                        "->" + "Region"+r.getRegionNumber()+";\n");
            }
        }
        dotNotation.append("}");
        return dotNotation.toString();
    }
}
