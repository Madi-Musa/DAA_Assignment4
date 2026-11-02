package edu.daa;

public class TwoRowEditDistance implements EditDistance {
    private long cells;

    public long operations() { return cells; }

    // State: previous[j]=D(i-1,j), current[j]=D(i,j) for prefix distances.
    // Transition: min(previous[j]+1, current[j-1]+1, previous[j-1]+replacement cost).
    // Base case: previous[j]=j initially; current[0]=i for each row.
    public int distance(String a, String b) {
        cells = 0;
        int m = b.length();
        int[] previous = new int[m + 1], current = new int[m + 1];
        for (int j = 0; j <= m; j++) previous[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= m; j++) {
                cells++;
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(previous[j - 1] + cost,
                        Math.min(previous[j] + 1, current[j - 1] + 1));
            }
            int[] swap = previous;
            previous = current;
            current = swap;
        }
        return previous[m];
    }
}
