package Generation;

import Generation.Instruction.Instruction;
import Semantique.TDS;

import java.util.ArrayList;
import java.util.HashMap;
import Generation.Instruction.*;

public class ProgrammeMaker {

    private HashMap<Integer, String> etiquettes;
    private ArrayList<Instruction> instructions;
    private ArrayList<String> headers;


    public ProgrammeMaker() {
        etiquettes = new HashMap<>();
        instructions = new ArrayList<>();
        instructions.add(null);
        headers = new ArrayList<>();
        //blablabla
    }

    public int getNextIndex() {
        return instructions.size();
    }

    public Instruction getEnTete() {
        return new Instruction() {
            @Override
            public String toString() {

                StringBuilder sb = new StringBuilder();
                sb.append("org 0x2222\n" +
                        "start main_\n" +
                        "stackbase 0x1000\n");
                sb.append("SP equ R15\n");
                sb.append("BP equ R14\n");
                sb.append("UT equ R13\n");
                sb.append("TA equ R12\n");
                sb.append("STTA rsb 4000\n");
                for (String s : headers) {
                    sb.append(s);
                    sb.append('\n');
                }

                return sb.toString();
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }

    public Instruction getBas() {
        return new Instruction() {
            @Override
            public String toString() {
                return "";
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };
    }

    public void addHeader(String s) {
        headers.add(s);
    }

    public void addHeaders() {
        instructions.set(0, getEnTete());
        instructions.add(getBas());
    }

    public String getASM() {
        addHeaders();
        checkForEmptyInstruction();
        StringBuilder builder = new StringBuilder();


        for (int i = 0 ; i < instructions.size() ; i++) {
            String app = etiquettes.get(i);
            if (app != null) {
                builder.append(app);
                builder.append('\t'); //to check
            }
            if (!instructions.get(i).isEmpty()) builder.append(instructions.get(i).toString());
            builder.append('\n');
        }
        //builder.append('\n');
        return builder.toString();
    }

    public void checkForEmptyInstruction() {
        ArrayList<Integer> toMove = new ArrayList<>();

        for (Integer i : etiquettes.keySet()) {
            if (instructions.get(i).isEmpty()) {
                toMove.add(i);
            }
        }

        for (Integer i : toMove) {
            String s = etiquettes.get(i);
            etiquettes.remove(i);
            int j = findNonEmpty(i);
            if (etiquettes.containsKey(j) || j >= instructions.size()) {
                System.out.println("Big problem ! Duplicates etiquettes !");
            }
            etiquettes.put(j, s);
        }
    }

    private int findNonEmpty(int deb) {
        int i = deb;
        while (i < instructions.size() && !instructions.get(i).isEmpty()) {
            i++;
        }
        return i;
    }

    public void addEtiquette(String id) {
        addEtiquette(id, this.getNextIndex());
    }

    public void addEtiquette(String id, int ninstruction) {
        etiquettes.put(ninstruction, id);
    }

    public void addInstruction(Instruction ins) {
        instructions.add(ins);
    }

    public void addSimpleInstruction(String simpleInstruction){
        this.addInstruction(new GenericInstruction(simpleInstruction));
    }
}
