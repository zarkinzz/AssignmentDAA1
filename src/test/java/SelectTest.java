package org.example.select;

import org.example.metrics.Metrics;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SelectTest {

    @Test
    void matchesArraysSortOnRandom() {
        Random rnd = new Random(7);
        for (int trial = 0; trial < 100; trial++) {
            int n = 200 + rnd.nextInt(800); // 200..999
            int[] a = rnd.ints(n, -100000, 100000).toArray();
            int[] b = Arrays.copyOf(a, a.length);
            Arrays.sort(b);

            int k = rnd.nextInt(n);
            Metrics m = new Metrics();
            int got = Select.select(a, k, m);
            assertEquals(b[k], got);
            assertTrue(m.maxDepth > 0);
        }
    }

    @Test
    void edgesAndDuplicates() {
        int[] same = {5,5,5,5,5,5,5};
        Metrics m1 = new Metrics();
        assertEquals(5, Select.select(Arrays.copyOf(same, same.length), 0, m1));
        assertEquals(5, Select.select(Arrays.copyOf(same, same.length), same.length - 1, m1));

        int[] b = {9,1,1,1,7,3,3,3,8};
        int[] sorted = Arrays.copyOf(b, b.length);
        Arrays.sort(sorted);
        Metrics m2 = new Metrics();
        assertEquals(sorted[0], Select.select(Arrays.copyOf(b, b.length), 0, m2));
        assertEquals(sorted[sorted.length-1], Select.select(Arrays.copyOf(b, b.length), sorted.length-1, m2));
    }

    @Test
    void tinyCases() {
        Metrics m = new Metrics();
        assertEquals(42, Select.select(new int[]{42}, 0, m));
        int[] a = {3,2};
        assertEquals(2, Select.select(Arrays.copyOf(a, a.length), 0, m));
        assertEquals(3, Select.select(Arrays.copyOf(a, a.length), 1, m));
    }
}
