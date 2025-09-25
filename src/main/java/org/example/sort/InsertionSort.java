package org.example.sort;

import org.example.metrics.Metrics;

final class InsertionSort {
    private InsertionSort() {}

    // Sort a[l..r] using insertion sort (simple and good for small parts)
    static void sort(int[] a, int l, int r, Metrics m) {
        for (int i = l + 1; i <= r; i++) {
            int x = a[i];
            int j = i - 1;
            // Move bigger items to the right
            while (j >= l) {
                m.incComp();
                if (a[j] <= x) break;
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = x;
        }
    }
}
