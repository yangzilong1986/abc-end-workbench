package com.abc.basic.algoritms.algs4.col;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class Stack<Item> implements Iterable<Item> {
    private Node<Item> last;     // top of stack
    private int n;   // size of the stack

    private static class Node<Item> {
        private Item item;
        private Node<Item> next;
    }
    public Stack() {
        last = null;
        n = 0;
    }

    public boolean isEmpty() {
        return last == null;
    }

    public int size() {
        return n;
    }

    /**
     * Adds the item to this stack.
     *
     * @param  item the item to add
     */
    public void push(Item item) {
        Node<Item> oldfirst = last;
        last = new Node<Item>();
        last.item = item;
        last.next = oldfirst;
        n++;
    }


     public Item pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack underflow");
        }
        Item item = last.item;// save item to return
         last = last.next; // delete first node
        n--;
        return item; // return the saved item
    }

    public Item peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack underflow");
        }
        return last.item;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (Item item : this) {
            s.append(item);
            s.append(' ');
        }
        return s.toString();
    }
       

    public Iterator<Item> iterator() {
        return new ListIterator<Item>(last);
    }

    // an iterator, doesn't implement remove() since it's optional
    private class ListIterator<Item> implements Iterator<Item> {
        private Node<Item> current;

        public ListIterator(Node<Item> first) {
            current = first;
        }

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Item item = current.item;
            current = current.next; 
            return item;
        }
    }


}