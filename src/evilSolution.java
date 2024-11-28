import java.util.ArrayList;

public class evilSolution {

    // Keeps track of the current state of the word with underscores for missing letters
    private ArrayList<Character> partialSolution;
    // Tracks how many letters are still missing in the word
    private int missingChars;

    // This sets up the solution for a word of the given length
    public evilSolution(int wordLength) {
        this.missingChars = wordLength; // Start with all letters missing
        this.partialSolution = new ArrayList<>();
        for (int i = 0; i < wordLength; i++) {
            partialSolution.add('_'); // Fill the solution with underscores
        }
    }

    // Checks if the word has been fully guessed
    public boolean isSolved() {
        return missingChars == 0; // If no blanks are left the word is solved
    }

    // Updates the solution based on the new pattern after a guess
    public void updatePattern(String pattern) {
        int newMissingChars = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char newChar = pattern.charAt(i);
            if (partialSolution.get(i) == '_' && newChar != '_') {
                partialSolution.set(i, newChar); // Fill in any newly revealed letters
            }
            if (partialSolution.get(i) == '_') {
                newMissingChars++; // Count how many blanks are still left
            }
        }
        this.missingChars = newMissingChars; // Update the count of missing letters
    }

    // Gives back the current solution as a list of characters
    public ArrayList<Character> getPartialSolution() {
        return partialSolution;
    }

    // Returns the current solution as a string with spaces between letters
    public String getPartialSolutionAsString() {
        StringBuilder sb = new StringBuilder();
        for (char c : partialSolution) {
            sb.append(c).append(" "); // Add spaces between each character
        }
        return sb.toString().trim(); // Get rid of the extra space at the end
    }
}
