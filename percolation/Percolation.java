import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int edgeLength;
    private int numOpen;
    private boolean[] open;
    private final WeightedQuickUnionUF wqu; // for finding connection
    private final WeightedQuickUnionUF wquFull; // for finding fullness
    private final int idUpperVirtualSite;
    private final int idLowerVirtualSite;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n <= 0");
        }

        edgeLength = n;
        numOpen = 0;

        open = new boolean[n * n];
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                open[i * n + j] = false;
            }
        }

        wqu = new WeightedQuickUnionUF(n * n + 2); // also consider virtual nodes
        wquFull = new WeightedQuickUnionUF(n * n + 1); // only consider upper node

        // set up virtual sites (VS) at the end
        idUpperVirtualSite = n * n;
        idLowerVirtualSite = n * n + 1;
        if (n > 1) {
            for (int i = index(1, 1); i <= index(1, n); ++i) {
                wqu.union(idUpperVirtualSite, i);
                wquFull.union(idUpperVirtualSite, i);
            }
            for (int i = index(n, 1); i <= index(n, n); ++i) {
                wqu.union(idLowerVirtualSite, i);
            }
        }
    }

    private void ensureValidSite(int row, int col) {
        if (!isValidSite(row, col)) {
            throw new IllegalArgumentException("Invalid row or col");
        }
    }

    private boolean isValidSite(int row, int col) {
        return !(row < 1 || col < 1 || row > edgeLength || col > edgeLength);
    }

    private int index(int row, int col) {
        return (row - 1) * edgeLength + col - 1;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        ensureValidSite(row, col);

        if (!open[index(row, col)]) {
            numOpen += 1;
            open[index(row, col)] = true;
            // neighbor offsets as (rowOffset,colOffset)

            if (edgeLength == 1) {
                // corner case
                wqu.union(index(row, col), idUpperVirtualSite);
                wquFull.union(index(row, col), idUpperVirtualSite);
                wqu.union(index(row, col), idLowerVirtualSite);
            }

            int[][] neighborOffsets = {
                    { -1, 0 }, // above
                    { 1, 0 }, // below
                    { 0, -1 }, // left
                    { 0, 1 } // right
            };
            for (int i = 0; i < neighborOffsets.length; ++i) {
                int neighborRow = row + neighborOffsets[i][0];
                int neighborCol = col + neighborOffsets[i][1];
                if (isValidSite(neighborRow, neighborCol) && open[index(neighborRow,
                                                                        neighborCol)]) {
                    wqu.union(index(neighborRow, neighborCol),
                              index(row, col));
                    wquFull.union(index(neighborRow, neighborCol),
                                  index(row, col));
                }
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        ensureValidSite(row, col);

        return open[index(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        return (isOpen(row, col) && wquFull.find(index(row, col)) == wquFull
                .find(idUpperVirtualSite));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOpen;
    }

    // does the system percolate?
    public boolean percolates() {
        return wqu.find(idUpperVirtualSite) == wqu.find(idLowerVirtualSite);
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation p = new Percolation(1);
        System.out.println(p.percolates());
        p.open(1, 1);
        System.out.println(p.percolates());
    }
}
