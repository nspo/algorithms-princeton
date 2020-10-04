import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;
import java.util.List;

public class Solver {
    private final SearchNode solutionNode;
    private final boolean solvable;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }

        // insert first node with initial board state into the main queue
        MinPQ<SearchNode> mainQueue = new MinPQ<>();
        SearchNode mainNode = new SearchNode(initial, 0, null);
        mainQueue.insert(mainNode);

        // do the same for a twin to prevent endless loops for unsolvable board
        MinPQ<SearchNode> twinQueue = new MinPQ<>();
        SearchNode twinNode = new SearchNode(initial.twin(), 0, null);
        twinQueue.insert(twinNode);

        while (!mainNode.getBoard().isGoal() && !twinNode.getBoard().isGoal()) {
            // neigher main nor twin board have been solved yet
            mainNode = mainQueue.delMin();
            addNeighborsToQueue(mainNode, mainQueue);

            twinNode = twinQueue.delMin();
            addNeighborsToQueue(twinNode, twinQueue);
        }

        // either main or twin board was solved
        if (mainNode.getBoard().isGoal()) {
            // great success!
            solvable = true;
            solutionNode = mainNode;
        }
        else {
            // twin board was solved -> main board is unsolvable
            solvable = false;
            solutionNode = null;
        }

    }

    private void addNeighborsToQueue(SearchNode node, MinPQ<SearchNode> queue) {
        for (Board neighborBoard : node.getBoard().neighbors()) {
            if (node.getPrevNode() == null || !neighborBoard
                    .equals(node.getPrevNode().getBoard())) {
                // the generated neighbor is not the previous board (optimization)
                SearchNode neighborNode = new SearchNode(neighborBoard,
                                                         node.getNumMoves() + 1,
                                                         node);
                queue.insert(neighborNode);
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (solvable)
            return solutionNode.getNumMoves();
        else
            return -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!solvable)
            return null;

        List<Board> nodeList = new LinkedList<>();
        nodeList.add(0, solutionNode.getBoard());
        SearchNode iterNode = solutionNode;
        while (iterNode.prevNode != null) {
            iterNode = iterNode.prevNode;
            nodeList.add(0, iterNode.getBoard());
        }
        return nodeList;
    }

    // test client (see below)
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int numMoves;
        private final SearchNode prevNode;
        private final int score;

        public SearchNode(Board board, int numMoves, SearchNode prevNode) {
            this.board = board;
            this.numMoves = numMoves;
            this.prevNode = prevNode;
            this.score = this.numMoves + this.board.manhattan();
        }

        public int compareTo(SearchNode other) {
            return this.score - other.score;
        }

        public int getNumMoves() {
            return numMoves;
        }

        public SearchNode getPrevNode() {
            return prevNode;
        }

        public Board getBoard() {
            return board;
        }
    }

}
