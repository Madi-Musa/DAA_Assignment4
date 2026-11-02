package edu.daa;

import java.util.ArrayList;
import java.util.List;

public class KmpSearch implements StringSearch {
    public int[] lps(String p) {
        if (p.isEmpty()) throw new IllegalArgumentException("Pattern must not be empty");
        int[] prefix = new int[p.length()];
        int i = 1, length = 0;
        while (i < p.length()) {
            if (p.charAt(i) == p.charAt(length)) prefix[i++] = ++length;
            else if (length > 0) length = prefix[length - 1];
            else prefix[i++] = 0;
        }
        return prefix;
    }

    public List<Integer> search(String text, String pattern) {
        if (pattern.isEmpty()) throw new IllegalArgumentException("Pattern must not be empty");
        List<Integer> matches = new ArrayList<>();
        if (pattern.length() > text.length()) return matches;
        int[] prefix = lps(pattern);
        int i = 0, j = 0;
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    matches.add(i - j);
                    j = prefix[j - 1];
                }
            } else if (j > 0) j = prefix[j - 1];
            else i++;
        }
        return matches;
    }
}
