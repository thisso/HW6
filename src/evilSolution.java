import java.util.ArrayList;

public class evilSolution {

    private static ArrayList<Character> partialSolution;
    private int missingChars;

    public evilSolution(int wordLength) {
        missingChars = wordLength;
        partialSolution = new ArrayList<>(missingChars);
        for (int i = 0; i < wordLength; i++) {
            partialSolution.add('_');
        }
    }

    public boolean isSolved() {
        return missingChars == 0;
    }

    public static void printProgress() {
        for (char c : partialSolution) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    public void updatePattern(String pattern) {
        int newMissingChars = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char newChar = pattern.charAt(i);
            if (partialSolution.get(i) == '_' && newChar != '_') {
                partialSolution.set(i, newChar);
            }
            if (partialSolution.get(i) == '_') {
                newMissingChars++;
            }
        }
        missingChars = newMissingChars;
    }

    public ArrayList<Character> getPartialSolution() {
        return partialSolution;
    }
}
