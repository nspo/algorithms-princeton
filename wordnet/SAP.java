import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class SAP {
    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.digraph = new Digraph(G); // deep copy
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        // call more general function
        ArrayList<Integer> vList = new ArrayList<>();
        vList.add(v);
        ArrayList<Integer> wList = new ArrayList<>();
        wList.add(w);
        return length(vList, wList);
    }

    private void stepsDfs(HashMap<Integer, Integer> mapSteps, int node, int numStepsUntil) {
        if (node < 0 || node >= digraph.V()) {
            throw new IllegalArgumentException();
        }

        if (mapSteps.containsKey(node) && mapSteps.get(node) <= numStepsUntil) {
            return;
        }
        mapSteps.put(node, numStepsUntil);

        for (int other : digraph.adj(node)) {
            int numStepsToOther = numStepsUntil + 1;
            if (!mapSteps.containsKey(other) || mapSteps.get(other) > numStepsToOther) {
                // only skip if the earlier path was longer
                stepsDfs(mapSteps, other, numStepsToOther);
            }
        }
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        // call more general function
        ArrayList<Integer> vList = new ArrayList<>();
        vList.add(v);
        ArrayList<Integer> wList = new ArrayList<>();
        wList.add(w);
        return ancestor(vList, wList);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();

        // map: node id -> number of steps needed
        HashMap<Integer, Integer> vMapSteps = new HashMap<>();
        for (Integer node : v) {
            if (node == null) throw new IllegalArgumentException();
            stepsDfs(vMapSteps, node, 0);
        }

        HashMap<Integer, Integer> wMapSteps = new HashMap<>();
        for (Integer node : w) {
            if (node == null) throw new IllegalArgumentException();
            stepsDfs(wMapSteps, node, 0);
        }

        int minLength = -1;
        for (int nodeFromV : vMapSteps.keySet()) {
            if (wMapSteps.containsKey(nodeFromV)) {
                int length = vMapSteps.get(nodeFromV) + wMapSteps.get(nodeFromV);
                if (minLength == -1 || length < minLength) {
                    minLength = length;
                }
            }
        }

        return minLength;
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) throw new IllegalArgumentException();

        // map: node id -> number of steps needed
        HashMap<Integer, Integer> vMapSteps = new HashMap<>();
        for (Integer node : v) {
            if (node == null) throw new IllegalArgumentException();
            stepsDfs(vMapSteps, node, 0);
        }

        HashMap<Integer, Integer> wMapSteps = new HashMap<>();
        for (Integer node : w) {
            if (node == null) throw new IllegalArgumentException();
            stepsDfs(wMapSteps, node, 0);
        }

        int minLength = -1;
        int minLengthNode = -1;
        for (int nodeFromV : vMapSteps.keySet()) {
            if (wMapSteps.containsKey(nodeFromV)) {
                int length = vMapSteps.get(nodeFromV) + wMapSteps.get(nodeFromV);
                if (minLength == -1 || length < minLength) {
                    minLength = length;
                    minLengthNode = nodeFromV;
                }
            }
        }

        return minLengthNode;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
