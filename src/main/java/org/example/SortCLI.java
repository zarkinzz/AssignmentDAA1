package org.example;

import org.example.metrics.CSVWriter;
import org.example.metrics.Metrics;
import org.example.select.Select;
import org.example.sort.MergeSort;
import org.example.sort.QuickSort;
import org.example.geometry.ClosestPair;
import org.example.geometry.ClosestPair.Point;


import java.util.*;


public final class SortCLI {

    public static void main(String[] args) throws Exception {
        Map<String, String> arg = parseArgs(args);

        String algo   = arg.getOrDefault("algo", "merge").toLowerCase(Locale.ROOT);
        int n         = Integer.parseInt(arg.getOrDefault("n", "100000"));
        int trials    = Integer.parseInt(arg.getOrDefault("trials", "3"));
        long seed     = Long.parseLong(arg.getOrDefault("seed", "42"));
        String csvOut = arg.getOrDefault("csv", "AssignmentDAA1.csv");
        int k         = Integer.parseInt(arg.getOrDefault("k", String.valueOf(Math.max(0, n / 2))));

        try (CSVWriter csv = new CSVWriter(csvOut)) {
            Random rnd = new Random(seed);

            for (int t = 1; t <= trials; t++) {
                Metrics m = new Metrics();

                switch (algo) {
                    case "merge" -> {
                        int[] a = rnd.ints(n, -1_000_000, 1_000_000).toArray();
                        long t0 = System.nanoTime();
                        MergeSort.sort(a, m);
                        long t1 = System.nanoTime();
                        csv.writeRow("merge", n, t, t1 - t0, (int) m.maxDepth, m.comparisons, m.allocations);
                    }
                    case "quick" -> {
                        int[] a = rnd.ints(n, -1_000_000, 1_000_000).toArray();
                        long t0 = System.nanoTime();
                        QuickSort.sort(a, m);
                        long t1 = System.nanoTime();
                        csv.writeRow("quick", n, t, t1 - t0, (int) m.maxDepth, m.comparisons, m.allocations);
                    }
                    case "select" -> {
                        int[] a = rnd.ints(n, -1_000_000, 1_000_000).toArray();
                        int kk = Math.min(Math.max(0, k), n - 1);
                        long t0 = System.nanoTime();
                        int val = Select.select(a, kk, m);
                        long t1 = System.nanoTime();
                        // мы не используем val дальше; важно время и метрики
                        csv.writeRow("select(k=" + kk + ")", n, t, t1 - t0, (int) m.maxDepth, m.comparisons, m.allocations);
                    }
                    case "closest" -> {
                        Point[] pts = new Point[n];
                        for (int i = 0; i < n; i++) {
                            // simple random plane
                            pts[i] = new Point(rnd.nextDouble() * 1_000_000.0,
                                    rnd.nextDouble() * 1_000_000.0);
                        }
                        long t0 = System.nanoTime();
                        double d = ClosestPair.closestDistance(pts, m);
                        long t1 = System.nanoTime();
                        csv.writeRow("closest", n, t, t1 - t0, (int) m.maxDepth, m.comparisons, m.allocations);
                    }
                    default -> {
                        System.err.println("Unknown --algo: " + algo);
                        System.err.println("Use: merge | quick | select | closest");
                        return;
                    }
                }
            }
        }
    }

    // Tiny arg parser: --key value  (B1 level, simple)
    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            if (s.startsWith("--")) {
                String key = s.substring(2);
                String val = "true";
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    val = args[++i];
                }
                map.put(key, val);
            }
        }
        return map;
    }
}
