package org.example.bench;

import org.example.select.Select;
import org.example.sort.QuickSort;
import org.example.sort.MergeSort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Compare deterministic Select vs full sorting (Quick/Merge).
 * We measure time per operation (ns/op).
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
public class SelectVsSortBench {

    @State(Scope.Thread)
    public static class Data {
        @Param({"10000", "20000", "50000"})
        public int n;

        @Param({"123"})
        public long seed;

        int[] base;       // master array
        int k;            // index to select

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(seed);
            base = rnd.ints(n, -1_000_000, 1_000_000).toArray();
            k = n / 2; // median
        }

        // fresh copies for each invocation (to avoid in-place effects)
        int[] copy() { return Arrays.copyOf(base, base.length); }
    }

    /** Deterministic select (median-of-medians). */
    @Benchmark
    public void select_mid(Data d, Blackhole bh) {
        int[] a = d.copy();
        int v = Select.select(a, d.k, /*metrics*/ new org.example.metrics.Metrics());
        bh.consume(v);
    }

    /** Full sort via QuickSort, then read k-th. */
    @Benchmark
    public void quicksort_then_get_k(Data d, Blackhole bh) {
        int[] a = d.copy();
        QuickSort.sort(a, /*metrics*/ new org.example.metrics.Metrics());
        bh.consume(a[d.k]);
    }

    /** Full sort via MergeSort, then read k-th. */
    @Benchmark
    public void mergesort_then_get_k(Data d, Blackhole bh) {
        int[] a = d.copy();
        MergeSort.sort(a, /*metrics*/ new org.example.metrics.Metrics());
        bh.consume(a[d.k]);
    }

    /** Java Arrays.sort as a reference. */
    @Benchmark
    public void arrays_sort_then_get_k(Data d, Blackhole bh) {
        int[] a = d.copy();
        Arrays.sort(a);
        bh.consume(a[d.k]);
    }
}
