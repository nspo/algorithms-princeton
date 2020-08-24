import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int n = 0;
    private Item[] data;

    // construct an empty randomized queue
    public RandomizedQueue() {
        data = (Item[]) new Object[1];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    private void resize(int newSize) {
        if (newSize < n) {
            throw new IllegalArgumentException("Cannot resize as data array is too large");
        }

        Item[] newData = (Item[]) new Object[newSize];
        for (int i = 0; i < n; ++i) {
            newData[i] = data[i];
        }

        data = newData;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (n == data.length) {
            // double size if array is full
            resize(2 * n);
        }

        data[n] = item;
        ++n;
    }

    // remove and return a random item
    public Item dequeue() {
        if (n == 0) {
            throw new NoSuchElementException();
        }
        // choose element to dequeue
        int idx = StdRandom.uniform(0, n);
        Item elem = data[idx];
        // place last element at elem's position if possible
        if (n > 1) {
            data[idx] = data[n - 1];
            data[n - 1] = null;
        }
        else {
            // just release reference
            data[idx] = null;
        }
        --n;

        if (n < data.length / 4) {
            // resize to half of array length
            resize(data.length / 2);
        }

        return elem;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (n == 0) {
            throw new NoSuchElementException();
        }

        return data[StdRandom.uniform(0, n)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private final int[] order;
        private int currentIndex;

        public RandomizedQueueIterator() {
            // create order
            order = new int[n];
            for (int i = 0; i < n; ++i) {
                order[i] = i;
            }
            StdRandom.shuffle(order);
        }

        public boolean hasNext() {
            return currentIndex < order.length;
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return data[order[currentIndex++]];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        System.out.println("Creating RandomizedQueue with 10 integers 0..9");
        RandomizedQueue<Integer> rq = new RandomizedQueue<>();
        for (int i = 0; i < 10; ++i) {
            rq.enqueue(i);
        }

        System.out.println("Calling dequeue twice");
        System.out.println(rq.dequeue());
        System.out.println(rq.dequeue());
        System.out.println("Sampling once");
        System.out.println(rq.sample());

        System.out.printf("isEmpty: %s\n", Boolean.toString(rq.isEmpty()));
        System.out.printf("size: %d\n", rq.size());

        System.out.println("Printing five elements with iterator");

        Iterator<Integer> it = rq.iterator();
        for (int i = 0; i < 5; ++i) {
            System.out.println(it.next());
        }

        System.out.println("Trying to remove elem");
        try {
            it.remove();
        }
        catch (UnsupportedOperationException e) {
            System.out.println("Exception");
        }

        System.out.println("Print all with iterator");
        for (int i : rq) {
            System.out.println(i);
        }
    }

}
