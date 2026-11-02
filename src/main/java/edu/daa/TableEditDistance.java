package edu.daa;

public class TableEditDistance implements EditDistance {
    private long cells;

    public long operations() { return cells; }

    // State: d[i][j] is the distance between prefixes of lengths i and j.
    // Transition: min(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1]+replacement cost).
    // Base case: d[0][j]=j and d[i][0]=i.
    public int distance(String a, String b) {
        cells = 0;
        int n = a.length(), m = b.length();
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) d[i][0] = i;
        for (int j = 0; j <= m; j++) d[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                cells++;
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                d[i][j] = Math.min(d[i - 1][j - 1] + cost,
                        Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1));
            }
        }
        return d[n][m];
    }
}
