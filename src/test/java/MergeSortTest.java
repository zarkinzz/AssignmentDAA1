import org.example.metrics.Metrics;
import org.example.sort.MergeSort;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class MergeSortTest {

    @Test
    void randomCorrectness() {
        // Make random data and compare with Arrays.sort()
        Random rnd = new Random(42);
        int n = 10000;
        int[] a = rnd.ints(n, -1_000_000, 1_000_000).toArray();
        int[] b = Arrays.copyOf(a, a.length);

        Metrics m = new Metrics();
        MergeSort.sort(a, m);
        Arrays.sort(b);

        assertArrayEquals(b, a);
        assertEquals(1, m.allocations);     // one buffer
        assertTrue(m.maxDepth > 0);
    }

    @Test
    void smallUsesInsertion() {
        // Near the cutoff: depth should be small
        int n = MergeSort.CUTOFF;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;

        Metrics m = new Metrics();
        MergeSort.sort(a, m);

        assertTrue(isSorted(a));
        assertTrue(m.maxDepth <= 3);
    }

    @Test
    void reverseArray() {
        // Worst order for many algorithms
        int n = 5000;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;

        int[] b = Arrays.copyOf(a, a.length);
        Metrics m = new Metrics();
        MergeSort.sort(a, m);
        Arrays.sort(b);
        assertArrayEquals(b, a);
    }

    @Test
    void manyDuplicates() {
        // All values the same → still sorted and stable
        int n = 8000;
        int[] a = new int[n];
        Arrays.fill(a, 7);
        Metrics m = new Metrics();
        MergeSort.sort(a, m);
        assertTrue(isSorted(a));
    }

    @Test
    void depthBoundRough() {
        // Depth should be about log2(n)
        int n = 1 << 15;
        int[] a = new Random(1).ints(n).toArray();
        Metrics m = new Metrics();
        MergeSort.sort(a, m);

        int bound = (int) (Math.log(n) / Math.log(2)) + 5;
        assertTrue(m.maxDepth <= bound);
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i-1] > a[i]) return false;
        return true;
    }
}
