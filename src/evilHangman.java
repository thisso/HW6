import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class evilHangman {

    // This list holds all the words that still fit the current game state.
    ArrayList<String> currentWordList;
    // Keeps track of every letter the player has guessed so far.
    private HashSet<Character> previousGuesses;
    // Stores letters that were guessed but turned out to be wrong.
    TreeSet<Character> incorrectGuesses;
    // Manages the current word solution, showing progress and updating as needed.
    evilSolution solution;
    // Used to read input from the player during the game.
    private Scanner inputScanner;

    // Constructor that uses a default dictionary file to start the game.
    public evilHangman() {
        this("engDictionary.txt");
    }

    // This lets you start the game with a custom dictionary file.
    public evilHangman(String filename) {
        try {
            // Load the words from the dictionary file into a list.
            currentWordList = dictionaryToList(filename);
        } catch (IOException e) {
            // If something goes wrong reading the file, we show an error and exit.
            System.out.printf(
                    "Couldn't read from the file %s. Verify that you have it in the right place and try running again.",
                    filename);
            e.printStackTrace();
            System.exit(0); // End the program if we can't load the dictionary.
        }

        previousGuesses = new HashSet<>();
        incorrectGuesses = new TreeSet<>();
        inputScanner = new Scanner(System.in);
    }

    // This is the main loop that starts and runs the game.
    public void start() {
        System.out.println("Welcome to Evil Hangman!");

        // Ask the player to pick how long the word should be.
        int wordLength = promptForWordLength();
        initializeGame(wordLength);

        // Keep playing until the solution is figured out or something goes wrong.
        while (!solution.isSolved()) {
            displayGameState(); // Show the current status of the game.
            char guess = promptForGuess(); // Ask the player for their next guess.

            // Break the word list into groups based on the player's guess.
            Map<String, ArrayList<String>> wordFamilies = partitionWordFamilies(guess);

            // Pick the group (or "family") with the most words in it.
            String chosenPattern = chooseLargestFamily(wordFamilies);

            // Update the game state with this new group of words.
            ArrayList<String> newWordList = wordFamilies.get(chosenPattern);
            updateGameState(guess, chosenPattern, newWordList);

            // This handles the very rare case where no words match anymore.
            if (currentWordList.isEmpty()) {
                System.out.println("No possible words remain based on your guesses. The game ends here.");
                System.out.println("To keep the game fun :) here's a random word from the original dictionary: 'example'.");
                break;
            }

        }

        endGame(); //  when the game ends.
    }

    // Ask the player how long they want the word to be
    private int promptForWordLength() {
        int wordLength = 0; // Start with an invalid value to enter the loop
        while (wordLength <= 1) { // Make sure the word length is greater than 1
            System.out.print("Enter the word length you'd like to play with (must be greater than 1): ");

            if (inputScanner.hasNextInt()) {
                wordLength = inputScanner.nextInt(); // Read the player's input as an integer

                if (wordLength <= 1) {
                    // Let the player know if the word length is too short
                    System.out.println("Word length must be greater than 1. Please try again.");
                }
            } else {
                // If the player enters something that's not a number, handle it gracefully
                System.out.println("Invalid input. Please enter a number greater than 1.");
                inputScanner.next(); // Clear the invalid input so we can prompt again
            }
        }
        return wordLength; // Once we have a valid input, return it
    }


    // Prepares the game by filtering the dictionary for words of the right length.
    void initializeGame(int wordLength) {
        ArrayList<String> filteredWords = new ArrayList<>();
        for (String word : currentWordList) {
            if (word.length() == wordLength) {
                filteredWords.add(word);
            }
        }
        if (filteredWords.isEmpty()) {
            // If there are no words of that length, we can't really play the game.
            throw new IllegalArgumentException("No words of the specified length in the dictionary.");
        }
        currentWordList = filteredWords;
        solution = new evilSolution(wordLength); // Start tracking progress for the word.
    }

    // This is where we ask the player to guess a letter.
    private char promptForGuess() {
        while (true) {
            System.out.print("Guess a letter: ");
            String input = inputScanner.next().toLowerCase();

            // Make sure the input is a single letter.
            if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
                System.out.println("Invalid input. Please enter a single letter.");
            } else if (previousGuesses.contains(input.charAt(0))) {
                // Let the player know if they try to guess a letter twice.
                System.out.println("You've already guessed that letter.");
            } else {
                return input.charAt(0); // It's a valid guess, so return it
            }
        }
    }

    // Divide the current words into groups based on where the guessed letter appears.
    Map<String, ArrayList<String>> partitionWordFamilies(char guess) {
        Map<String, ArrayList<String>> wordFamilies = new HashMap<>();

        for (String word : currentWordList) {
            StringBuilder pattern = new StringBuilder();

            // Build a pattern for the current word, using underscores for unknown letters.
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    pattern.append(guess);
                } else {
                    pattern.append(solution.getPartialSolution().get(i)); // Keep existing letters/underscores.
                }
            }

            String patternStr = pattern.toString();
            wordFamilies.putIfAbsent(patternStr, new ArrayList<>());
            wordFamilies.get(patternStr).add(word);

            // Debugging: Print the pattern and word for development purposes.
            //System.out.println("Generated Pattern: " + patternStr + " | Word: " + word);
        }

        // Debugging: Print all the different groups we made.
        //System.out.println("Word Families: " + wordFamilies);

        return wordFamilies;
    }

    // Choose the largest word group (or family) from all the options.
    String chooseLargestFamily(Map<String, ArrayList<String>> wordFamilies) {
        String largestFamily = null;
        int maxSize = 0;

        for (Map.Entry<String, ArrayList<String>> entry : wordFamilies.entrySet()) {
            if (entry.getValue().size() > maxSize) {
                maxSize = entry.getValue().size();
                largestFamily = entry.getKey();
            }
        }

        return largestFamily; // This gives us the pattern with the most matching words.
    }

    // Update the game state with the new pattern and word list.
    public void updateGameState(char guess, String chosenPattern, ArrayList<String> newWordList) {
        currentWordList = newWordList;
        solution.updatePattern(chosenPattern); // Update the progress for the solution.

        // If the guessed letter isn't in the pattern, it was a wrong guess.
        if (!chosenPattern.contains(String.valueOf(guess))) {
            incorrectGuesses.add(guess);
        }
        previousGuesses.add(guess); // Record that this guess was made.
    }

    // Show the player how they're doing in the game.
    private void displayGameState() {
        System.out.println("\nCurrent progress: " + solution.getPartialSolutionAsString());
        System.out.println("Incorrect guesses: " + incorrectGuesses);
        System.out.println("Previous guesses: " + previousGuesses);
    }

    // Wrap up the game, either as a win or a loss.
    private void endGame() {
        if (solution.isSolved()) {
            System.out.println("Congratulations! You guessed the word: " + currentWordList.get(0));
        } else {
            System.out.println("Game over! The word was: " + currentWordList.get(0));
        }
    }

    // Read the dictionary file and turn it into a list of words.
    private static ArrayList<String> dictionaryToList(String filename) throws IOException {
        FileInputStream fs = new FileInputStream(filename);
        Scanner scnr = new Scanner(fs);

        ArrayList<String> wordList = new ArrayList<>();

        while (scnr.hasNext()) {
            wordList.add(scnr.next());
        }

        return wordList; // Returns the list of all words from the file.
    }

    // Get the list of words that still fit the current game state (for debugging/testing).
    public ArrayList<String> getRemainingWords() {
        return currentWordList;
    }
}
