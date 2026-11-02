package edu.daa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CountersTest {
    @Test
    void editCountsAndResets() {
        RecursiveEditDistance recursive = new RecursiveEditDistance();
        recursive.distance("A", "B");
        assertEquals(4, recursive.operations());
        recursive.distance("", "B");
        assertEquals(1, recursive.operations());
        for (EditDistance dp : new EditDistance[]{new TableEditDistance(), new TwoRowEditDistance()}) {
            dp.distance("AB", "XYZ");
            assertEquals(6, dp.operations());
            dp.distance("", "ABC");
            assertEquals(0, dp.operations());
        }
    }

    @Test
    void searchCountsPreprocessingAndResets() {
        NaiveSearch naive = new NaiveSearch();
        naive.search("AAAA", "AB");
        assertEquals(6, naive.operations());
        KmpSearch kmp = new KmpSearch();
        kmp.search("AAAA", "AB");
        assertEquals(1, kmp.lpsComparisons());
        assertEquals(7, kmp.searchComparisons());
        assertEquals(8, kmp.operations());
        for (StringSearch search : new StringSearch[]{naive, kmp}) {
            search.search("A", "AB");
            assertEquals(0, search.operations());
        }
    }

    @Test
    void generatorIsRepeatable() {
        for (String family : new String[]{"edit", "search"}) {
            for (String type : new String[]{"random", "mismatch"}) {
                assertEquals(InputGenerator.generate(family, type, 64), InputGenerator.generate(family, type, 64));
            }
        }
        assertNotEquals(InputGenerator.generate("edit", "random", 64).first(),
                InputGenerator.generate("edit", "random", 128).first());
    }
}
