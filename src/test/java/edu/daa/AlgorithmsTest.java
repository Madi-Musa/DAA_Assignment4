package edu.daa;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {
    private final EditDistance[] edits = {
            new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance()};
    private final StringSearch[] searches = {new NaiveSearch(), new KmpSearch()};

    @Test
    void editReferencesAndEmptyStrings() {
        for (EditDistance edit : edits) {
            assertEquals(3, edit.distance("kitten", "sitting"));
            assertEquals(2, edit.distance("flaw", "lawn"));
            assertEquals(5, edit.distance("intention", "execution"));
            assertEquals(3, edit.distance("", "abc"));
            assertEquals(3, edit.distance("abc", ""));
            assertEquals(0, edit.distance("", ""));
            assertEquals(0, edit.distance("abc", "abc"));
            assertEquals(1, edit.distance("a", "b"));
        }
    }

    @Test
    void editAgreesOn100RandomPairs() {
        Random random = new Random(42);
        for (int i = 0; i < 100; i++) {
            String a = randomString(random, random.nextInt(7));
            String b = randomString(random, random.nextInt(7));
            int expected = edits[0].distance(a, b);
            for (EditDistance edit : edits) assertEquals(expected, edit.distance(a, b), a + "/" + b);
        }
    }

    @Test
    void lpsReferences() {
        KmpSearch kmp = new KmpSearch();
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, kmp.lps("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, kmp.lps("ABABCABAB"));
        assertArrayEquals(new int[]{0}, kmp.lps("A"));
        assertThrows(IllegalArgumentException.class, () -> kmp.lps(""));
    }

    @Test
    void searchReferencesAndEdges() {
        for (StringSearch search : searches) {
            assertEquals(List.of(10), search.search("ABABDABACDABABCABAB", "ABABCABAB"));
            assertEquals(List.of(0, 1, 2, 3), search.search("AAAAA", "AA"));
            assertEquals(List.of(0, 2, 4), search.search("ABABABA", "ABA"));
            assertEquals(List.of(), search.search("abc", "abcd"));
            assertEquals(List.of(), search.search("", "a"));
            assertEquals(List.of(), search.search("abc", "z"));
            assertEquals(List.of(0), search.search("abc", "abc"));
            assertThrows(IllegalArgumentException.class, () -> search.search("abc", ""));
            assertThrows(IllegalArgumentException.class, () -> search.search("", ""));
        }
    }

    @Test
    void searchAgreesOn100RandomTexts() {
        Random random = new Random(84);
        for (int i = 0; i < 100; i++) {
            String text = randomString(random, random.nextInt(101));
            String pattern = randomString(random, 1 + random.nextInt(10));
            assertEquals(searches[0].search(text, pattern), searches[1].search(text, pattern));
        }
    }

    private String randomString(Random random, int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) result.append("ABC".charAt(random.nextInt(3)));
        return result.toString();
    }
}
