package Generation.Instruction;


public class GenericInstruction extends Instruction {
    private String content;

    public GenericInstruction() {

    }

    public GenericInstruction(String c) {
        content=c;
    }

    @Override
    public String toString() {
        return content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.equals("");
    }
}
