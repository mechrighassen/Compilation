package Generation.Instruction;

public class IdentifierAccess extends Instruction {
    private int n = 0;
    private int offset = 0;


    public IdentifierAccess(int n, int offset) {
        this.n=n;
        this.offset=offset;
    }
    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();

        if (n == 0 && offset < 0) {
            sBuilder.append("LDW UT, BP\n" +
                    "ADQ "+(-offset)+", UT\n" +
                    "LDW R0, (UT)");
        }
        return sBuilder.toString();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
