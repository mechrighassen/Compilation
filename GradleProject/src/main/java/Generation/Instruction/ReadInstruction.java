package Generation.Instruction;

public class ReadInstruction extends Instruction {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LDQ -10, UT\n");
        sb.append("ADD UT, SP, R1\n");
        sb.append("LDW R0, R1\n");
        sb.append("LDQ 65, UT\n");
        sb.append("TRP UT\n");

        sb.append("LDQ -48, R2\n");
        sb.append("LDQ 10, R3\n");
        sb.append("LDW R0, #0\n");
        sb.append("READBEGINLOOP    LDB UT, (R1)\n");
        sb.append("JEQ #READENDLOOP-$-2\n");
        sb.append("MUL R0, R3, R0\n");
        sb.append("ADD R2, UT, UT\n");
        sb.append("ADD R0, UT, R0\n");
        sb.append("ADQ 1, R1\n");
        sb.append("JMP #READBEGINLOOP -$-2\n");
        sb.append("READENDLOOP LDQ 0, R1\n");
        return sb.toString();

    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
