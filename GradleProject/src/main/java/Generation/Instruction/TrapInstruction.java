package Generation.Instruction;

public class TrapInstruction extends Instruction {
    private int trap;
    private String registre;

    public TrapInstruction(int trap, String registre) {
        this.trap=trap;
        this.registre=registre;
    }

    public TrapInstruction(int trap) {
        this(trap, "UT");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("LDQ ").append(trap).append(",").append(registre).append('\n');
        sb.append("TRP ").append(registre).append('\n');

        return sb.toString();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
