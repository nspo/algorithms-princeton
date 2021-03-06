import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int R = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] byRank = new int[R]; // byRank[i]=c means char c is at rank i
        int[] byChar = new int[R]; // byChar[c]=i means char c is at rank i
        for (int i = 0; i < R; ++i) {
            byRank[i] = i;
            byChar[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int rankC = byChar[c];
            BinaryStdOut.write(rankC, 8); // write current rank of char c
            // move c to front and shuffle others to the right
            for (int i = rankC; i > 0; --i) {
                int other = byRank[i - 1];
                byChar[other] = i;
                byRank[i] = other;
            }
            byChar[c] = 0;
            byRank[0] = c;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] byRank = new int[R]; // byRank[i]=c means char c is at rank i
        for (int i = 0; i < R; ++i) {
            byRank[i] = i;
        }

        while (!BinaryStdIn.isEmpty()) {
            int rankC = BinaryStdIn.readChar();
            int c = byRank[rankC];
            BinaryStdOut.write(c, 8); // write decoded character
            // move c to front and shuffle others to the right
            for (int i = rankC; i > 0; --i) {
                int other = byRank[i - 1];
                byRank[i] = other;
            }
            byRank[0] = c;
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Invalid number of arguments (must be 1; '-' to encode, '+' to decode");
        }
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
        else {
            throw new IllegalArgumentException(
                    "Invalid argument (must be '-' to encode, '+' to decode");
        }


    }

}
