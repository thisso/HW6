import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class evilHangman {

    private ArrayList<String> currentWordList;
    private HashSet<Character> previousGuesses;
    private TreeSet<Character> incorrectGuesses;
    private evilSolution solution;
    private Scanner inputScanner;

    public evilHangman() {
        this("engDictionary.txt");
    }

    public evilHangman(String filename) {
        try {
            currentWordList = dictionaryToList(filename);
        } catch (IOException e) {
            System.out.printf(
                    "Couldn't read from the file %s. Verify that you have it in the right place and try running again.",
                    filename);
            e.printStackTrace();
            System.exit(0);
        }

        previousGuesses = new HashSet<>();
        incorrectGuesses = new TreeSet<>();
        inputScanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to Evil Hangman!");

        int wordLength = promptForWordLength();
        initializeGame(wordLength);

        while (!solution.isSolved()) {
            displayGameState();
            char guess = promptForGuess();

            // Partition word families based on the guess
            Map<String, ArrayList<String>> wordFamilies = partitionWordFamilies(guess);

            // Choose the largest word family
            String chosenPattern = chooseLargestFamily(wordFamilies);

            // Assign the newWordList from the largest family
            ArrayList<String> newWordList = wordFamilies.get(chosenPattern);

            // Update the solution and guesses
            updateGameState(guess, chosenPattern, newWordList);

            // If no words remain (rare edge case), end the game
            if (currentWordList.isEmpty()) {
                System.out.println("No possible words remain. Ending game.");
                break;
            }
        }

        endGame();
    }

    private int promptForWordLength() {
        int wordLength = 0;
        while (wordLength <= 0) {
            System.out.print("Enter the word length you'd like to play with: ");
            if (inputScanner.hasNextInt()) {
                wordLength = inputScanner.nextInt();
                if (wordLength <= 0) {
                    System.out.println("Please enter a positive integer.");
                }
            } else {
                System.out.println("Invalid input. Please enter a positive integer.");
                inputScanner.next();
            }
        }
        return wordLength;
    }

    void initializeGame(int wordLength) {
        ArrayList<String> filteredWords = new ArrayList<>();
        for (String word : currentWordList) {
            if (word.length() == wordLength) {
                filteredWords.add(word);
            }
        }
        if (filteredWords.isEmpty()) {
            throw new IllegalArgumentException("No words of the specified length in the dictionary.");
        }
        currentWordList = filteredWords;
        solution = new evilSolution(wordLength);
    }

    private char promptForGuess() {
        while (true) {
            System.out.print("Guess a letter: ");
            String input = inputScanner.next().toLowerCase();

            if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                System.out.println("Invalid input. Please enter a single letter.");
            } else if (previousGuesses.contains(input.charAt(0))) {
                System.out.println("You've already guessed that letter.");
            } else {
                return input.charAt(0);
            }
        }
    }

    Map<String, ArrayList<String>> partitionWordFamilies(char guess) {
        Map<String, ArrayList<String>> wordFamilies = new HashMap<>();

        for (String word : currentWordList) {
            StringBuilder pattern = new StringBuilder();

            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    pattern.append(guess);
                } else {
                    pattern.append(solution.getPartialSolution().get(i));
                }
            }

            String patternStr = pattern.toString();
            wordFamilies.putIfAbsent(patternStr, new ArrayList<>());
            wordFamilies.get(patternStr).add(word);
        }

        return wordFamilies;
    }

    String chooseLargestFamily(Map<String, ArrayList<String>> wordFamilies) {
        String largestFamily = null;
        int maxSize = 0;

        for (Map.Entry<String, ArrayList<String>> entry : wordFamilies.entrySet()) {
            if (entry.getValue().size() > maxSize) {
                maxSize = entry.getValue().size();
                largestFamily = entry.getKey();
            }
        }

        return largestFamily;
    }

    public void updateGameState(char guess, String chosenPattern, ArrayList<String> newWordList) {
        currentWordList = newWordList;
        solution.updatePattern(chosenPattern);

        if (!chosenPattern.contains(String.valueOf(guess))) {
            incorrectGuesses.add(guess);
        }
        previousGuesses.add(guess);
    }

    private void displayGameState() {
        System.out.println("\nCurrent progress: " + solution.getPartialSolutionAsString());
        System.out.println("Incorrect guesses: " + incorrectGuesses);
        System.out.println("Previous guesses: " + previousGuesses);
    }

    private void endGame() {
        if (solution.isSolved()) {
            System.out.println("Congratulations! You guessed the word: " + currentWordList.get(0));
        } else {
            System.out.println("Game over! The word was: " + currentWordList.get(0));
        }
    }

    private static ArrayList<String> dictionaryToList(String filename) throws IOException {
        FileInputStream fs = new FileInputStream(filename);
        Scanner scnr = new Scanner(fs);

        ArrayList<String> wordList = new ArrayList<>();

        while (scnr.hasNext()) {
            wordList.add(scnr.next());
        }

        return wordList;
    }

    public ArrayList<String> getRemainingWords() {
        return currentWordList;
    }
}
