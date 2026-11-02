package edu.daa;

import java.util.Random;

public class InputGenerator {
    public static final long SEED = 20261102L;

    public record Input(String first, String second, long seed) {}

    public static Input generate(String family, String type, int n) {
        long seed = SEED + 31L * family.hashCode() + 17L * type.hashCode() + n;
        int m = family.equals("edit") ? n : n / 16;
        Random random = new Random(seed);
        if (type.equals("random")) return new Input(randomString(random, n), randomString(random, m), seed);
        if (family.equals("edit")) return new Input("A".repeat(n), "B".repeat(m), seed);
        return new Input("A".repeat(n), "A".repeat(m - 1) + "B", seed);
    }

    private static String randomString(Random random, int n) {
        StringBuilder result = new StringBuilder(n);
        for (int i = 0; i < n; i++) result.append("ABCD".charAt(random.nextInt(4)));
        return result.toString();
    }
}
