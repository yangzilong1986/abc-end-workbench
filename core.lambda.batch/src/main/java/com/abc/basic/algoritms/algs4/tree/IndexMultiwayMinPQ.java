package com.abc.basic.algoritms.algs4.tree;

import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IndexMultiwayMinPQ<Key> implements Iterable<Integer> {
	private final int dDimension;				//Dimension of the heap
	private int n;						//Number of keys currently in the queue
	private int nmax;					//Maximum number of items in the queue
	private int[] pqMultiwayBasedIndexing;					//Multiway heap
	//Inverse of pqMultiwayBasedIndexing :
	// qpIndexedForIndexKey[pqMultiwayBasedIndexing[i]] = pqMultiwayBasedIndexing[qpIndexedForIndexKey[i]] = i
	private int[] qpIndexedForIndexKey;
	private Key[] keys;					//keys[i] = priority of i
	private final Comparator<Key> comp; //Comparator over the keys
	
	
	public IndexMultiwayMinPQ(int N, int D) {
		if (N < 0) {
			throw new IllegalArgumentException("Maximum number of elements cannot be negative");
		}
		if (D < 2) {
			throw new IllegalArgumentException("Dimension should be 2 or over");
		}
		this.dDimension = D;
		nmax = N;
		pqMultiwayBasedIndexing = new int[nmax+D];
		qpIndexedForIndexKey = new int[nmax+D];
		keys = (Key[]) new Comparable[nmax+D];
		for (int i = 0; i < nmax+D; qpIndexedForIndexKey[i++] = -1);
		comp = new MyComparator();
	}
	
	public IndexMultiwayMinPQ(int N, Comparator<Key> C, int D) {
		if (N < 0) {
			throw new IllegalArgumentException("Maximum number of elements cannot be negative");
		}
		if (D < 2) {
			throw new IllegalArgumentException("Dimension should be 2 or over");
		}
		this.dDimension = D;
		nmax = N;
		pqMultiwayBasedIndexing = new int[nmax+D];
		qpIndexedForIndexKey = new int[nmax+D];
		keys = (Key[]) new Comparable[nmax+D];
		for (int i = 0; i < nmax+D; qpIndexedForIndexKey[i++] = -1);
		comp = C;
	}

	public boolean isEmpty() {
		return n == 0;
	}


	public boolean contains(int i) {
		if (i < 0 ||i >= nmax) {
			throw new IndexOutOfBoundsException();
		}
		return qpIndexedForIndexKey[i+dDimension] != -1;
	}

	public int size() {
		return n;
	}


	public void insert(int i, Key key) {
		if (i < 0 || i >= nmax) {
			throw new IndexOutOfBoundsException();
		}
		if (contains(i)) {
			throw new IllegalArgumentException("Index already there");
		}
		keys[i+dDimension] = key;
		pqMultiwayBasedIndexing[n+dDimension] = i;
		qpIndexedForIndexKey[i+dDimension] = n;
		swim(n++);
	}


	public int minIndex() {
		if (isEmpty()) {
			throw new NoSuchElementException("Priority queue is empty");
		}
		return pqMultiwayBasedIndexing[dDimension];
	}


	public Key minKey() {
		if (isEmpty()) {
			throw new NoSuchElementException("Priority queue is empty");
		}
		return keys[pqMultiwayBasedIndexing[dDimension]+dDimension];
	}


	public int delMin() {
		if (isEmpty()) {
			throw new NoSuchElementException("Priority queue is empty");
		}
		int min = pqMultiwayBasedIndexing[dDimension];
		exch(0, --n);
		sink(0);
		qpIndexedForIndexKey[min+dDimension] = -1;
		keys[pqMultiwayBasedIndexing[n+dDimension]+dDimension] = null;
		pqMultiwayBasedIndexing[n+dDimension] = -1;
		return min;
	}

	public Key keyOf(int i) {
		if (i < 0 || i >= nmax) {
			throw new IndexOutOfBoundsException();
		}
		if (! contains(i)) {
			throw new NoSuchElementException("Specified index is not in the queue");
		}
		return keys[i+dDimension];
	}

	public void changeKey(int i, Key key) {
		if (i < 0 || i >= nmax) {
			throw new IndexOutOfBoundsException();
		}
		if (! contains(i)) {
			throw new NoSuchElementException("Specified index is not in the queue");
		}
		Key tmp = keys[i+dDimension];
		keys[i+dDimension] = key;
		if (comp.compare(key, tmp) <= 0) {
			swim(qpIndexedForIndexKey[i+dDimension]);
		}else{
			sink(qpIndexedForIndexKey[i+dDimension]);
		}
	}

	public void decreaseKey(int i, Key key) {
		if (i < 0 || i >=nmax) {
			throw new IndexOutOfBoundsException();
		}
		if (! contains(i)) {
			throw new NoSuchElementException("Specified index is not in the queue");
		}
		if (comp.compare(keys[i+dDimension], key) <= 0) {
			throw new IllegalArgumentException("Calling with this argument would not decrease the Key");
		}
		keys[i+dDimension] = key;
		swim(qpIndexedForIndexKey[i+dDimension]);
	}


	public void increaseKey(int i, Key key) {
		if (i < 0 || i >=nmax) {
			throw new IndexOutOfBoundsException();
		}
		if (! contains(i)) {
			throw new NoSuchElementException("Specified index is not in the queue");
		}
		if (comp.compare(keys[i+dDimension], key) >= 0) {
			throw new IllegalArgumentException("Calling with this argument would not increase the Key");
		}
		keys[i+dDimension] = key;
		sink(qpIndexedForIndexKey[i+dDimension]);
	}

	public void delete(int i) {
		if (i < 0 || i >= nmax) {
			throw new IndexOutOfBoundsException();
		}
		if (! contains(i)) {
			throw new NoSuchElementException("Specified index is not in the queue");
		}
		int idx = qpIndexedForIndexKey[i+dDimension];
		exch(idx, --n);
		swim(idx);
		sink(idx);
		keys[i+dDimension] = null;
		qpIndexedForIndexKey[i+dDimension] = -1;
	}
	//Compares two keys
	private boolean greater(int i, int j) {
		return comp.compare(keys[pqMultiwayBasedIndexing[i+dDimension]+dDimension], keys[pqMultiwayBasedIndexing[j+dDimension]+dDimension]) > 0;
	}
	
	//Exchanges two keys
	private void exch(int x, int y) {
		int i = x+dDimension, j = y+dDimension;
		int swap = pqMultiwayBasedIndexing[i];
		pqMultiwayBasedIndexing[i] = pqMultiwayBasedIndexing[j];
		pqMultiwayBasedIndexing[j] = swap;
		qpIndexedForIndexKey[pqMultiwayBasedIndexing[i]+dDimension] = x;
		qpIndexedForIndexKey[pqMultiwayBasedIndexing[j]+dDimension] = y;
	}
	
	//Moves upward
	private void swim(int i) {
		if (i > 0 && greater((i-1)/dDimension, i)) {
			exch(i, (i-1)/dDimension);
			swim((i-1)/dDimension);
		}
	}
	
	//Moves downward
	private void sink(int i) {
		if (dDimension*i+1 >= n) return;
		int min = minChild(i);
		while (min < n && greater(i, min)) {
			exch(i, min);
			i = min;
			min = minChild(i);
		}
	}
	
	//Return the minimum child of i
	private int minChild(int i) {
		int loBound = dDimension*i+1, hiBound = dDimension*i+dDimension;
		int min = loBound;
		for (int cur = loBound; cur <= hiBound; cur++) {
			if (cur < n && greater(min, cur)) {
				min = cur;
			}
		}
		return min;
	}
	
	public Iterator<Integer> iterator() {
		return new MyIterator();
	}
	
	//Constructs an Iterator over the indices in linear time
	private class MyIterator implements Iterator<Integer> {
		IndexMultiwayMinPQ<Key> clone;
		
		public MyIterator() {
			clone = new IndexMultiwayMinPQ<Key>(nmax, comp, dDimension);
			for (int i = 0; i < n; i++) {
				clone.insert(pqMultiwayBasedIndexing[i+dDimension], keys[pqMultiwayBasedIndexing[i+dDimension]+dDimension]);
			}
		}

		public boolean hasNext() {
			return !clone.isEmpty();
		}
		
		public Integer next() {
            if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return clone.delMin();
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	//default Comparator
	private class MyComparator implements Comparator<Key> {
		@Override
		public int compare(Key key1, Key key2) {
			return ((Comparable<Key>) key1).compareTo(key2);
		}
	}

	public static void main(String[] args) {
		// insert a bunch of strings
		String[] strings = {"it", "was", "the", "best", "of"};

		IndexMultiwayMinPQ<String> heapBasedIndexing = new IndexMultiwayMinPQ<String>(strings.length,4);
		for (int i = 0; i < strings.length; i++) {
			heapBasedIndexing.insert(i, strings[i]);
		}

		// delete and print each key
		while (!heapBasedIndexing.isEmpty()) {
			int i = heapBasedIndexing.delMin();
			StdOut.println(i + " " + strings[i]);
		}
		StdOut.println();

		// reinsert the same strings
		for (int i = 0; i < strings.length; i++) {
			heapBasedIndexing.insert(i, strings[i]);
		}

		// print each key using the iterator
		for (int i : heapBasedIndexing) {
			StdOut.println(i + " " + strings[i]);
		}
		while (!heapBasedIndexing.isEmpty()) {
			heapBasedIndexing.delMin();
		}

	}
}