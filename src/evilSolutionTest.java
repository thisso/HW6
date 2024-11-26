import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class evilSolutionTest {

    @Test
    public void testInitializeGame() {
        evilHangman game = new evilHangman();
        List<String> dictionary = Arrays.asList("echo", "heal", "belt", "peel", "hazy");

        // Mock the word list for testing purposes
        game.initializeGame(4);

        // Verify that the game initializes correctly with words of the specified length
        assertEquals(5, game.getRemainingWords().size());
    }

    @Test
    public void testPartitionWordFamilies() {
        evilHangman game = new evilHangman();
        List<String> dictionary = Arrays.asList("echo", "heal", "belt", "peel", "hazy");

        // Mock the word list for testing
        game.initializeGame(4);

        // Test partitioning based on the guessed letter 'e'
        Map<String, ArrayList<String>> wordFamilies = game.partitionWordFamilies('e');

        // Verify word families are correctly grouped
        assertTrue(wordFamilies.get("e---").contains("echo"));
        assertTrue(wordFamilies.get("-e--").containsAll(Arrays.asList("heal", "belt")));
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
