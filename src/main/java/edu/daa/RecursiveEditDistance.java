package edu.daa;

public class RecursiveEditDistance implements EditDistance {
    private long calls;

    public long operations() { return calls; }

    // State: D(i,j) is the distance between prefixes of lengths i and j.
    // Transition: equal last characters use D(i-1,j-1); otherwise 1 + min(delete, insert, replace).
    // Base case: D(0,j)=j and D(i,0)=i.
    public int distance(String a, String b) {
        calls = 0;
        return solve(a, b, a.length(), b.length());
    }

    private int solve(String a, String b, int i, int j) {
        calls++;
        if (i == 0) return j;
        if (j == 0) return i;
        if (a.charAt(i - 1) == b.charAt(j - 1)) return solve(a, b, i - 1, j - 1);
        return 1 + Math.min(solve(a, b, i - 1, j - 1),
                Math.min(solve(a, b, i - 1, j), solve(a, b, i, j - 1)));
    }
}
