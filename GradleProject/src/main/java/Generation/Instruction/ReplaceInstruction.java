package Generation.Instruction;

public class ReplaceInstruction extends Instruction {
    private String mainContent;
    private String replaceWith;


    public ReplaceInstruction(String main) {
        mainContent = main;
    }

    public String getMainContent() {
        return mainContent;
    }

    public void setMainContent(String mainContent) {
        this.mainContent = mainContent;
    }

    public String getReplaceWith() {
        return replaceWith;
    }

    public void setReplaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
    }

    @Override
    public String toString() {
        return mainContent.replaceFirst("---",replaceWith);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
