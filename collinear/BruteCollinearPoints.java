/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.LinkedList;

public class BruteCollinearPoints {
    private final LineSegment[] lineSegments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null) {
                throw new IllegalArgumentException();
            }
        }

        LinkedList<LineSegment> listLineSegments = new LinkedList<>();
        for (int i1 = 0; i1 < points.length; ++i1) {
            for (int i2 = i1 + 1; i2 < points.length; ++i2) {
                double slope12 = points[i1].slopeTo(points[i2]);
                if (slope12 == Double.NEGATIVE_INFINITY) throw new IllegalArgumentException();
                for (int i3 = i2 + 1; i3 < points.length; ++i3) {
                    double slope13 = points[i1].slopeTo(points[i3]);
                    if (slope13 == Double.NEGATIVE_INFINITY) throw new IllegalArgumentException();
                    if (points[i2].slopeTo(points[i3]) == Double.NEGATIVE_INFINITY)
                        throw new IllegalArgumentException();

                    for (int i4 = i3 + 1; i4 < points.length; ++i4) {
                        double slope14 = points[i1].slopeTo(points[i4]);
                        if (slope14 == Double.NEGATIVE_INFINITY)
                            throw new IllegalArgumentException();
                        if (points[i2].slopeTo(points[i4]) == Double.NEGATIVE_INFINITY)
                            throw new IllegalArgumentException();
                        if (points[i3].slopeTo(points[i4]) == Double.NEGATIVE_INFINITY)
                            throw new IllegalArgumentException();
                        // do check on innermost layer so that
                        // duplicate points are detected without sorting first
                        if (slope12 == slope13 && slope12 == slope14) {
                            // System.out.printf("Match for %d/%d/%d/%d\n", i1, i2, i3, i4);
                            // find a start and end point
                            Point[] segmentPoints = {
                                    points[i1], points[i2], points[i3], points[i4]
                            };

                            Arrays.sort(segmentPoints);
                            // StdOut.println(segmentPoints[0]);
                            // StdOut.println(segmentPoints[3]);
                            listLineSegments
                                    .add(new LineSegment(segmentPoints[0],
                                                         segmentPoints[3]));
                        }
                    }
                }
            }
        }

        // convert to array
        lineSegments = listLineSegments.toArray(new LineSegment[0]);
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return lineSegments.clone();
    }

    public static void main(String[] args) {
        // Point[] points = new Point[13];
        // points[0] = new Point(0, 0);
        // points[1] = new Point(1, 1);
        // points[2] = new Point(0, 1);
        // points[3] = new Point(1, 0);
        // points[4] = new Point(5, 7);
        // points[5] = new Point(3, 2);
        // points[6] = new Point(3, 1);
        // points[7] = new Point(7, 7);
        // points[8] = new Point(9, 9);
        // points[9] = new Point(-1, 0);
        // points[10] = new Point(-5, 0);
        // points[11] = new Point(0, -2);
        // points[12] = new Point(0, -13);
        // BruteCollinearPoints brute = new BruteCollinearPoints(points);

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
