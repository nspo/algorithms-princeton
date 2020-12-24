import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

// brute force solution
public class PointSET {

    private final SET<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(
            Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.clear();
        // StdDraw.setPenRadius(0.01);
        for (Point2D p : set) {
            StdDraw.point(p.x(), p.y());
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(
            RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        LinkedList<Point2D> list = new LinkedList<>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                list.add(p);
            }
        }
        return list;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(
            Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        Point2D pNearest = null;
        for (Point2D pIter : set) {
            if (pNearest == null || p.distanceSquaredTo(pIter) < p.distanceSquaredTo(pNearest)) {
                pNearest = pIter;
            }
        }

        return pNearest;
    }

    // unit testing of the methods (optional)
    public static void main(
            String[] args) {
        PointSET pSet = new PointSET();

        pSet.insert(new Point2D(0.1, 0.1));
        pSet.insert(new Point2D(0.2, 0.2));
        pSet.insert(new Point2D(0.3, 0.4));
        pSet.draw();

        for (Point2D p : pSet.range(new RectHV(0, 0, 0.2, 0.2))) {
            StdOut.printf("in rect: %f / %f\n", p.x(), p.y());
        }

        Point2D pNearest = pSet.nearest(new Point2D(0.15, 0.1));
        StdOut.printf("Nearest to 0.15/0.1: %f/%f\n", pNearest.x(), pNearest.y());
    }
}
