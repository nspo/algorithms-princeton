import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < s.length(); ++i) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < s.length(); ++i) {
            BinaryStdOut.write(s.charAt((csa.index(i) + s.length() - 1) % csa.length()), 8);
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt(); // index of original substring in sorted suffix array
        String s = BinaryStdIn.readString(); // last column of sorted cyclic suffix array

        // count occurrences of each char
        int[] count = new int[R + 1];
        for (int i = 0; i < s.length(); ++i) {
            ++count[s.charAt(i) + 1];
        }
        // accumulate
        for (int i = 1; i < count.length; ++i) {
            count[i] += count[i - 1];
        }

        // save sorted chars and determine next index for each
        int[] next = new int[s.length()];
        char[] sorted = new char[s.length()];

        for (int i = 0; i < s.length(); ++i) {
            int sortedI = count[s.charAt(i)]++;
            sorted[sortedI] = s.charAt(i);
            next[sortedI] = i;
        }

        // write result
        int index = first;
        for (int i = 0; i < sorted.length; ++i) {
            BinaryStdOut.write(sorted[index]);
            index = next[index];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Invalid number of arguments (must be 1; '-' to encode, '+' to decode");
        }
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid argument (must be '-' to encode, '+' to decode");
        }
    }

}
