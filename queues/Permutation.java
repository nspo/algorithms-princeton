import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;

public class Permutation {
    public static void main(String[] args) {
        int numRequested = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<>();

        int numRead = 0;
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            ++numRead;
            if (rq.size() < numRequested) {
                // always add to queue if it does not have enough elements yet
                rq.enqueue(s);
            }
            else {
                // only add new element depending on random result
                // this leads to all elements having a probability of
                // numRequested/numRead_total of being in the queue in the end
                if (StdRandom.uniform(0, numRead) < numRequested) {
                    rq.dequeue();
                    rq.enqueue(s);
                }
            }

        }

        // as 0 <= numRequested <= rq.size() we can do:
        Iterator<String> it = rq.iterator();
        for (int i = 0; i < numRequested; ++i) {
            System.out.println(it.next());
        }
    }
}
