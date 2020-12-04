package Generation;

import Generation.Instruction.Instruction;
import Generation.Instruction.RestoreInstruction;
import Generation.Instruction.SaveInstruction;

import java.util.ArrayList;
import java.util.List;

public class RegisterStatus {

    private boolean registres[];
    private boolean registresOnceUsed[];
    private RegisterStatus previous;
    public int maxRegistre = RegisterController.maxRegistre;

    public RegisterStatus(RegisterStatus previous) {
        this.previous=previous;
        registres = new boolean[maxRegistre];
        registresOnceUsed = new boolean[maxRegistre];
        for (int i = 0 ; i < maxRegistre ; i++) registres[i]=false;
        for (int i = 0 ; i < maxRegistre ; i++) registresOnceUsed[i] = false;
    }

    public boolean[] getRegister() { return registres; }

    public boolean[] getRegistresOnceUsed() { return registresOnceUsed; }
    public void useRegister(int i) {
        registres[i]=true;registresOnceUsed[i] = true;
    }

    public void freeRegister(int i ) {
        registres[i]=false;
    }

    public Instruction saveRegister() {
        if (previous != null) {
            return new SaveInstruction(this);
        }
        else return null;
    }

    public Instruction restoreRegister() {
        if (previous != null) {
            return new RestoreInstruction(this);
        }
        else return null;
    }

    public RegisterStatus getParent() {
        return previous;
    }

    public List<Integer> intersection(boolean r1[], boolean r2[]) {
        ArrayList<Integer> array = new ArrayList<>();
        for (int i = 0 ; i < r1.length ; i++) {
            if (r1[i] && r2[i]) {
                array.add(i);
            }
        }
        return array;
    }

    protected int getFreeRegisterId() {
        int i = 0;
        int id = -1;
        while (i < maxRegistre && (id == -1 || previous.getRegister()[id])) {
            if (!getRegister()[i]) {
                id = i;
            }
            i++;
        }
        return id;
    }

    protected void useId(int i) {
        if (!registres[i]) registres[i] = true;
    }

    public List<Integer> toSave() {
        List<Integer> tosave = new ArrayList<>();
        for (int i = 0 ; i < maxRegistre ; i++) {
            if (registres[i]) {
                tosave.add(i);
            }
        }
        return tosave;
    }
}
