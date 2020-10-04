import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private final int n;
    private final int[][] tiles;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        this.n = tiles.length;
        this.tiles = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; ++i) {
            for (int j = 0; j < tiles.length; ++j) {
                this.tiles[i][j] = tiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        String s = String.format("%d\n", n);
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                s = s.concat(String.format(" %d", tiles[row][col]));
            }
            s = s.concat("\n");
        }

        return s;
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int hamming = 0;
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                int val = tiles[row][col];
                if (val == 0) continue; // blank square is no tile
                if (targetCol(val) != col || targetRow(val) != row) {
                    ++hamming;
                }
            }
        }
        return hamming;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int sum = 0;
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                int val = tiles[row][col];
                if (val == 0) continue; // blank square is no tile
                int dCol = Math.abs(targetCol(val) - col);
                int dRow = Math.abs(targetRow(val) - row);
                sum += dCol + dRow;
            }
        }
        return sum;
    }

    // which row a value should be in
    private int targetRow(int val) {
        if (val == 0) {
            return n - 1;
        }
        return (val - 1) / n;
    }

    // which column a value should be in
    private int targetCol(int val) {
        if (val == 0) {
            return n - 1;
        }
        return (val - 1) % n;
    }

    private boolean indexValid(int row, int col) {
        if (row < 0 || row >= n) {
            return false;
        }

        if (col < 0 || col >= n) {
            return false;
        }

        return true;
    }

    // whether index is a tile
    private boolean isTile(int row, int col) {
        if (tiles[row][col] == 0) {
            return false;
        }

        return true;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }

        if (y == null) {
            return false;
        }

        if (y.getClass() != getClass()) {
            return false;
        }

        Board yBoard = (Board) y;
        if (n != yBoard.n) {
            return false;
        }

        // check all fields
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                int val = tiles[row][col];
                int yVal = yBoard.tiles[row][col];
                if (val != yVal) {
                    return false;
                }
            }
        }
        return true;
    }


    // all neighboring boards
    public Iterable<Board> neighbors() {
        // find blank element
        int blankRow = 0;
        int blankCol = 0;
        for (int row = 0; row < n; ++row) {
            for (int col = 0; col < n; ++col) {
                int val = tiles[row][col];
                if (val == 0) {
                    blankRow = row;
                    blankCol = col;
                    break;
                }
            }
        }

        Queue<Board> neighbors = new Queue<>();

        int[][] offsets = { { -1, 0 }, { +1, 0 }, { 0, -1 }, { 0, +1 } };
        for (int idx = 0; idx < 4; ++idx) {
            int[] offset = offsets[idx];
            int swapRow = offset[0] + blankRow;
            int swapCol = offset[1] + blankCol;
            if (indexValid(swapRow, swapCol)) {
                // save neighbor as it's valid

                // copy tiles array
                int[][] nbTiles = new int[tiles.length][tiles.length];
                for (int i = 0; i < tiles.length; ++i) {
                    for (int j = 0; j < tiles.length; ++j) {
                        nbTiles[i][j] = tiles[i][j];
                    }
                }

                nbTiles[blankRow][blankCol] = nbTiles[swapRow][swapCol];
                nbTiles[swapRow][swapCol] = 0;

                neighbors.enqueue(new Board(nbTiles));
            }
        }

        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        // pick two tiles to swap
        // we can assume n >= 2 but have to check we did not choose the blank element
        int row1 = 0, col1 = 0;
        if (!isTile(row1, col1)) {
            col1 = 1;
        }

        int row2 = 1, col2 = 0;
        if (!isTile(row2, col2)) {
            col2 = 1;
        }

        // copy tiles array
        int[][] twinTiles = new int[tiles.length][tiles.length];
        for (int i = 0; i < tiles.length; ++i) {
            for (int j = 0; j < tiles.length; ++j) {
                twinTiles[i][j] = tiles[i][j];
            }
        }

        // swap the two tiles
        int tmp = twinTiles[row1][col1];
        twinTiles[row1][col1] = twinTiles[row2][col2];
        twinTiles[row2][col2] = tmp;

        Board twin = new Board(twinTiles);

        return twin;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] bArr = { { 5, 2, 3 }, { 4, 1, 6 }, { 7, 8, 0 } };
        testBoard(bArr);

        int[][] bArr2 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 0, 8 } };
        testBoard(bArr2);
    }

    // helper for main
    private static void testBoard(int[][] bArr) {
        Board b = new Board(bArr);
        StdOut.println("Board: ");
        StdOut.print(b);
        StdOut.printf("Dimension: %d\n", b.dimension());
        Board bSame = new Board(bArr);
        StdOut.printf("b equals bSame: %b\n", b.equals(bSame));
        StdOut.printf("b is goal: %b\n", b.isGoal());
        StdOut.printf("Hamming: %d\nManhattan: %d\n", b.hamming(), b.manhattan());
        StdOut.printf("5 -- targetRow: %d, targetCol: %d\n", b.targetRow(5), b.targetCol(5));
        StdOut.printf("0 -- targetRow: %d, targetCol: %d\n\n", b.targetRow(0), b.targetCol(0));

        StdOut.println("Neighbors: ");
        for (Board nb : b.neighbors()) {
            StdOut.println(nb);
        }
        StdOut.println("A twin: ");
        StdOut.println(b.twin());
    }

}
