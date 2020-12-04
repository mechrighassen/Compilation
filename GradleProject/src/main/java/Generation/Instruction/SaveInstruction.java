package Generation.Instruction;

import Generation.Instruction.Instruction;
import Generation.RegisterStatus;

import java.util.List;

public class SaveInstruction extends Instruction {

    private RegisterStatus status;
    private List<Integer> toSave;

    public SaveInstruction(RegisterStatus s) {
        status=s;
        toSave = s.toSave();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0 ; i < toSave.size() ; i++) {
            builder.append("STW R");
            builder.append(toSave.get(i)+1);
            builder.append(",(SP)");
            builder.append('\n');
            builder.append("ADQ -2,SP");
            builder.append('\n');
        }
        return builder.toString();
    }

    public boolean isEmpty() {
        return toSave.size()==0;
    }
}
