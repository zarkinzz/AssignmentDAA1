package org.example.select;

import org.example.metrics.Metrics;
import org.example.sort.InsertionSort;
import org.example.util.ArraysUtil;

import static org.example.util.PartitionUtil.threeWayByValue;

/**
 * Deterministic Select (Median-of-Medians, groups of 5). O(n).
 * Returns k-th smallest (0-based).
 */
public final class Select {
    private static final int CUTOFF = 24; // small range -> insertion sort
    private Select() {}

    // Public API
    public static int select(int[] a, int k, Metrics m) {
        if (a == null || a.length == 0) throw new IllegalArgumentException("empty array");
        if (k < 0 || k >= a.length) throw new IllegalArgumentException("k out of range");
        return selectRange(a, 0, a.length - 1, k, m);
    }

    // Work inside a[l..r]
    private static int selectRange(int[] a, int l, int r, int k, Metrics m) {
        try (var d = m.guard()) {
            int n = r - l + 1;
            if (n <= CUTOFF) {
                InsertionSort.sort(a, l, r, m);
                return a[l + k];
            }

            // 1) Make medians of groups of 5 and move them to the front
            int write = l;
            for (int i = l; i <= r; i += 5) {
                int gL = i;
                int gR = Math.min(i + 4, r);
                InsertionSort.sort(a, gL, gR, m);
                int med = gL + ((gR - gL) >> 1);
                swap(a, write++, med);
            }

            // 2) Pivot value = median of medians
            int medCount = write - l;
            int pivotVal = selectRange(a, l, write - 1, medCount >> 1, m);

            // 3) 3-way partition by pivot value
            int[] bd = threeWayByValue(a, l, r, pivotVal, m);
            int lt = bd[0], gt = bd[1];

            int leftSize = lt - l;
            int midSize  = gt - lt + 1;

            if (k < leftSize) return selectRange(a, l, lt - 1, k, m);
            if (k < leftSize + midSize) return pivotVal;
            return selectRange(a, gt + 1, r, k - leftSize - midSize, m);
        }
    }

    // local swap (simple)
    private static void swap(int[] a, int i, int j) {
        ArraysUtil.swap(a, i, j);
    }
}
