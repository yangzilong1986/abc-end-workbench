package com.abc.basic.algoritms.algs4.tree;

import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.Iterator;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class MultiwayMinPQ<Key> implements Iterable<Key> {
	private final int d; 				//Dimension of the heap
	private int n;						//Number of keys currently in the heap
	private int order;					//Number of levels of the tree
	private Key[] keys;					//Array of keys
	private final Comparator<Key> comp;	//Comparator over the keys
	
	public MultiwayMinPQ(int d) {
		if (d < 2) {
			throw new IllegalArgumentException("Dimension should be 2 or over");
		}
		this.d = d;
		order = 1;
		keys = (Key[]) new Comparable[d << 1];
		comp = new MyComparator();
	}
	
	public MultiwayMinPQ(Comparator<Key> comparator, int d) {
		if (d < 2) {
			throw new IllegalArgumentException("Dimension should be 2 or over");
		}
		this.d = d;
		order = 1;
		keys = (Key[]) new Comparable[d << 1];
		comp = comparator;
	}
	
	public MultiwayMinPQ(Key[] a, int d) {
		if (d < 2) {
			throw new IllegalArgumentException("Dimension should be 2 or over");
		}
		this.d = d;
		order = 1;
		keys = (Key[]) new Comparable[d << 1];
		comp = new MyComparator();
		for (Key key : a){
			insert(key);
		}
	}
	
	public MultiwayMinPQ(Comparator<Key> comparator, Key[] a, int d) {
		if (d < 2) {
			throw new IllegalArgumentException("Dimension should be 2 or over");
		}
		this.d = d;
		order = 1;
		keys = (Key[]) new Comparable[d << 1];
		comp = comparator;
		for (Key key : a) {
			insert(key);
		}
	}

	public boolean isEmpty() {
		return n == 0;
	}

	public int size() {
		return n;
	}

	public void insert(Key key) {
		keys[n+d] = key;
		swim(n++);
		if (n == keys.length-d) {
			resize(getN(order+1)+d);
			order++;
		}
	}

	public Key minKey() {
		if (isEmpty()) {
			throw new NoSuchElementException("Priority queue is empty");
		}
		return keys[d];
	}

	public Key delMin() {
		if (isEmpty()) {
			throw new NoSuchElementException("Priority queue is empty");
		}
		exch(0, --n);
		sink(0);
		Key min = keys[n+d];
		keys[n+d] = null;
		int number = getN(order-2);
		if(order > 1 && n == number)  {
			resize(number+(int)Math.pow(d, order-1)+d);
			order--;
		}
		return min;
	}
	

	//Compares two keys
	private boolean greater(int x, int y) {
		int i = x+d, j = y+d;
		if (keys[i] == null)
			return false;
		if (keys[j] == null)
			return true;
		return comp.compare(keys[i], keys[j]) > 0;
	}
	
	//Exchanges the position of two keys
	private void exch(int x, int y) {
		int i = x+d, j = y+d;
		Key swap = keys[i];
		keys[i] = keys[j];
		keys[j] = swap;
	}
	
	//Gets the maximum number of keys in the heap, given the number of levels of the tree
	private int getN(int order) {
		return (1-((int)Math.pow(d, order+1)))/(1-d);
	}
	
	/***************************
	 * Functions for moving upward or downward
	 **************************/
	
	//Moves upward
	private void swim(int i) {
		if (i > 0 && greater((i-1)/d, i)) {
			exch(i, (i-1)/d);
			swim((i-1)/d);
		}
	}
	
	//Moves downward
	private void sink(int i) {
		int child = d*i+1;
		if (child >= n) return;
		int min = minChild(i);
		while (min < n && greater(i, min)) {
			exch(i, min);
			i = min;
			min = minChild(i);
		}
	}
	
	private int minChild(int i) {
		int loBound = d*i+1, hiBound = d*i+d;
		int min = loBound;
		for (int cur = loBound; cur <= hiBound; cur++) {
			if (cur < n && greater(min, cur)) {
				min = cur;
			}
		}
		return min;
	}
	
	private void resize(int N) {
		Key[] array = (Key[]) new Comparable[N];
		for (int i = 0; i < Math.min(keys.length, array.length); i++) {
			array[i] = keys[i];
			keys[i] = null;
		}
		keys = array;
	}
	

	public Iterator<Key> iterator() {
		return new MyIterator();
	}
	
	//Constructs an Iterator over the keys in linear time
	private class MyIterator implements Iterator<Key> {
		MultiwayMinPQ<Key> data;
		
		public MyIterator() {
			data = new MultiwayMinPQ<Key>(comp, d);
			data.keys = (Key[]) new Comparable[keys.length];
			data.n = n;
			for (int i = 0; i < keys.length; i++) {
				data.keys[i] = keys[i];
			}
		}

		public boolean hasNext() {
			return !data.isEmpty();
		}
		
		public Key next() {
                        if (!hasNext()) throw new NoSuchElementException();
			return data.delMin();
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

	public static void main(String[] args){
		MultiwayMinPQ<String> mmPQ=new MultiwayMinPQ<String>(4);
		mmPQ.insert("a");
		mmPQ.insert("w");
		mmPQ.insert("c");
		mmPQ.insert("a");
		mmPQ.insert("m");
		mmPQ.insert("z");
		for(String a:mmPQ){
			StdOut.print(" "+a);
		}
		StdOut.print("\n");
	}
	
}