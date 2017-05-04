package com.abc.basic.algoritms.thomas.chapter18;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
import com.abc.basic.algoritms.thomas.chapter18.BTree.NodeKey;

public class BTreeTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	static class BTreeHolder {
		BTree tree;
		List<Integer> data = new ArrayList<Integer>();
		
		public BTreeHolder() { tree = new BTree(); }
		public BTreeHolder(int d) { tree = new BTree(d); }
		
		public void insert(int k) {
			tree.insert(k);
			tree.checkConstraint();
			data.add(k);
			assertEquals(data.size(), tree.size());
		}
		
		public boolean delete(int k) {
			boolean result = tree.delete(k);
			tree.checkConstraint();
			assertEquals(data.remove((Integer)k), result);
			assertEquals(data.size(), tree.size());
			return result;
		}
		public boolean isEmpty() { return tree.isEmpty(); }
		public int size() { return tree.size(); }
		public NodeKey search(int k) { return tree.search(k); }
	}
	
	public void testSize() {
		BTreeHolder tree = new BTreeHolder();
		assertEquals(true, tree.isEmpty());
		assertEquals(0, tree.size());
		
		tree.insert(5);
		assertEquals(false, tree.isEmpty());
		assertEquals(1, tree.size());
		
		tree.insert(3);
		assertEquals(2, tree.size());
	}

	public void testInsert() {
		BTreeHolder tree = new BTreeHolder();
		
		tree.insert(20);		
		tree.insert(40);
		tree.insert(60);
		tree.insert(45);
		tree.insert(35);
		assertEquals(5, tree.size());
		NodeKey nodeKey = tree.search(40);
		assertNotNull(nodeKey);
		assertEquals(40, nodeKey.node.key[nodeKey.keyIndex]);
		
		nodeKey = tree.search(45);
		assertNotNull(nodeKey);
		assertEquals(45, nodeKey.node.key[nodeKey.keyIndex]);
		
		nodeKey = tree.search(23);
		assertEquals(null, nodeKey);
	}
	
	public void testRandomInsert() {
		BTreeHolder tree = new BTreeHolder();
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			tree.insert(random.nextInt(100));
		}
		assertEquals(100, tree.size());
		
		tree = new BTreeHolder(5);
		for (int i = 0; i < 1000; i++) {
			tree.insert(random.nextInt(100));
		}
		assertEquals(1000, tree.size());
	}
	
	public void testDelete() {
		BTreeHolder tree = new BTreeHolder();
		
		tree.insert(20);		
		tree.insert(40);
		tree.insert(60);
		tree.insert(45);
		tree.insert(35);
		assertEquals(5, tree.size());
		
		assertEquals(true, tree.delete(45));
		assertEquals(false, tree.delete(45));
		assertEquals(true, tree.delete(60));
		assertEquals(true, tree.delete(40));
		assertEquals(true, tree.delete(20));
		assertEquals(true, tree.delete(35));		
		assertEquals(0, tree.size());
	}
	
	public void testRandomDelete() {
		BTreeHolder tree = null;
		tree = new BTreeHolder();
		int [] array = new int[] {6, 2, 8, 0, 2};
		for (int i = 0; i < array.length; i++) {
			tree.insert(array[i]);
		}
		assertEquals(true, tree.delete(8));
		
		tree = new BTreeHolder();
		array = new int[] {3, 4, 2, 3, 1, 5, 5, 6, 6, 9};
		for (int i = 0; i < array.length; i++) {
			tree.insert(array[i]);
		}
		assertEquals(true, tree.delete(4));
		
		tree = new BTreeHolder();
		array = new int[] {0, 0, 4, 8, 0, 5, 5, 6, 9, 9};
		for (int i = 0; i < array.length; i++) {
			tree.insert(array[i]);
		}
		assertEquals(false, tree.delete(1));
		
		tree = new BTreeHolder(5);
		array = new int[] {2, 8, 9, 7, 8, 0, 6, 3, 7, 2};
		for (int i = 0; i < array.length; i++) {
			tree.insert(array[i]);
		}
		tree.delete(6);
		tree.delete(6);
		
		Random random = new Random();
		tree = new BTreeHolder(5);
		for (int j = 0; j < 1000; j++) {
			tree.insert(random.nextInt(100));
		}
		for (int j = 0; j < 2000; j++) {
			tree.delete(random.nextInt(100));
		}
		
		int num = 10;
		for (int j = 0; j < 100; j++) {
			p("count " + j);
			tree = new BTreeHolder(5);
			for (int i = 0; i < num; i++) {
				int d = random.nextInt(10);
				p("insert " + d);
				tree.insert(d);
			}
			assertEquals(num, tree.size());
			for (int i = 0; i < num; i++) {
				int d = random.nextInt(10);
				p("delete " + d);
				tree.delete(d);
			}
		}
	}
	
	boolean debug = false;
	void p(String msg) {
		if (debug) System.out.println(msg);
	}
}
