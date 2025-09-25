import org.example.metrics.Metrics;
import org.example.sort.QuickSort;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class QuickSortTest {

    @Test
    void randomCorrectness() {
        Random rnd = new Random(123);
        int n = 20000;
        int[] a = rnd.ints(n, -1_000_000, 1_000_000).toArray();
        int[] b = Arrays.copyOf(a, a.length);

        Metrics m = new Metrics();
        QuickSort.sort(a, m);
        Arrays.sort(b);

        assertArrayEquals(b, a);
        assertTrue(m.maxDepth > 0);
    }

    @Test
    void manyDuplicates() {
        int n = 15000;
        int[] a = new int[n];
        Arrays.fill(a, 7);
        Metrics m = new Metrics();
        QuickSort.sort(a, m);
        assertTrue(isSorted(a));
    }

    @Test
    void alreadySorted() {
        int n = 15000;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        Metrics m = new Metrics();
        QuickSort.sort(a, m);
        assertTrue(isSorted(a));
    }

    @Test
    void reverseSorted() {
        int n = 15000;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        Metrics m = new Metrics();
        QuickSort.sort(a, m);
        assertTrue(isSorted(a));
    }

    @Test
    void depthIsLogLikeOnRandom() {
        int n = 1 << 15; // 32768
        int[] a = new Random(1).ints(n).toArray();
        Metrics m = new Metrics();
        QuickSort.sort(a, m);

        int approx = (int)(Math.log(n) / Math.log(2));
        // allow wide margin because of randomness
        assertTrue(m.maxDepth <= approx * 3, "depth should be O(log n) on average");
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i-1] > a[i]) return false;
        return true;
    }
}
