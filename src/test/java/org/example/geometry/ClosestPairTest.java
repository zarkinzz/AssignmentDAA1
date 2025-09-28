package org.example.geometry;

import org.example.geometry.ClosestPair.Point;
import org.example.metrics.Metrics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class ClosestPairTest {

    // Compare with O(n^2) on small n
    @Test
    void smallMatchesBruteforce() {
        Random rnd = new Random(1);
        for (int n = 2; n <= 200; n += 19) {
            Point[] pts = new Point[n];
            for (int i = 0; i < n; i++) {
                pts[i] = new Point(rnd.nextDouble() * 1000.0, rnd.nextDouble() * 1000.0);
            }
            Metrics m = new Metrics();
            double fast = ClosestPair.closestDistance(pts, m);
            double slow = bruteForce(pts);
            Assertions.assertEquals(slow, fast, 1e-9);
            Assertions.assertTrue(m.maxDepth > 0);
        }
    }

    @Test
    void duplicatesGiveZero() {
        Point[] pts = { new Point(0,0), new Point(1,1), new Point(0,0) };
        Metrics m = new Metrics();
        double d = ClosestPair.closestDistance(pts, m);
        Assertions.assertEquals(0.0, d, 0.0);
    }

    @Test
    void linePoints() {
        // many points on a line
        Point[] pts = new Point[2000];
        for (int i = 0; i < pts.length; i++) pts[i] = new Point(i, 2*i);
        Metrics m = new Metrics();
        double d = ClosestPair.closestDistance(pts, m);
        Assertions.assertTrue(d > 0.0);
    }

    // O(n^2) check for small n
    private static double bruteForce(Point[] a) {
        double best2 = Double.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                double dx = a[i].x - a[j].x;
                double dy = a[i].y - a[j].y;
                double d2 = dx*dx + dy*dy;
                if (d2 < best2) best2 = d2;
            }
        }
        return Math.sqrt(best2);
    }
}
