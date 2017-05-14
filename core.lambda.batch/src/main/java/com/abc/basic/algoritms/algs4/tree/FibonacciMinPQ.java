package com.abc.basic.algoritms.algs4.tree;

import com.abc.basic.algoritms.algs4.utils.In;
import com.abc.basic.algoritms.algs4.utils.StdOut;

import java.util.Iterator;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Comparator;

public class FibonacciMinPQ<Key> implements Iterable<Key> {
	private Node head;					//Head of the circular root list
	private Node min;					//Minimum Node of the root list
	private int size;					//Number of keys in the heap
	private final Comparator<Key> comp;	//Comparator over the keys
	//Used for the consolidate operation
	//合并; 统一
	//保存各个度对应的节点,如度为1的节点对应的节点
	private HashMap<Integer, Node> table = new HashMap<Integer, Node>();
	
	//Represents a Node of a tree
	private class Node {
		Key key;//Key of this Node
		int order;	//Order of the tree rooted by this Node
		//兄弟，姐妹
//		Node left;
//		Node right;
		Node prev, next;//Siblings of this Node
		Node child;	//Child of this Node
	}
	
	/**
	 * Initializes an empty priority queue
	 * Worst case is O(1)
	 * @param C a Comparator over the Keys
	 */
	public FibonacciMinPQ(Comparator<Key> C) {
		comp = C;
	}
	
    /**
     * Initializes an empty priority queue
     * Worst case is O(1)
     */
	public FibonacciMinPQ() {
		comp = new MyComparator();
	}
	
	public FibonacciMinPQ(Key[] a) {
		comp = new MyComparator();
		for (Key k : a) {
			insert(k);
		}
	}
	
	public FibonacciMinPQ(Comparator<Key> C, Key[] a) {
		comp = C;
		for (Key k : a) {
			insert(k);
		}
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

	public void insert(Key key) {
		Node x = new Node();
		x.key = key;
		size++;
		head = insert(x, head);
		if (min == null) {
			min = head;
		}else  {
			min = (greater(min.key, key)) ? head : min;
		}
	}

	public Key minKey() {
		if (isEmpty()) {
			throw new NoSuchElementException("Priority queue is empty");
		}
		return min.key;
	}

	public Key delMin() {
		if (isEmpty()) {
			throw new NoSuchElementException("Priority queue is empty");
		}
		head = cut(min, head);
		Node x = min.child;
		Key key = min.key;
		min.key = null;
		if (x != null) {
			head = meld(head, x);
			min.child = null;
		}
		size--;
		if (!isEmpty()) {
			consolidate();
		}else {
			min = null;
		}
		return key;
	}
	

	public FibonacciMinPQ<Key> union(FibonacciMinPQ<Key> that) {
		this.head = meld(head, that.head);
		this.min = (greater(this.min.key, that.min.key)) ? that.min : this.min;
		this.size = this.size+that.size;
		return this;
	}
	
	/*************************************
	 * General helper functions
	 ************************************/
	
	//Compares two keys
	private boolean greater(Key n, Key m) {
		if (n == null) {
			return false;
		}
		if (m == null) {
			return true;
		}
		return comp.compare(n,m) >= 0;
	}
	
	//Assuming root1 holds a greater key than root2, root2 becomes the new root
	private void link(Node root1, Node root2) {
		root2.child = insert(root1, root2.child);
		root2.order++;
	}
	
	/*************************************
	 * Function for consolidating all trees in the root list
	 ************************************/
	/**
	 * 合并
	 */
	//Coalesce the roots, thus reshapes the tree
	private void consolidate() {
		table.clear();
		Node x = head;
		int maxOrder = 0;
		min = head;
		Node y = null; Node z = null;
		do {
			y = x;
			x = x.next;
			z = table.get(y.order);
			while (z != null) {
				table.remove(y.order);
				if (greater(y.key, z.key)) {
					link(y, z);
					y = z;
				} else {
					link(z, y);
				}
				z = table.get(y.order);
			}
			table.put(y.order, y);
			if (y.order > maxOrder) {
				maxOrder = y.order;
			}
		} while (x != head);
		head = null;
		for (Node n : table.values()) {
			if (n != null) {
				min = greater(min.key, n.key) ? n : min;
				head = insert(n, head);
			}
		}
	}
	

	//Inserts a Node in a circular list containing head, returns a new head
	private Node insert(Node x, Node head) {
		if (head == null) {
			x.prev = x;//
			x.next = x;
		} else {
			head.prev.next = x;
			x.next = head;
			x.prev = head.prev;
			head.prev = x;
		}
		return x;
	}
	
	//Removes a tree from the list defined by the head pointer
	private Node cut(Node x, Node head) {
		if (x.next == x) {
			x.next = null;
			x.prev = null;
			return null;
		} else {
			x.next.prev = x.prev;
			x.prev.next = x.next;
			Node res = x.next;
			x.next = null;
			x.prev = null;
			if (head == x)  return res;
			else 			return head;
		}
	}
	
	//Merges two root lists together
	// 混合，合并; 根List合并
	private Node meld(Node x, Node y) {
		if (x == null) {
			return y;
		}
		if (y == null) {
			return x;
		}
		x.prev.next = y.next;
		y.next.prev = x.prev;
		x.prev = y;
		y.next = x;
		return x;
	}
	
	/*************************************
	 * Iterator
	 ************************************/

	public Iterator<Key> iterator() {
		return new MyIterator();
	}
	
	private class MyIterator implements Iterator<Key> {
		private FibonacciMinPQ<Key> copy;
		
		
		//Constructor takes linear time
		public MyIterator() {
			copy = new FibonacciMinPQ<Key>(comp);
			insertAll(head);
		}
		
		private void insertAll(Node head) {
			if (head == null) return;
			Node x = head;
			do {
				copy.insert(x.key);
				insertAll(x.child);
				x = x.next;
			} while (x != head);
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public boolean hasNext() {
			return !copy.isEmpty();
		}
		
		//Takes amortized logarithmic time
		public Key next() {
			if (!hasNext()) throw new NoSuchElementException();
			return copy.delMin();
		}
	}
	
	/*************************************
	 * Comparator
	 ************************************/
	
	//default Comparator
	private class MyComparator implements Comparator<Key> {
		@Override
		public int compare(Key key1, Key key2) {
			return ((Comparable<Key>) key1).compareTo(key2);
		}
	}
	public static void main(String[] args) {
		FibonacciMinPQ<String> pq = new FibonacciMinPQ<String>();
//		In in = new In(In.PATH_NAME + "m2.txt");
//		while (!in.isEmpty()) {
//			String item = in.readString();
//			if (!item.equals("-")) {
//				pq.insert(item);
//			}
//
//		}
		pq.insert("a");
		pq.insert("c");
		pq.insert("v");
		pq.insert("c");
		StdOut.println("(" + pq.size() + " left on pq)");
//		while (!pq.isEmpty()) {
//			StdOut.print(pq.delMin() + " ");
//		}
		for(String a:pq){
			StdOut.print(a + " ");
		}
	}
}