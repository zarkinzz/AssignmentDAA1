package org.example.util;
import static org.example.util.ArraysUtil.swap;
import java.util.concurrent.ThreadLocalRandom;

/** Simple array helpers. */
public final class ArraysUtil {
    private ArraysUtil() {}

    public static void swap(int[] a, int i, int j) {
        if (i == j) return;
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }

    /** Fisher–Yates in-place shuffle. */
    public static void shuffle(int[] a) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = a.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            swap(a, i, j);
        }
    }
}
