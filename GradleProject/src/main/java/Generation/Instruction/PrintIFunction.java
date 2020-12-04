package Generation.Instruction;

public class PrintIFunction extends Instruction {
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("LDW R0, (BP)4\n");
        sb.append("LDW R1, #10\n");
        sb.append("ADQ -20, SP\n");
        sb.append("LDW R10, SP\n");
        sb.append("ADQ -4, SP\n");
        sb.append("LDW R9, SP\n");

        sb.append("ldq 32, r3      // code ASCII de espace (SPace) -> r3\n" +
                "        ldq 10, UT            // 10 -> wr\n" +
                "        cmp r1, UT            // charge les indicateurs de b - 10\n" +
                "        bne NOSIGN-$-2        // si non égal (donc si b != 10) saute en NOSIGN, sinon calcule signe\n" +
                "        ldq 43, r3    // charge le code ASCII du signe plus + dans r3\n" +
                "        tst r0                // charge les indicateurs de r0 et donc de i\n" +
                "        bge POSIT-$-2         // saute en POSIT si i >= 0\n" +
                "        neg r0, r0            // change le signe de r0\n" +
                "        ldq 45, r3   // charge le code ASCII du signe moins - dans r3\n" +
                "POSIT   NOP                   // r3 = code ASCII de signe: SP pour aucun, - ou +\n");

        sb.append("// convertit l'entier i en chiffres et les empile de droite à gauche\n" +
                "NOSIGN  ldw r2, r0            // r2 <- r0\n" +
                "CNVLOOP ldw r0, r2            // r0 <- r2\n");

        sb.append("        srl r1, r1            // r1 = b/2\n" +
                "        ani r0, r4, #1        // ANd Immédiate entre r0 et 00...01 vers r4:\n" +
                "                              // bit n°0 de r0 -> r4; r4 = reste\" de r0/2\n" +
                "        srl r0, r0            // r0 / 2 -> r0\n" +
                "        div r0, r1, r2        // quotient = r0 / r1 -> r2, reste' = r0 % r1 -> r0\n" +
                "        shl r0, r0            // r0 = 2 * reste'\n" +
                "        add r0, r4, r0        // r0 = reste = 2 * reste' + reste\" => r0 = chiffre\n" +
                "        shl r1, r1            // r1 = b\n" +
                "\n" +
                "        adq -10, r0           // chiffre - 10 -> r0 \n" +
                "        bge LETTER-$-2        // saute en LETTER si chiffre >= 10\n" +
                "        adq 10+48, r0    // ajoute 10 => r0 = chiffre, ajoute code ASCII de 0 \n" +
                "                              // => r0 = code ASCII de chiffre\n" +
                "        bmp STKCHR-$-2        // saute en STKCHR \n" +
                "\n" +
                "LETTER  adq 65, r0       // r0 = ASCII(A) pour chiffre = 10, ASCII(B) pour 11 ...\n" +
                "                              // ajoute code ASCII de A => r = code ASCII de chiffre\n" +
                "STKCHR  stw r0, -(SP)         // empile code ASCII du chiffre \n" +
                "                              // (sur un mot complet pour pas désaligner pile)\n" +
                "        tst r2                // charge les indicateurs en fonction du quotient ds r2)\n" +
                "        bne CNVLOOP-$-2       // boucle si quotient non nul; sinon sort\n");

        sb.append("// recopie les caractères dans le tampon dans le bon ordre: de gauche à droite\n" +
                "        ldw r1, R10    // r1 pointe sur le début du tampon déjà alloué \n" +
                "        stb r3, (r1)+         // copie le signe dans le tampon\n" +
                "CPYLOOP ldw r0, (SP)+         // dépile code du chiffre gauche (sur un mot) dans r0\n" +
                "        stb r0, (r1)+         // copie code du chiffre dans un Byte du tampon de gauche à droite\n" +
                "        cmp SP, R9           // compare sp et sa valeur avant empilement des caractères qui était bp\n" +
                "        bne CPYLOOP-$-2       // boucle s'il reste au moins un chiffre sur la pile\n" +
                "        ldq 0, r0           // charge le code du caractère NUL dans r0\n" +
                "        stb r0, (r1)+         // sauve code NUL pour terminer la chaîne de caractères\n");

        sb.append("LDW R0, #-4\n");
        sb.append("ADD BP, R0, SP\n");
        sb.append("LDW R0, R10\n");
        sb.append("LDQ 66, UT\n");
        sb.append("TRP UT\n");
        return sb.toString();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
