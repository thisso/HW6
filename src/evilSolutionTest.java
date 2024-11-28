import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class evilSolutionTest {



    @Test
    public void testInitializeGame() {
        // Use a mocked dictionary for testing
        evilHangman game = new evilHangman();
        List<String> mockDictionary = Arrays.asList("echo", "heal", "belt", "peel", "hazy");
        game.currentWordList = new ArrayList<>(mockDictionary); // Override dictionary

        // Initialize the game with a word length of 4
        game.initializeGame(4);

        // Validate that the game initialized correctly
        ArrayList<String> remainingWords = game.getRemainingWords();
        assertEquals("The number of words with length 4 should match", 5, remainingWords.size());
        assertTrue("Remaining words should contain expected words",
                remainingWords.containsAll(Arrays.asList("echo", "heal", "belt", "peel", "hazy")));
    }



    @Test
    public void testPartitionWordFamiliesIsolated() {
        evilHangman game = new evilHangman();
        List<String> dictionary = Arrays.asList("echo", "heal", "belt", "aced", "hazy");

        // Override the current word list
        game.currentWordList = new ArrayList<>(dictionary);

        // Mock the initial solution pattern as all dashes ("----")
        ArrayList<Character> mockSolution = new ArrayList<>(Arrays.asList('-', '-', '-', '-'));
        game.solution = new evilSolution(4) {
            @Override
            public ArrayList<Character> getPartialSolution() {
                return mockSolution; // Return mock pattern
            }
        };

        // Test partitioning based on the guessed letter 'e'
        Map<String, ArrayList<String>> wordFamilies = game.partitionWordFamilies('e');

        // Expected output patterns
        assertNotNull("Family for pattern 'e---' should not be null", wordFamilies.get("e---"));
        assertTrue("Family for pattern 'e---' should contain 'echo'", wordFamilies.get("e---").contains("echo"));

        assertNotNull("Family for pattern '-e--' should not be null", wordFamilies.get("-e--"));
        assertTrue("Family for pattern '-e--' should contain 'heal' and 'belt'",
                wordFamilies.get("-e--").containsAll(Arrays.asList("heal", "belt")));

        assertNotNull("Family for pattern '--e-' should not be null", wordFamilies.get("--e-"));
        assertTrue("Family for pattern '--e-' should contain 'peel'", wordFamilies.get("--e-").contains("aced"));

        assertNotNull("Family for pattern '----' should not be null", wordFamilies.get("----"));
        assertTrue("Family for pattern '----' should contain 'hazy'", wordFamilies.get("----").contains("hazy"));

        // Debugging: Print the word families for verification
        System.out.println("Word Families: " + wordFamilies);
    }






    @Test
    public void testChooseLargestFamily() {
        evilHangman game = new evilHangman();

        // Mock word families for testing
        Map<String, ArrayList<String>> wordFamilies = new HashMap<>();
        wordFamilies.put("e---", new ArrayList<>(Collections.singletonList("echo")));
        wordFamilies.put("-e--", new ArrayList<>(Arrays.asList("heal", "belt")));
        wordFamilies.put("-ee-", new ArrayList<>(Collections.singletonList("peel")));
        wordFamilies.put("----", new ArrayList<>(Collections.singletonList("hazy")));

        // Test choosing the largest family
        String largestFamily = game.chooseLargestFamily(wordFamilies);

        // Verify the largest family is correctly identified
        assertEquals("-e--", largestFamily);
    }

    @Test
    public void testUpdateGameState() {
        evilHangman game = new evilHangman();
        game.initializeGame(4);

        // Mock a new word list and pattern
        ArrayList<String> newWordList = new ArrayList<>(Arrays.asList("heal", "belt"));
        game.updateGameState('e', "-e--", newWordList);

        // Verify the state updates correctly
        assertEquals(2, game.getRemainingWords().size());
        assertTrue(game.getRemainingWords().contains("heal"));
        assertTrue(game.getRemainingWords().contains("belt"));
    }
}
