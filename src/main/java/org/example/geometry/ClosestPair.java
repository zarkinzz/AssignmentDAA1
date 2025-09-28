package org.example.geometry;

import org.example.metrics.Metrics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public final class ClosestPair {

    /** Immutable point. */
    public static final class Point {
        public final double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    private static final Comparator<Point> BY_X = (a, b) -> Double.compare(a.x, b.x);
    private static final Comparator<Point> BY_Y = (a, b) -> Double.compare(a.y, b.y);

    private ClosestPair() {}

    /** Public API: minimal Euclidean distance. */
    public static double closestDistance(Point[] pts, Metrics m) {
        if (pts == null || pts.length < 2) throw new IllegalArgumentException("need >= 2 points");

        // Copy and sort by X once
        Point[] px = Arrays.copyOf(pts, pts.length);
        Arrays.sort(px, BY_X);

        // Also make array sorted by Y once
        Point[] py = Arrays.copyOf(px, px.length);
        Arrays.sort(py, BY_Y);

        // Reusable buffer for strip (by Y)
        Point[] strip = new Point[pts.length];
        m.incAlloc(); // one allocation

        double best2 = solve(px, py, 0, px.length - 1, strip, m); // squared distance
        return Math.sqrt(best2);
    }

    // Recursion on px[l..r]; py has the same points ordered by Y.
    private static double solve(Point[] px, Point[] py, int l, int r, Point[] strip, Metrics m) {
        try (var d = m.guard()) {
            int n = r - l + 1;

            // Small case: brute force
            if (n <= 3) {
                double best2 = Double.POSITIVE_INFINITY;
                for (int i = l; i <= r; i++) {
                    for (int j = i + 1; j <= r; j++) {
                        m.incComp();
                        best2 = Math.min(best2, dist2(px[i], px[j]));
                    }
                }
                return best2;
            }

            int mid = (l + r) >>> 1;
            double midX = px[mid].x;

            // Split py into left/right keeping Y order.
            // Use identity set of left points to route ties.
            Set<Point> leftSet = new HashSet<>(mid - l + 1);
            for (int i = l; i <= mid; i++) leftSet.add(px[i]);

            Point[] pyL = new Point[n];
            Point[] pyR = new Point[n];
            int ly = 0, ry = 0;
            for (Point p : py) {
                if (leftSet.contains(p)) pyL[ly++] = p;
                else                      pyR[ry++] = p;
            }

            // Recurse
            double dl2 = solve(px, Arrays.copyOf(pyL, ly), l, mid, strip, m);
            double dr2 = solve(px, Arrays.copyOf(pyR, ry), mid + 1, r, strip, m);
            double delta2 = Math.min(dl2, dr2);
            double delta  = Math.sqrt(delta2);

            // Build strip near mid line (|x - midX| < delta), order by Y is preserved
            int sy = 0;
            for (Point p : py) {
                if (Math.abs(p.x - midX) < delta) strip[sy++] = p;
            }

            // Scan strip: each point checks next up to ~7 points
            for (int i = 0; i < sy; i++) {
                for (int j = i + 1; j < sy && (strip[j].y - strip[i].y) < delta; j++) {
                    m.incComp();
                    double d2 = dist2(strip[i], strip[j]);
                    if (d2 < delta2) {
                        delta2 = d2;
                        delta  = Math.sqrt(delta2);
                    }
                }
            }

            return delta2;
        }
    }

    // Squared distance (faster in loops)
    private static double dist2(Point a, Point b) {
        double dx = a.x - b.x, dy = a.y - b.y;
        return dx * dx + dy * dy;
    }
}
