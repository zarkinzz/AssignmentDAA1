package org.example.geometry;

import org.example.metrics.Metrics;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Closest pair of points in 2D.
 * Time: O(n log n). We sort by X once, split by X, check strip by Y.
 * We use squared distance inside loops (faster), sqrt only at the end.
 */
public final class ClosestPair {

    /** Simple point (immutable). */
    public static final class Point {
        public final double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }

    private static final Comparator<Point> BY_X = (a, b) -> Double.compare(a.x, b.x);
    private static final Comparator<Point> BY_Y = (a, b) -> Double.compare(a.y, b.y);

    private ClosestPair() {}

    /** Public API: return minimal Euclidean distance. */
    public static double closestDistance(Point[] pts, Metrics m) {
        if (pts == null || pts.length < 2) throw new IllegalArgumentException("need >= 2 points");

        // copy and sort by X once
        Point[] px = Arrays.copyOf(pts, pts.length);
        Arrays.sort(px, BY_X);

        // also prepare array sorted by Y once
        Point[] py = Arrays.copyOf(px, px.length);
        Arrays.sort(py, BY_Y);

        // reusable buffer for strip (by Y)
        Point[] strip = new Point[pts.length];
        m.incAlloc(); // one buffer

        double best2 = solve(px, py, 0, px.length - 1, strip, m); // squared
        return Math.sqrt(best2);
    }

    // Recursion on px[l..r], py is the same set sorted by Y (only those in [l..r])
    private static double solve(Point[] px, Point[] py, int l, int r, Point[] strip, Metrics m) {
        try (var d = m.guard()) {
            int n = r - l + 1;

            // small case: brute force
            if (n <= 3) {
                double best2 = Double.POSITIVE_INFINITY;
                for (int i = l; i <= r; i++) {
                    for (int j = i + 1; j <= r; j++) {
                        m.incComp();
                        best2 = Math.min(best2, dist2(px[i], px[j]));
                    }
                }
                // keep py sorted by Y for this range (already ok)
                return best2;
            }

            int mid = (l + r) >>> 1;
            double midX = px[mid].x;

            // split px
            // left: [l..mid], right: [mid+1..r]

            // split py into pyL/pyR keeping Y order
            Point[] pyL = new Point[n];
            Point[] pyR = new Point[n];
            int ly = 0, ry = 0;
            for (Point p : py) {
                // check if p belongs to current segment by X-position using binary search
                // (px is by X, so compare p.x to midX)
                if (p.x < px[l].x || p.x > px[r].x) continue; // outside current range
                if (p.x < midX || (p.x == midX && indexLE(px, l, mid, p))) {
                    pyL[ly++] = p;
                } else {
                    pyR[ry++] = p;
                }
            }

            // recurse
            double dl2 = solve(px, trim(pyL, ly), l, mid, strip, m);
            double dr2 = solve(px, trim(pyR, ry), mid + 1, r, strip, m);
            double delta2 = Math.min(dl2, dr2);
            double delta = Math.sqrt(delta2);

            // build strip: points within delta from mid line, order by Y (py already by Y)
            int sy = 0;
            for (Point p : py) {
                if (p == null) break;
                if (p.x < px[l].x || p.x > px[r].x) continue;
                if (Math.abs(p.x - midX) < delta) {
                    strip[sy++] = p;
                }
            }

            // scan strip: check next up to 7 points by Y
            for (int i = 0; i < sy; i++) {
                for (int j = i + 1; j < sy && (strip[j].y - strip[i].y) < delta; j++) {
                    m.incComp();
                    double d2 = dist2(strip[i], strip[j]);
                    if (d2 < delta2) {
                        delta2 = d2;
                        delta = Math.sqrt(delta2);
                    }
                }
            }

            return delta2;
        }
    }

    // squared distance
    private static double dist2(Point a, Point b) {
        double dx = a.x - b.x, dy = a.y - b.y;
        return dx * dx + dy * dy;
    }

    // helper: is point p among px[l..mid] (ties by identity ok for our arrays)
    private static boolean indexLE(Point[] px, int l, int mid, Point p) {
        // binary search by x to approximate; then linear check for exact object is overkill,
        // but it is fine for clarity. We only use this for tie (x == midX) routing.
        return p.x <= px[mid].x;
    }

    // copy first len elements (keeps Y order)
    private static Point[] trim(Point[] a, int len) {
        return len == a.length ? a : Arrays.copyOf(a, len);
    }
}
