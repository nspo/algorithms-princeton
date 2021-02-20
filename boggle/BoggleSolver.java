import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class BoggleSolver {
    private static final int R = 26; // only uppercase letters

    // contains position of a field on the board
    private static class Position {
        private final int row;
        private final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private ArrayList<ArrayList<Position>> neighbors; // neighbors at index row*board.cols+col

    // board size for which computed neighbors are valid
    private int precomputedBoardRows = 0;
    private int precomputedBoardCols = 0;

    private int runCounter = 0; // times that the dictionary was searched with a board

    private static class Node {
        private String word = null; // to avoid having to build word along the way
        private int lastUsedInRun = -1; // to mark whether word was used in current run
        private boolean anyChildren = false; // whether this node has at least one child
        private final Node[] next = new Node[R];
    }

    private final Node root;


    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        root = new Node();
        for (String s : dictionary) {
            if (s.length() < 3) continue; // minimum word length
            if (s.contains("Q") && !s.contains("QU")) continue; // invalid in boggle

            Node node = root;
            for (int j = 0; j < s.length(); ++j) {
                char c = s.charAt(j);
                int index = c - 'A';
                if (node.next[index] == null) {
                    node.next[index] = new Node();
                }
                node.anyChildren = true;
                node = node.next[index];
            }
            node.word = s;
        }
    }

    // precompute all neighbor fields per field in board
    private void precomputeNeighbors(BoggleBoard board) {
        if (precomputedBoardRows == board.rows() && precomputedBoardCols == board.cols()) {
            // already computed neighbors for the current board size
            return;
        }
        precomputedBoardRows = board.rows();
        precomputedBoardCols = board.cols();

        // possible offsets in general
        final int[] OFFSETS_ROW = { -1, -1, -1, 0, 0, +1, +1, +1 };
        final int[] OFFSETS_COL = { -1, 0, +1, -1, +1, -1, 0, +1 };

        neighbors = new ArrayList<ArrayList<Position>>();
        neighbors.ensureCapacity(board.rows() * board.cols());
        for (int row = 0; row < board.rows(); ++row) {
            for (int col = 0; col < board.cols(); ++col) {
                neighbors.add(new ArrayList<Position>());
                int index = row * board.cols() + col;
                for (int i = 0; i < OFFSETS_ROW.length; ++i) {
                    int newRow = row + OFFSETS_ROW[i];
                    int newCol = col + OFFSETS_COL[i];
                    if (isField(newRow, newCol, board)) {
                        neighbors.get(index).add(new Position(newRow, newCol));
                    }
                }
            }
        }
    }

    // get neighbors to row/col for current board
    private Iterable<Position> getNeighbors(int row, int col) {
        return neighbors.get(row * precomputedBoardCols + col);
    }

    // is row/col a valid field location on board?
    private static boolean isField(int row, int col, BoggleBoard board) {
        return row >= 0 && row < board.rows() && col >= 0 && col < board.cols();
    }

    // collect all valid words below node
    private void collectWords(Node node, int row, int col, boolean[][] visited, BoggleBoard board,
                              ArrayList<String> result) {
        if (node == null) return;
        if (visited[row][col]) return;
        visited[row][col] = true;
        if (node.word != null && node.lastUsedInRun != runCounter) {
            // could also depend on trie instead of checking duplicates manually but that
            // would probably be slower
            node.lastUsedInRun = runCounter;
            result.add(node.word);
        }
        if (node.anyChildren) {
            // only look at neighbors if current node has children at all
            for (Position neighbor : getNeighbors(row, col)) {
                char c = board.getLetter(neighbor.row, neighbor.col);
                int index = c - 'A';
                if (c == 'Q') {
                    // special case - treat as QU
                    if (node.next[index] != null) {
                        char c2 = 'U';
                        int index2 = c2 - 'A';
                        collectWords(node.next[index].next[index2], neighbor.row, neighbor.col,
                                     visited,
                                     board, result);
                    }
                }
                else {
                    collectWords(node.next[index], neighbor.row, neighbor.col, visited, board,
                                 result);
                }
            }
        }
        visited[row][col] = false; // mark as available again
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        precomputeNeighbors(board);
        ArrayList<String> result = new ArrayList<>();
        boolean[][] visited = new boolean[board.rows()][board.cols()];
        for (int row = 0; row < board.rows(); ++row) {
            for (int col = 0; col < board.cols(); ++col) {
                // visited array is always reset when collectWords returns
                char c = board.getLetter(row, col);
                int index = c - 'A';
                if (c == 'Q') {
                    // special case - treat as QU
                    if (root.next[index] != null) {
                        char c2 = 'U';
                        int index2 = c2 - 'A';
                        collectWords(root.next[index].next[index2], row, col, visited, board,
                                     result);
                    }
                }
                else {
                    collectWords(root.next[index], row, col, visited, board, result);
                }
            }
        }
        runCounter += 1;
        return result;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null || word.length() < 3) return 0;
        // check whether word is in dictionary
        Node node = root;
        for (int i = 0; i < word.length(); ++i) {
            if (node == null) return 0;
            char c = word.charAt(i);
            int index = c - 'A';
            node = node.next[index];
        }
        if (node == null || node.word == null) {
            return 0;
        }
        switch (word.length()) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                // note that word.length() < 3 was already handled above
                return 11;
        }
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
