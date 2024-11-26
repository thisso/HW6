import java.util.ArrayList;

public class evilSolution {

    private ArrayList<Character> partialSolution;
    private int missingChars;

    public evilSolution(int wordLength) {
        this.missingChars = wordLength;
        this.partialSolution = new ArrayList<>();
        for (int i = 0; i < wordLength; i++) {
            partialSolution.add('_'); // Initialize the solution with underscores
        }
    }

    // Checks if the word is fully solved
    public boolean isSolved() {
        return missingChars == 0;
    }

    // Updates the partial solution based on the new pattern
    public void updatePattern(String pattern) {
        int newMissingChars = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char newChar = pattern.charAt(i);
            if (partialSolution.get(i) == '_' && newChar != '_') {
                partialSolution.set(i, newChar); // Update revealed characters
            }
            if (partialSolution.get(i) == '_') {
                newMissingChars++; // Count remaining blanks
            }
        }
        this.missingChars = newMissingChars;
    }

    // Returns the current partial solution as a list of characters
    public ArrayList<Character> getPartialSolution() {
        return partialSolution;
    }

    // **Added**: Returns the partial solution as a formatted string
    public String getPartialSolutionAsString() {
        StringBuilder sb = new StringBuilder();
        for (char c : partialSolution) {
            sb.append(c).append(" "); // Add spaces between characters
        }
        return sb.toString().trim(); // Remove trailing space
    }
}
