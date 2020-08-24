import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node first = null;
    private Node last = null;
    private int count = 0;

    private class Node {
        Item data;
        Node next;
        Node prev;
    }

    // construct an empty deque
    public Deque() {

    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null;
    }

    // return the number of items on the deque
    public int size() {
        return count;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node newNode = new Node();
        newNode.data = item;
        newNode.prev = null;

        if (count == 0) {
            // empty deque
            newNode.next = null;
            first = newNode;
            last = newNode;
        }
        else {
            // prepend newNode
            first.prev = newNode;
            newNode.next = first;
            first = newNode;
        }

        ++count;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        Node newNode = new Node();
        newNode.data = item;
        newNode.next = null;

        if (count == 0) {
            // empty deque
            newNode.prev = null;
            first = newNode;
            last = newNode;
        }
        else {
            // append newNode
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }
        ++count;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (count == 0) {
            throw new NoSuchElementException();
        }

        Item data = first.data;
        if (count == 1) {
            // was last element
            first = null;
            last = null;
        }
        else {
            first.next.prev = null;
            first = first.next;
        }

        --count;
        return data;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (count == 0) {
            throw new NoSuchElementException();
        }

        Item data = last.data;
        if (count == 1) {
            // was last element
            first = null;
            last = null;
        }
        else {
            last.prev.next = null;
            last = last.prev;
        }

        --count;
        return data;
    }

    private class DequeueIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (current == null) {
                throw new NoSuchElementException();
            }

            Item data = current.data;
            current = current.next;
            return data;
        }

    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new DequeueIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deq = new Deque<>();
        deq.addLast("Liebe");
        deq.addLast("Glück");
        deq.addFirst("Geburt");
        for (String s : deq) {
            StdOut.println(s);
        }

        Iterator<String> it = deq.iterator();
        try {
            it.remove();
        }
        catch (UnsupportedOperationException e) {
            StdOut.println("exception");
        }

        deq.removeLast();
        deq.removeFirst();
        StdOut.println(deq.size());
        StdOut.println(deq.isEmpty());
        deq.removeFirst();
        StdOut.println(deq.size());
        StdOut.println(deq.isEmpty());
    }

}
