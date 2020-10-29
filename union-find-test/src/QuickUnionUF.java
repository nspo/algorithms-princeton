public class QuickUnionUF {
    private int[] id;

    public QuickUnionUF(int N) {
        id = new int[N];
        for (int i = 0; i < N; ++i) {
            id[i] = i;
        }
    }

    private int findRoot(int p) {
        while (id[p] != p) {
            p = id[p];
        }
        return p;
    }

    public void union(int p, int q) {
        id[findRoot(p)] = findRoot(q);
    }

    public boolean connected(int p, int q) {
//        ArrayList<Integer> common = new ArrayList<Integer>(0);
        return findRoot(p) == findRoot(q);
    }
}
