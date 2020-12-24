/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

// 2d tree offering efficient range and nearest neighbor search
public class KdTree {
    private class Node {
        private final Point2D p;
        private Node left;
        private Node right;
        private final boolean separatedByX;

        public Node(double x, double y, Node left, Node right, boolean separatedByX) {
            this.p = new Point2D(x, y);
            this.left = left;
            this.right = right;
            this.separatedByX = separatedByX;
        }
    }

    private Node root;
    private int size;

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    private static boolean doubleEquals(double d1, double d2) {
        // do not use epsilon as checkstyle complains about it in this case...
        // final double epsilon = 1.0e-7;
        return Math.abs(d2 - d1) == 0;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        root = insert(root, p.x(), p.y(), true);
    }

    private Node insert(Node node, double x, double y, boolean separatedByX) {
        if (node == null) {
            ++size;
            return new Node(x, y, null, null, separatedByX);
        }

        if (doubleEquals(node.p.x(), x) && doubleEquals(node.p.y(), y)) {
            return node;
        }

        // could be simplified to one branch with more complex conditions
        if (separatedByX) {
            if (x < node.p.x()) {
                node.left = insert(node.left, x, y, false);
            }
            else {
                node.right = insert(node.right, x, y, false);
            }
        }
        else {
            if (y < node.p.y()) {
                node.left = insert(node.left, x, y, true);
            }
            else {
                node.right = insert(node.right, x, y, true);
            }
        }

        return node;
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        Node node = root;
        while (node != null) {
            if (doubleEquals(node.p.x(), p.x()) && doubleEquals(node.p.y(), p.y())) {
                return true;
            }
            else if ((node.separatedByX && p.x() < node.p.x()) || (!node.separatedByX
                    && p.y() < node.p.y())) {
                node = node.left;
            }
            else {
                node = node.right;
            }
        }

        return false;
    }

    // draw all points to standard draw
    public void draw() {
        draw(root);
    }

    private void draw(Node node) {
        if (node == null) return;

        draw(node.left);
        StdDraw.point(node.p.x(), node.p.y());
        draw(node.right);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();

        LinkedList<Point2D> list = new LinkedList<>();

        range(root, list, rect);
        return list;
    }

    private void range(Node node, LinkedList<Point2D> list, RectHV rect) {
        if (node == null) return;

        if (rect.contains(node.p)) {
            list.add(node.p);
        }

        // check if left tree could contain a match
        if (
                (node.separatedByX && rect.xmin() < node.p.x()) ||
                        (!node.separatedByX && rect.ymin() < node.p.y())
        ) {
            range(node.left, list, rect);
        }

        // check if right tree could contain a match
        if (
                (node.separatedByX && rect.xmax() >= node.p.x()) ||
                        (!node.separatedByX && rect.ymax() >= node.p.y())
        ) {
            range(node.right, list, rect);
        }
    }

    // return the rectangle that is based on rectBase but limited to one side of node
    private RectHV limitedRectangle(RectHV rectBase, Node node, boolean lookingAtLeftChild) {
        if (node.separatedByX) {
            if (lookingAtLeftChild) {
                return new RectHV(rectBase.xmin(), rectBase.ymin(), node.p.x(), rectBase.ymax());
            }
            else {
                return new RectHV(node.p.x(), rectBase.ymin(), rectBase.xmax(), rectBase.ymax());
            }
        }
        else {
            // separated by y
            if (lookingAtLeftChild) {
                return new RectHV(rectBase.xmin(), rectBase.ymin(), rectBase.xmax(), node.p.y());
            }
            else {
                return new RectHV(rectBase.xmin(), node.p.y(), rectBase.xmax(), rectBase.ymax());
            }
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        return nearest(root, null, p, new RectHV(0, 0, 1, 1));
    }

    private Point2D nearest(Node node, Point2D nearest, Point2D p, RectHV nodeRect) {
        if (node == null) return nearest;

        if (nearest != null && nodeRect.distanceSquaredTo(p) > nearest.distanceSquaredTo(p)) {
            // return as search here is not worth it
            return nearest;
        }

        // StdOut.printf("nearest: looking at %.3f %.3f\n", node.p.x(), node.p.y());

        // check current point
        if (nearest == null ||
                node.p.distanceSquaredTo(p) < nearest.distanceSquaredTo(p)) {
            nearest = node.p;
        }

        // look at more probable side first
        // will skip second child at top of function if it does not make sense to continue
        if ((node.separatedByX && p.x() < node.p.x()) || (!node.separatedByX && p.y() < node.p
                .y())) {
            nearest = nearest(node.left, nearest, p, limitedRectangle(nodeRect, node, true));
            nearest = nearest(node.right, nearest, p, limitedRectangle(nodeRect, node, false));
        }
        else {
            nearest = nearest(node.right, nearest, p, limitedRectangle(nodeRect, node, false));
            nearest = nearest(node.left, nearest, p, limitedRectangle(nodeRect, node, true));
        }


        return nearest;
    }


    // unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree tree = new KdTree();
        // input10.txt
        tree.insert(new Point2D(0.372, 0.497));
        tree.insert(new Point2D(0.564, 0.413));
        tree.insert(new Point2D(0.226, 0.577));
        tree.insert(new Point2D(0.144, 0.179));
        tree.insert(new Point2D(0.083, 0.51));
        tree.insert(new Point2D(0.32, 0.708));
        tree.insert(new Point2D(0.417, 0.362));
        tree.insert(new Point2D(0.862, 0.825));
        tree.insert(new Point2D(0.785, 0.725));
        tree.insert(new Point2D(0.499, 0.208));

        StdOut.println(tree.nearest(new Point2D(0.42, 0.21)));
    }
}


