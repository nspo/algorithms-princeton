import edu.princeton.cs.algs4.StdIn;

import java.util.Iterator;

public class Permutation {
    public static void main(String[] args) {
        int numRequested = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<>();

        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            rq.enqueue(s);
        }

        // as 0 <= numRequested <= rq.size() we can do:
        Iterator<String> it = rq.iterator();
        for (int i = 0; i < numRequested; ++i) {
            System.out.println(it.next());
        }
    }
}
