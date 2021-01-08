import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class WordNet {
    private final HashMap<Integer, ArrayList<String>> synsetsById;
    private final HashMap<String, ArrayList<Integer>> synsetsByStr;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsetsFile, String hypernymsFile) {
        if (synsetsFile == null || hypernymsFile == null) throw new IllegalArgumentException();

        synsetsById = new HashMap<>();
        synsetsByStr = new HashMap<>();
        In in = new In(synsetsFile);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            synsetsById.put(id, new ArrayList<>());
            for (String s : fields[1].split(" ")) {
                synsetsById.get(id).add(s);

                if (!synsetsByStr.containsKey(s)) {
                    synsetsByStr.put(s, new ArrayList<>());
                }
                synsetsByStr.get(s).add(id);
            }

        }

        // allow for moving hypernyms out as member variable if necessary later
        // consider creating digraph directly
        TreeMap<Integer, ArrayList<Integer>> hypernyms = new TreeMap<>();
        in = new In(hypernymsFile);
        while (in.hasNextLine()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            hypernyms.put(id, new ArrayList<>());
            for (int i = 1; i < fields.length; ++i) {
                int hypId = Integer.parseInt(fields[i]);
                hypernyms.get(id).add(hypId);
            }
        }

        Digraph g = new Digraph(hypernyms.size());
        for (int from : hypernyms.keySet()) {
            for (int to : hypernyms.get(from)) {
                g.addEdge(from, to);
            }
        }
        if ((new DirectedCycle(g)).hasCycle()) {
            // not a DAG
            throw new IllegalArgumentException();
        }

        this.sap = new SAP(g);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetsByStr.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) throw new IllegalArgumentException();
        return synsetsByStr.get(word) != null;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        ArrayList<Integer> idsA = synsetsByStr.get(nounA);
        ArrayList<Integer> idsB = synsetsByStr.get(nounB);
        return sap.length(idsA, idsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) throw new IllegalArgumentException();
        ArrayList<Integer> idsA = synsetsByStr.get(nounA);
        ArrayList<Integer> idsB = synsetsByStr.get(nounB);
        int idAncestor = sap.ancestor(idsA, idsB);
        if (idAncestor == -1) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder();
        for (String s : synsetsById.get(idAncestor)) {
            sb.append(s).append(" ");
        }
        sb.deleteCharAt(sb.length() - 1); // remove last space
        return sb.toString();
    }

    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        StdOut.println(wn.isNoun("AIDS"));
        StdOut.println(wn.isNoun("accommodation"));
        StdOut.println(wn.isNoun("Java"));

        StdOut.println(wn.distance("AIDS", "Addison's_disease"));
        StdOut.println(wn.distance("transgression", "resistance"));
        StdOut.println(wn.sap("Ratibida", "Slavic"));
    }

}
