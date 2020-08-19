import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {    // perform independent trials on an n-by-n grid
    private static final double CONFIDENCE_95 = 1.96;

    private final double[] percolationThreshold;
    private final int numTrials;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("invalid n or number of trials");
        }

        numTrials = trials;

        percolationThreshold = new double[trials];
        for (int i = 0; i < trials; ++i) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                int row = StdRandom.uniform(1, n + 1);
                int col = StdRandom.uniform(1, n + 1);
                percolation.open(row, col);
                // System.out.printf("Opened %d, %d. %d open now.\n", row, col,
                //                   percolation.numberOfOpenSites());
            }

            percolationThreshold[i] = ((double) percolation.numberOfOpenSites()) / (n * n);
        }

    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(percolationThreshold);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(percolationThreshold);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - CONFIDENCE_95 * stddev() / Math.sqrt(numTrials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + CONFIDENCE_95 * stddev() / Math.sqrt(numTrials);
    }

    // test client
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        PercolationStats ps = new PercolationStats(n, trials);
        System.out.printf("mean                     = %f\n", ps.mean());
        System.out.printf("stddev                   = %f\n", ps.stddev());
        System.out.printf("95%% confidence interval  = [%f, %f]\n",
                          ps.confidenceLo(),
                          ps.confidenceHi());
    }
}
