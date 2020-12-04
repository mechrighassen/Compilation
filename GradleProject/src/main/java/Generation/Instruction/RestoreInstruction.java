package Generation.Instruction;

import Generation.Instruction.Instruction;
import Generation.RegisterStatus;

import java.util.List;

public class RestoreInstruction extends Instruction {

    private RegisterStatus status;
    private List<Integer> toSave;

    public RestoreInstruction(RegisterStatus s) {
        status=s;
        toSave = s.toSave();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = toSave.size()-1 ; i >=0 ; i--) {
            builder.append("ADQ 2,SP");
            builder.append('\n');
            builder.append("LDW R");
            builder.append(toSave.get(i)+1);
            builder.append(",(SP)");
            builder.append('\n');
        }
        return builder.toString();
    }
    public boolean isEmpty() {
        return toSave.size()==0;
    }
}
