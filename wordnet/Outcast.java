import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        if (wordnet == null) throw new IllegalArgumentException();
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns.length == 0) throw new IllegalArgumentException();

        String maxOutcast = nouns[0];
        int maxOutcastDist = 0;

        for (int i = 0; i < nouns.length; ++i) {
            int currOutcastDist = 0;
            // go through all other nouns
            for (int jOffset = 0; jOffset + 1 < nouns.length; ++jOffset) {
                int j = (i + 1 + jOffset) % nouns.length;
                currOutcastDist += wordnet.distance(nouns[i], nouns[j]);
            }
            if (currOutcastDist > maxOutcastDist) {
                // new max outcast found
                maxOutcastDist = currOutcastDist;
                maxOutcast = nouns[i];
            }
        }

        return maxOutcast;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
