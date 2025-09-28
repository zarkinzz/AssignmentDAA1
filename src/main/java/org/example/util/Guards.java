package org.example.util;

import org.example.metrics.Metrics;

/** Small helper for try-with-resources. */
public final class Guards {
    private Guards() {}
    /** Use: try (var d = Guards.depth(m)) { ... } */
    public static Metrics.DepthGuard depth(Metrics m) { return m.guard(); }
}
