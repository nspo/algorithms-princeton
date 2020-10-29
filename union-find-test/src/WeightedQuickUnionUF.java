public class WeightedQuickUnionUF {
    private int[] id;
    private int[] sz;

    public WeightedQuickUnionUF(int N) {
        id = new int[N];
        sz = new int[N];
        for (int i = 0; i < N; ++i) {
            id[i] = i;
            sz[i] = 1;
        }
    }

    private int findRoot(int p) {
        while (id[p] != p) {
            id[p] = id[id[p]];
            p = id[p];
        }
        return p;
    }

    public void union(int p, int q) {
        int rp = findRoot(p);
        int rq = findRoot(q);
        if (rp == rq) return;

        if (sz[rp] < sz[rq]) {
            id[rp] = rq;
            sz[rq] += sz[rp];
        } else {
            id[rq] = rp;
            sz[rp] += sz[rq];
        }
    }

    public boolean connected(int p, int q) {
        return findRoot(p) == findRoot(q);
    }
}
