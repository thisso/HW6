import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class evilHangman {

    private ArrayList<String> currentWordList; // Tracks the current set of valid words
    private HashSet<Character> previousGuesses;
    private TreeSet<Character> incorrectGuesses; // Tracks incorrect guesses
    private evilSolution Solution;
    private Scanner inputScanner;

    public evilHangman() {
        this("engDictionary.txt");
    }

    public evilHangman(String filename) {
        try {
            currentWordList = dictionaryToList(filename); // Initialize with the full dictionary
        } catch (IOException e) {
            System.out.printf(
                    "Couldn't read from the file %s. Verify that you have it in the right place and try running again.",
                    filename);
            e.printStackTrace();
            System.exit(0); // Stop the program--no dictionary, no game
        }

        previousGuesses = new HashSet<>();
        incorrectGuesses = new TreeSet<>();

        // Choose a random word length to begin the game
        int randomIndex = new Random().nextInt(currentWordList.size());
        int targetLength = currentWordList.get(randomIndex).length();

        // Filter words to only include those of the chosen length
        currentWordList.removeIf(word -> word.length() != targetLength);

        Solution = new evilSolution(targetLength);
        inputScanner = new Scanner(System.in);
    }

    public void start() {
        while (!Solution.isSolved()) {
            char guess = promptForGuess();
            recordGuess(guess);
        }
        printVictory();
    }

    private char promptForGuess() {
        while (true) {
            System.out.println("Guess a letter.\n");
            evilSolution.printProgress();
            System.out.println("Incorrect guesses:\n" + incorrectGuesses.toString());
            String input = inputScanner.next();
            if (input.length() != 1) {
                System.out.println("Please enter a single character.");
            } else if (previousGuesses.contains(input.charAt(0))) {
                System.out.println("You've already guessed that.");
            } else {
                return input.charAt(0);
            }
        }
    }

    private void recordGuess(char guess) {
        previousGuesses.add(guess);

        // Partition the current word list into word families
        Map<String, ArrayList<String>> wordFamilies = partitionWordFamilies(guess);

        // Choose the largest word family to maximize remaining options
        String chosenPattern = chooseLargestFamily(wordFamilies);

        // Update the solution's pattern and word list
        Solution.updatePattern(chosenPattern);
        currentWordList = wordFamilies.get(chosenPattern);

        // Determine if the guess was correct
        if (!chosenPattern.contains(String.valueOf(guess))) {
            incorrectGuesses.add(guess);
        }
    }

    private Map<String, ArrayList<String>> partitionWordFamilies(char guess) {
        Map<String, ArrayList<String>> wordFamilies = new HashMap<>();

        for (String word : currentWordList) {
            StringBuilder pattern = new StringBuilder();

            // Create a pattern based on the guessed letter's position in the word
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    pattern.append(guess);
                } else {
                    pattern.append(Solution.getPartialSolution().get(i));
                }
            }

            String patternStr = pattern.toString();

            // Group words into families based on their patterns
            wordFamilies.putIfAbsent(patternStr, new ArrayList<>());
            wordFamilies.get(patternStr).add(word);
        }

        return wordFamilies;
    }

    private String chooseLargestFamily(Map<String, ArrayList<String>> wordFamilies) {
        String largestFamilyPattern = null;
        int maxSize = 0;

        for (Map.Entry<String, ArrayList<String>> entry : wordFamilies.entrySet()) {
            if (entry.getValue().size() > maxSize) {
                maxSize = entry.getValue().size();
                largestFamilyPattern = entry.getKey();
            }
        }

        return largestFamilyPattern;
    }

    private void printVictory() {
        System.out.printf("Congrats! The word was %s%n", currentWordList.get(0)); // Reveal one possible word
    }

    private static ArrayList<String> dictionaryToList(String filename) throws IOException {
        FileInputStream fs = new FileInputStream(filename);
        Scanner scnr = new Scanner(fs);

        ArrayList<String> wordList = new ArrayList<>();

        while (scnr.hasNext()) {
            wordList.add(scnr.next());
        }

        scnr.close();
        return wordList;
    }
}
