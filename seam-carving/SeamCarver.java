import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    private Picture picture;
    private double[] energyCache; // cache for result of energy() calls

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();

        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    private static int rgb2red(int rgb) {
        return (rgb >> 16) & 0xff;
    }

    private static int rgb2green(int rgb) {
        return (rgb >> 8) & 0xff;
    }

    private static int rgb2blue(int rgb) {
        return (rgb) & 0xff;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) {
            throw new IllegalArgumentException();
        }
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            // border pixel
            return 1000;
        }

        if (energyCache == null || energyCache.length != width() * height()) {
            // initialize energy cache
            energyCache = new double[width() * height()];
            for (int i = 0; i < energyCache.length; ++i) energyCache[i] = Double.NaN;
        }

        if (!Double.isNaN(energyCache[coordinatesToId(x, y)])) {
            // value is cached
            return energyCache[coordinatesToId(x, y)];
        }

        int rgbXp1 = picture.getRGB(x + 1, y);
        int rgbXm1 = picture.getRGB(x - 1, y);
        int rX = rgb2red(rgbXp1) - rgb2red(rgbXm1);
        int gX = rgb2green(rgbXp1) - rgb2green(rgbXm1);
        int bX = rgb2blue(rgbXp1) - rgb2blue(rgbXm1);

        int rgbYp1 = picture.getRGB(x, y + 1);
        int rgbYm1 = picture.getRGB(x, y - 1);
        int rY = rgb2red(rgbYp1) - rgb2red(rgbYm1);
        int gY = rgb2green(rgbYp1) - rgb2green(rgbYm1);
        int bY = rgb2blue(rgbYp1) - rgb2blue(rgbYm1);

        double deltaX2 = rX * rX + gX * gX + bX * bX;
        double deltaY2 = rY * rY + gY * gY + bY * bY;

        double result = Math.sqrt(deltaX2 + deltaY2);
        energyCache[coordinatesToId(x, y)] = result;

        return result;
    }

    private int coordinatesToId(int x, int y) {
        return y * width() + x;
    }

    private int idToCoordinateY(int id) {
        return id / width();
    }

    private int idToCoordinateX(int id) {
        return id % width();
    }

    // helper function to relax edge without explicit digraph
    private static void relax(double[] distTo, int[] edgeTo, int vFrom, int vTo, double weight) {
        if (distTo[vTo] > distTo[vFrom] + weight) {
            edgeTo[vTo] = vFrom;
            distTo[vTo] = distTo[vFrom] + weight;
        }
    }

    // determine sequence of y indices for horizontal seam
    public int[] findHorizontalSeam() {
        // setup shortest path search without explicit EdgeWeightedDigraph etc.
        double[] distTo = new double[width() * height() + 1];
        for (int i = 0; i < distTo.length; ++i) distTo[i] = Double.POSITIVE_INFINITY;

        int[] edgeTo = new int[width() * height() + 1];
        for (int i = 0; i < edgeTo.length; ++i) edgeTo[i] = -1;

        // virtual vertex (connected to all pixels in the first column)
        int vLeft = width() * height();
        distTo[vLeft] = 0;

        // connect vLeft to first col
        for (int y = 0; y < height(); ++y) {
            int xLeft = 0;
            int v = coordinatesToId(xLeft, y);
            relax(distTo, edgeTo, vLeft, v, energy(xLeft, y));
        }

        // relax pixels with edge to pixels in the next col to the right
        // (leverages the topological order of the implicit digraph)
        for (int x = 0; x + 1 < width(); ++x) { // skip last col
            int xRight = x + 1;
            for (int y = 0; y < height(); ++y) {
                for (int yRight = Math.max(0, y - 1); yRight <= Math.min(height() - 1, y + 1);
                     ++yRight) {
                    relax(distTo, edgeTo, coordinatesToId(x, y), coordinatesToId(xRight, yRight),
                          energy(xRight, yRight));
                }
            }
        }

        // identify pixel in last col with minimal dist to vLeft
        int vLastCol = -1;
        double distToLastCol = Double.POSITIVE_INFINITY;
        for (int y = 0; y < height(); ++y) {
            int x = width() - 1;
            if (distToLastCol > distTo[coordinatesToId(x, y)]) {
                distToLastCol = distTo[coordinatesToId(x, y)];
                vLastCol = coordinatesToId(x, y);
            }
        }

        // save result in array of y coordinates
        int[] yCoordinates = new int[width()];
        for (int v = vLastCol; v != vLeft; v = edgeTo[v]) {
            int x = idToCoordinateX(v);
            yCoordinates[x] = idToCoordinateY(v);
        }

        return yCoordinates;
    }

    // determine sequence of x indices for vertical seam
    public int[] findVerticalSeam() {
        // setup shortest path search without explicit EdgeWeightedDigraph etc.
        double[] distTo = new double[width() * height() + 1];
        for (int i = 0; i < distTo.length; ++i) distTo[i] = Double.POSITIVE_INFINITY;

        int[] edgeTo = new int[width() * height() + 1];
        for (int i = 0; i < edgeTo.length; ++i) edgeTo[i] = -1;

        // virtual vertex (connected to all pixels in the first row)
        int vTop = width() * height();
        distTo[vTop] = 0;

        // connect vTop to first row
        for (int x = 0; x < width(); ++x) {
            int yTop = 0;
            int v = coordinatesToId(x, yTop);
            relax(distTo, edgeTo, vTop, v, energy(x, yTop));
        }

        // relax pixels with edge to pixels in the next row below
        // (leverages the topological order of the implicit digraph)
        for (int y = 0; y + 1 < height(); ++y) { // skip last row
            int yBelow = y + 1;
            for (int x = 0; x < width(); ++x) {
                for (int xBelow = Math.max(0, x - 1); xBelow <= Math.min(width() - 1, x + 1);
                     ++xBelow) {
                    relax(distTo, edgeTo, coordinatesToId(x, y), coordinatesToId(xBelow, yBelow),
                          energy(xBelow, yBelow));
                }
            }
        }

        // identify pixel in last row with minimal dist to vTop
        int vLastRow = -1;
        double distToLastRow = Double.POSITIVE_INFINITY;
        for (int x = 0; x < width(); ++x) {
            int y = height() - 1;
            if (distToLastRow > distTo[coordinatesToId(x, y)]) {
                distToLastRow = distTo[coordinatesToId(x, y)];
                vLastRow = coordinatesToId(x, y);
            }
        }

        // save result in array of x coordinates
        int[] xCoordinates = new int[height()];
        for (int v = vLastRow; v != vTop; v = edgeTo[v]) {
            int y = idToCoordinateY(v);
            xCoordinates[y] = idToCoordinateX(v);
        }

        return xCoordinates;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width()) throw new IllegalArgumentException();

        // check values
        for (int i = 0; i < seam.length; ++i) {
            if (i > 0) {
                // not first element
                if (Math.abs(seam[i] - seam[i - 1]) > 1) throw new IllegalArgumentException();
            }
            if (seam[i] < 0 || seam[i] >= height()) throw new IllegalArgumentException();
        }

        Picture newPic = new Picture(width(), height() - 1);
        for (int x = 0; x < width(); ++x) {
            int yNew = 0;
            for (int y = 0; y < height(); ++y) {
                if (y != seam[x]) {
                    // do not skip this
                    newPic.set(x, yNew, this.picture.get(x, y));
                    ++yNew;
                }
            }
        }
        picture = newPic;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height()) throw new IllegalArgumentException();

        // check values
        for (int i = 0; i < seam.length; ++i) {
            if (i > 0) {
                // not first element
                if (Math.abs(seam[i] - seam[i - 1]) > 1) throw new IllegalArgumentException();
            }
            if (seam[i] < 0 || seam[i] >= width()) throw new IllegalArgumentException();
        }

        Picture newPic = new Picture(width() - 1, height());
        for (int y = 0; y < height(); ++y) {
            int xNew = 0;
            for (int x = 0; x < width(); ++x) {
                if (x != seam[y]) {
                    // do not skip this
                    newPic.set(xNew, y, this.picture.get(x, y));
                    ++xNew;
                }
            }
        }
        picture = newPic;
    }
}
