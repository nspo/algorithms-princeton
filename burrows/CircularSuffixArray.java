import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private final Integer[] index;
    private final int length;
    private final String string;

    private class CircularSuffixOrder implements Comparator<Integer> {
        private int compareRec(int indexA, int indexB, int offset) {
            if (offset >= length) return 0;
            int adjA = (indexA + offset) % length;
            int adjB = (indexB + offset) % length;
            if (string.charAt(adjA) != string.charAt(adjB)) {
                return string.charAt(adjA) - string.charAt(adjB);
            }
            else {
                return compareRec(indexA, indexB, offset + 1);
            }
        }

        public int compare(Integer indexA, Integer indexB) {
            return compareRec(indexA, indexB, 0);
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException();

        string = s;
        length = s.length();
        index = new Integer[length];

        // sort circular suffix array
        for (int i = 0; i < length; ++i) {
            index[i] = i;
        }
        Arrays.sort(index, new CircularSuffixOrder());
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray csa = new CircularSuffixArray(s);
        StdOut.print("Length: ");
        StdOut.println(csa.length());
        StdOut.print("Index: ");
        for (int i = 0; i < s.length(); ++i) {
            StdOut.print(csa.index(i));
            StdOut.print(" ");
        }
    }

}
