package org.example.metrics;

/** Counters + recursion depth for one run. */
public class Metrics {
    // counters
    public long comparisons;
    public long allocations;

    // recursion depth
    public long depth;
    public long maxDepth;

    // comparisons
    public void incComp() { comparisons++; }
    public void addComp(long c) { comparisons += c; }

    // allocations
    public void incAlloc() { allocations++; }
    public void addAlloc(long c) { allocations += c; }

    // depth
    public void push() { depth++; if (depth > maxDepth) maxDepth = depth; }
    public void pop()  { depth--; }

    // safe guard (use try-with-resources if удобно)
    public DepthGuard guard() { push(); return new DepthGuard(this); }
    public static final class DepthGuard implements AutoCloseable {
        private final Metrics m; private boolean closed;
        DepthGuard(Metrics m) { this.m = m; }
        @Override public void close() { if (!closed) { m.pop(); closed = true; } }
    }

    // reset all
    public void reset() {
        comparisons = 0; allocations = 0;
        depth = 0; maxDepth = 0;
    }
}
