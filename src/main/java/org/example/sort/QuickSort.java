package org.example.sort;
import org.example.metrics.Metrics;
import static org.example.util.PartitionUtil.lomutoRandom;
import java.util.concurrent.ThreadLocalRandom;

public final class QuickSort {

    // For small parts we use insertion sort (faster on small n)
    public static final int CUTOFF = 24;

    private QuickSort() {}

    // Public entry
    public static void sort(int[] a, Metrics m) {
        if (a == null || a.length <= 1) return;
        qs(a, 0, a.length - 1, m);
    }

    // Robust quicksort:
    // - randomized pivot
    // - recurse on the smaller side
    // - loop (iterate) on the larger side (bounded stack)
    private static void qs(int[] a, int lo, int hi, Metrics m) {
        while (lo < hi) {
            int n = hi - lo + 1;
            if (n <= CUTOFF) {
                InsertionSort.sort(a, lo, hi, m);
                return;
            }

            // pick random pivot and move to lo
            int p = lo + ThreadLocalRandom.current().nextInt(n);
            swap(a, lo, p);

            // Lomuto partition (simple and clear)
            int pivot = a[lo];
            int i = lo;
            for (int j = lo + 1; j <= hi; j++) {
                m.incComp();
                if (a[j] < pivot) {
                    i++;
                    swap(a, i, j);
                }
            }
            swap(a, lo, i);

            int leftSize  = i - lo;
            int rightSize = hi - i;

            // recurse into smaller side, loop on larger
            try (Metrics.DepthGuard d = m.guard()) {
                if (leftSize < rightSize) {
                    if (leftSize > 1) qs(a, lo, i - 1, m);
                    lo = i + 1; // iterate on right
                } else {
                    if (rightSize > 1) qs(a, i + 1, hi, m);
                    hi = i - 1; // iterate on left
                }
            }
        }
    }

    private static void swap(int[] a, int i, int j) {
        if (i == j) return;
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
}
