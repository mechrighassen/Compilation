package Generation;

import Generation.Instruction.Instruction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class RegisterController {
    //Remarque : les régions de resigtre différent des régions dans la TDS!
    Deque<Integer> regionOuvertes; // Pile des régions ouvertes
    ArrayList<RegisterStatus> regions; //Régions
    int lastRegion=-1; //L'index de la dernière région visité dans this.regions

    public static int maxRegistre = 11;

    public RegisterController() {
        regionOuvertes = new ArrayDeque<>();
        regions = new ArrayList<>();
    }

    public void entrerRegion() {
        lastRegion++;
        if (lastRegion >= regions.size()) {
            if (regionOuvertes.isEmpty()) regions.add(new RegisterStatus(new RegisterStatus(null)));
            else regions.add(new RegisterStatus(regions.get(regionOuvertes.peek())));
        }
        regionOuvertes.push(lastRegion);
    }

    public void sortirRegion() {
        regionOuvertes.pop();
    }

    public RegisterStatus getCurrentRegister() {
        if (!regionOuvertes.isEmpty()) return regions.get(regionOuvertes.peek());
        else return null;
    }
    public void nouveauParcours() {
        lastRegion=-1;
        regionOuvertes.clear();
    }

    public int getFreeRegistre() {
        RegisterStatus r = getCurrentRegister();
        return r.getFreeRegisterId()+1;
    }
    public void useRegistre(int i) {
        getCurrentRegister().useId(i-1);
    }
    public void freeRegistre(int i) {
        getCurrentRegister().freeRegister(i-1);
    }
    public Instruction getSaveInstruction() {
        return getCurrentRegister().saveRegister();
    }
    public Instruction getRestoreInstruction() {
        return getCurrentRegister().restoreRegister();
    }
}
