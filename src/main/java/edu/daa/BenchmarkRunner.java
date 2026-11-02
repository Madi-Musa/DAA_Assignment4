package edu.daa;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

public class BenchmarkRunner {
    private static final int RUNS = 5;
    private static final int WARMUPS = 3;

    public static void main(String[] args) throws IOException {
        Path output = Path.of(args.length == 0 ? "results/results.csv" : args[0]);
        if (output.toAbsolutePath().getParent() != null) Files.createDirectories(output.toAbsolutePath().getParent());
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            csv.println("algorithm,input,n,m,run,time_ms,operations,lps_comparisons,search_comparisons,answer,seed,input_sha256");
            for (String type : List.of("random", "mismatch")) {
                for (int n : new int[]{2, 4, 6, 8, 128, 256, 512, 1024}) {
                    InputGenerator.Input input = InputGenerator.generate("edit", type, n);
                    EditDistance[] algorithms = n <= 8
                            ? new EditDistance[]{new RecursiveEditDistance(), new TableEditDistance(), new TwoRowEditDistance()}
                            : new EditDistance[]{new TableEditDistance(), new TwoRowEditDistance()};
                    int expected = new TwoRowEditDistance().distance(input.first(), input.second());
                    for (EditDistance algorithm : algorithms) measureEdit(csv, type, input, algorithm, expected);
                }
            }
            for (String type : List.of("random", "late_mismatch")) {
                for (int n : new int[]{2000, 4000, 8000, 16000}) {
                    InputGenerator.Input input = InputGenerator.generate("search", type, n);
                    List<Integer> expected = new NaiveSearch().search(input.first(), input.second());
                    for (StringSearch algorithm : new StringSearch[]{new NaiveSearch(), new KmpSearch()}) {
                        measureSearch(csv, type, input, algorithm, expected);
                    }
                }
            }
            if (csv.checkError()) throw new IOException("Could not write benchmark CSV");
        }
    }

    private static void measureEdit(PrintWriter csv, String type, InputGenerator.Input input,
                                    EditDistance algorithm, int expected) {
        for (int i = 0; i < WARMUPS; i++) algorithm.distance(input.first(), input.second());
        double[] times = new double[RUNS];
        long previous = -1;
        for (int run = 1; run <= RUNS; run++) {
            long start = System.nanoTime();
            int answer = algorithm.distance(input.first(), input.second());
            double ms = (System.nanoTime() - start) / 1_000_000.0;
            if (answer != expected || (previous >= 0 && previous != algorithm.operations())) {
                throw new IllegalStateException("Edit result or counter changed");
            }
            previous = algorithm.operations();
            times[run - 1] = ms;
            write(csv, algorithm.getClass().getSimpleName(), type, input, run, ms, previous, 0, 0, answer);
        }
        printMedian(algorithm.getClass().getSimpleName(), type, input.first().length(), times);
    }

    private static void measureSearch(PrintWriter csv, String type, InputGenerator.Input input,
                                      StringSearch algorithm, List<Integer> expected) {
        for (int i = 0; i < WARMUPS; i++) algorithm.search(input.first(), input.second());
        double[] times = new double[RUNS];
        long previous = -1;
        for (int run = 1; run <= RUNS; run++) {
            long start = System.nanoTime();
            List<Integer> answer = algorithm.search(input.first(), input.second());
            double ms = (System.nanoTime() - start) / 1_000_000.0;
            if (!answer.equals(expected) || (previous >= 0 && previous != algorithm.operations())) {
                throw new IllegalStateException("Search result or counter changed");
            }
            previous = algorithm.operations();
            times[run - 1] = ms;
            long lps = algorithm instanceof KmpSearch kmp ? kmp.lpsComparisons() : 0;
            long search = algorithm instanceof KmpSearch kmp ? kmp.searchComparisons() : previous;
            write(csv, algorithm.getClass().getSimpleName(), type, input, run, ms, previous, lps, search, answer.size());
        }
        printMedian(algorithm.getClass().getSimpleName(), type, input.first().length(), times);
    }

    private static void write(PrintWriter csv, String algorithm, String type, InputGenerator.Input input,
                              int run, double ms, long operations, long lps, long search, int answer) {
        csv.printf(Locale.ROOT, "%s,%s,%d,%d,%d,%.6f,%d,%d,%d,%d,%d,%s%n", algorithm, type,
                input.first().length(), input.second().length(), run, ms, operations, lps, search,
                answer, input.seed(), fingerprint(input));
    }

    private static String fingerprint(InputGenerator.Input input) {
        try {
            byte[] bytes = (input.first() + "\0" + input.second()).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void printMedian(String algorithm, String type, int n, double[] times) {
        Arrays.sort(times);
        System.out.printf(Locale.ROOT, "%s,%s,n=%d,median_ms=%.6f%n", algorithm, type, n, times[RUNS / 2]);
    }
}
