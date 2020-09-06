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

public class FastCollinearPoints {
    private final LineSegment[] lineSegments;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null) {
                throw new IllegalArgumentException();
            }
        }

        // copy points to sort them separately from original array
        Point[] sortedPoints = points.clone();
        Arrays.sort(sortedPoints);

        // check for duplicates
        for (int i = 1; i < sortedPoints.length; ++i) {
            if (sortedPoints[i - 1].compareTo(sortedPoints[i]) == 0) {
                // duplicate found
                throw new IllegalArgumentException();
            }
        }

        // use a LinkedList b/c ArrayList is forbidden and so are custom types
        LinkedList<LineSegment> listLineSegments = new LinkedList<>();

        for (Point p : sortedPoints) {
            // copy sortedPoints into a working copy which can be reordered
            // this must be done for each point so that assumptions below (specifically that
            // "smaller" points come first in the array sorted by slope) hold
            // this requires a stable sort of course
            Point[] workingPoints = sortedPoints.clone();
            Arrays.sort(workingPoints, p.slopeOrder());

            LinkedList<Point> possibleSolutions = new LinkedList<>();

            int i = 1; // index 0 is p itself, so skip that
            while (i < workingPoints.length) {
                final double slope = p.slopeTo(workingPoints[i]);
                // find all points with the same slope w.r.t. p
                possibleSolutions.clear();
                while (i < workingPoints.length && p.slopeTo(workingPoints[i]) == slope) {
                    possibleSolutions.add(workingPoints[i]);
                    ++i;
                }

                // add possibleSolution iff there are enough (4 including p) matches
                // and p is the "smallest" point (so that no duplicate solutions are added)

                if (possibleSolutions.size() + 1 >= 4
                        && p.compareTo(possibleSolutions.peekFirst()) < 0) {
                    listLineSegments.add(new LineSegment(p, possibleSolutions.peekLast()));
                }
            }
        }

        // copy into an array
        lineSegments = listLineSegments.toArray(new LineSegment[0]);
    }

    // the number of line segments
    public int numberOfSegments() {
        return lineSegments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        // copy everything so FastCollinearPoints is immutable from outside
        return lineSegments.clone();
    }

    public static void main(String[] args) {

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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}
