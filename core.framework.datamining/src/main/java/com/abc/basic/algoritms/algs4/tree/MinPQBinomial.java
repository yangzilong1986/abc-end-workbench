package com.abc.basic.algoritms.algs4.tree;

import java.util.Iterator;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * 二项堆（Binomial Heap）是一种堆结构。与二叉堆（Binary Heap）相比，其优势是可以快速合并两个堆，
 * 因此它属于可合并堆（Mergeable Heap）数据结构的一种。
 */
public class MinPQBinomial<Key> implements Iterable<Key> {
    private Node head;                    //head of the list of roots
    private final Comparator<Key> comp;    //Comparator over the keys

    //Represents a Node of a Binomial Tree
    private class Node {
        Key key;                        //Key contained by the Node
        int order;                        //The order of the Binomial Tree rooted by this Node
        Node child, sibling;            //child and sibling of this Node
    }

    public MinPQBinomial() {
        comp = new MyComparator();
    }

    public MinPQBinomial(Comparator<Key> C) {
        comp = C;
    }

    public MinPQBinomial(Key[] a) {
        comp = new MyComparator();
        for (Key k : a) {
            insert(k);
        }
    }

    public MinPQBinomial(Comparator<Key> C, Key[] a) {
        comp = C;
        for (Key k : a) {
            insert(k);
        }
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        int result = 0, tmp;
        for (Node node = head; node != null; node = node.sibling) {
            if (node.order > 30) {
                throw new ArithmeticException("The number of elements cannot be evaluated, " +
                        "but the priority queue is still valid.");
            }
            tmp = 1 << node.order;
            result |= tmp;
        }
        return result;
    }

    /**
     * Puts a Key in the heap
     * Worst case is O(log(n))
     *
     * @param key a Key
     */
    public void insert(Key key) {
        Node x = new Node();
        x.key = key;
        x.order = 0;
        MinPQBinomial<Key> H = new MinPQBinomial<Key>(); //The Comparator oh the H heap is not used
        H.head = x;
        this.head = this.union(H).head;
    }

    /**
     * Get the minimum key currently in the queue
     * Worst case is O(log(n))
     *
     * @return the minimum key currently in the priority queue
     * @throws java.util.NoSuchElementException if the priority queue is empty
     */
    public Key minKey() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        Node min = head;
        Node current = head;
        while (current.sibling != null) {
            min = (greater(min.key, current.sibling.key)) ? current : min;
            current = current.sibling;
        }
        return min.key;
    }

    /**
     * Deletes the minimum key
     * Worst case is O(log(n))
     *
     * @return the minimum key
     * @throws java.util.NoSuchElementException if the priority queue is empty
     */
    public Key delMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        Node min = eraseMin();
        Node x = (min.child == null) ? min : min.child;
        if (min.child != null) {
            min.child = null;
            Node prevx = null, nextx = x.sibling;
            while (nextx != null) {
                x.sibling = prevx;
                prevx = x;
                x = nextx;
                nextx = nextx.sibling;
            }
            x.sibling = prevx;
            MinPQBinomial<Key> H = new MinPQBinomial<Key>();
            H.head = x;
            head = union(H).head;
        }
        return min.key;
    }

    public MinPQBinomial<Key> union(MinPQBinomial<Key> heap) {
        if (heap == null) throw new IllegalArgumentException("Cannot merge a Binomial Heap with null");
        this.head = merge(new Node(), this.head, heap.head).sibling;
        Node x = this.head;
        Node prevx = null, nextx = x.sibling;
        while (nextx != null) {
            if (x.order < nextx.order ||
                    (nextx.sibling != null && nextx.sibling.order == x.order)) {
                prevx = x;
                x = nextx;
            } else if (greater(nextx.key, x.key)) {
                x.sibling = nextx.sibling;
                link(nextx, x);
            } else {
                if (prevx == null) {
                    this.head = nextx;
                } else {
                    prevx.sibling = nextx;
                }
                link(x, nextx);
                x = nextx;
            }
            nextx = x.sibling;
        }
        return this;
    }

    /*************************************************
     * General helper functions
     ************************************************/

    //Compares two keys
    private boolean greater(Key n, Key m) {
        if (n == null) {
            return false;
        }
        if (m == null) {
            return true;
        }
        return comp.compare(n, m) > 0;
    }

    //Assuming root1 holds a greater key than root2, root2 becomes the new root
    private void link(Node root1, Node root2) {
        root1.sibling = root2.child;
        root2.child = root1;
        root2.order++;
    }

    //Deletes and return the node containing the minimum key
    private Node eraseMin() {
        Node min = head;
        Node previous = null;
        Node current = head;
        while (current.sibling != null) {
            if (greater(min.key, current.sibling.key)) {
                previous = current;
                min = current.sibling;
            }
            current = current.sibling;
        }
        previous.sibling = min.sibling;
        if (min == head) head = min.sibling;
        return min;
    }

    /**************************************************
     * Functions for inserting a key in the heap
     *************************************************/

    //Merges two root lists into one, there can be up to 2 Binomial Trees of same order
    private Node merge(Node h, Node x, Node y) {
        if (x == null && y == null) {
            return h;
        } else if (x == null) {
            h.sibling = merge(y, null, y.sibling);
        } else if (y == null) {
            h.sibling = merge(x, x.sibling, null);
        } else if (x.order < y.order) {
            h.sibling = merge(x, x.sibling, y);
        } else {
            h.sibling = merge(y, x, y.sibling);
        }
        return h;
    }

    /******************************************************************
     * Iterator
     *****************************************************************/

    /**
     * Gets an Iterator over the keys in the priority queue in ascending order
     * The Iterator does not implement the remove() method
     * iterator() : Worst case is O(n)
     * next() : 	Worst case is O(log(n))
     * hasNext() : 	Worst case is O(1)
     *
     * @return an Iterator over the keys in the priority queue in ascending order
     */
    public Iterator<Key> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<Key> {
        MinPQBinomial<Key> data;

        //Constructor clones recursively the elements in the queue
        //It takes linear time
        public MyIterator() {
            data = new MinPQBinomial<Key>(comp);
            data.head = clone(head, false, false, null);
        }

        private Node clone(Node x, boolean isParent, boolean isChild, Node parent) {
            if (x == null) return null;
            Node node = new Node();
            node.key = x.key;
            node.sibling = clone(x.sibling, false, false, parent);
            node.child = clone(x.child, false, true, node);
            return node;
        }

        public boolean hasNext() {
            return !data.isEmpty();
        }

        public Key next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return data.delMin();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /***************************
     * Comparator
     **************************/

    //default Comparator
    private class MyComparator implements Comparator<Key> {
        @Override
        public int compare(Key key1, Key key2) {
            return ((Comparable<Key>) key1).compareTo(key2);
        }
    }

}