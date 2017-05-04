package com.abc.basic.algoritms.algs4.col;

import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ResizingArrayBag<Item> implements Iterable<Item> {
    private Item[] a;         // array of items
    private int n;            // number of elements on bag

    /**
     * Initializes an empty bag.
     */
    public ResizingArrayBag() {
        a = (Item[]) new Object[2];
        n = 0;
    }

    /**
     * Is this bag empty?
     * @return true if this bag is empty; false otherwise
     */
    public boolean isEmpty() {
        return n == 0;
    }

    /**
     * Returns the number of items in this bag.
     * @return the number of items in this bag
     */
    public int size() {
        return n;
    }

    // resize the underlying array holding the elements
    private void resize(int capacity) {
        assert capacity >= n;
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < n; i++) {
            temp[i] = a[i];
        }
        a = temp;
    }

    /**
     * Adds the item to this bag.
     * @param item the item to add to this bag
     */
    public void add(Item item) {
        if (n == a.length) {
            resize(2*a.length);    // double size of array if necessary
        }
        a[n++] = item;                            // add item
    }


    /**
     * Returns an iterator that iterates over the items in the bag in arbitrary order.
     * @return an iterator that iterates over the items in the bag in arbitrary order
     */
    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ArrayIterator implements Iterator<Item> {
        private int i = 0;
        public boolean hasNext()  {
            return i < n;
        }
        public void remove()      {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return a[i++];
        }
    }

    /**
     * Unit tests the {@code ResizingArrayBag} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        ResizingArrayBag<String> bag = new ResizingArrayBag<String>();
        bag.add("Hello");
        bag.add("World");
        bag.add("how");
        bag.add("are");
        bag.add("you");

        for (String s : bag)
            StdOut.println(s);
    }

}