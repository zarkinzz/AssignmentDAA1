package org.example.util;

import org.example.metrics.Metrics;

import java.util.concurrent.ThreadLocalRandom;

/** Partition helpers for quicksort/select. */
public final class PartitionUtil {
    private PartitionUtil() {}

    /**
     * Lomuto partition with random pivot.
     * Returns final index of pivot. Range: [lo..hi].
     */
    public static int lomutoRandom(int[] a, int lo, int hi, Metrics m) {
        int n = hi - lo + 1;
        int p = lo + ThreadLocalRandom.current().nextInt(n);
        ArraysUtil.swap(a, lo, p);

        int pivot = a[lo];
        int i = lo;
        for (int j = lo + 1; j <= hi; j++) {
            m.incComp();
            if (a[j] < pivot) {
                i++;
                ArraysUtil.swap(a, i, j);
            }
        }
        ArraysUtil.swap(a, lo, i);
        return i;
    }

    /**
     * Hoare partition (pivot = a[(lo+hi)/2]).
     * Returns index j such that [lo..j] <= pivot <= [j+1..hi] (approx).
     */
    public static int hoare(int[] a, int lo, int hi, Metrics m) {
        int pivot = a[(lo + hi) >>> 1];
        int i = lo - 1, j = hi + 1;
        while (true) {
            do { i++; m.incComp(); } while (a[i] < pivot);
            do { j--; m.incComp(); } while (a[j] > pivot);
            if (i >= j) return j;
            ArraysUtil.swap(a, i, j);
        }
    }

    /**
     * 3-way partition by value: < pivot | == pivot | > pivot.
     * Returns [lt, gt] bounds of == block.
     */
    public static int[] threeWayByValue(int[] a, int lo, int hi, int pivot, Metrics m) {
        int lt = lo, i = lo, gt = hi;
        while (i <= gt) {
            m.incComp();
            if (a[i] < pivot) {
                ArraysUtil.swap(a, lt++, i++);
            } else {
                m.incComp();
                if (a[i] > pivot) {
                    ArraysUtil.swap(a, i, gt--);
                } else {
                    i++;
                }
            }
        }
        return new int[]{lt, gt};
    }
}
