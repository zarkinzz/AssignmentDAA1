package org.example.sort;

import org.example.metrics.Metrics;
import java.util.Objects;

public final class MergeSort {

    // For small arrays we use insertion sort (it is faster there)
    public static final int CUTOFF = 24;

    private MergeSort() {}

    // Public method: create one buffer and reuse it
    public static void sort(int[] a, Metrics m) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(m, "metrics");
        if (a.length <= 1) return;

        int[] tmp = new int[a.length]; // one temporary buffer
        m.incAlloc();
        sort(a, 0, a.length - 1, tmp, m);
    }

    // Recursive part
    private static void sort(int[] a, int l, int r, int[] tmp, Metrics m) {
        try (Metrics.DepthGuard d = m.guard()) { // track recursion depth
            int n = r - l + 1;
            if (n <= CUTOFF) {            // small part → insertion sort
                InsertionSort.sort(a, l, r, m);
                return;
            }

            int mid = (l + r) >>> 1;
            sort(a, l, mid, tmp, m);      // left half
            sort(a, mid + 1, r, tmp, m);  // right half

            // If already in order, skip merge (small speed up)
            m.incComp();
            if (a[mid] <= a[mid + 1]) return;

            merge(a, l, mid, r, tmp, m);  // combine two sorted halves
        }
    }

    // Merge two sorted parts [l..mid] and [mid+1..r]
    private static void merge(int[] a, int l, int mid, int r, int[] tmp, Metrics m) {
        int i = l, j = mid + 1, k = l;

        // Take the smaller value each time (stable: use <=)
        while (i <= mid && j <= r) {
            m.incComp();
            if (a[i] <= a[j]) {
                tmp[k++] = a[i++];
            } else {
                tmp[k++] = a[j++];
            }
        }
        // Copy the rest
        while (i <= mid) tmp[k++] = a[i++];
        while (j <= r)   tmp[k++] = a[j++];

        // Write back to the main array
        System.arraycopy(tmp, l, a, l, r - l + 1);
    }
}
