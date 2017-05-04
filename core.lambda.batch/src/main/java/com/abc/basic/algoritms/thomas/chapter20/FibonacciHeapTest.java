package com.abc.basic.algoritms.thomas.chapter20;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
import com.abc.basic.algoritms.thomas.chapter12.IntIterator;
import com.abc.basic.algoritms.thomas.chapter20.FibonacciHeap.Node;

public class FibonacciHeapTest extends TestCase {
	private static final boolean DEBUG = true;
	
	private static class FibonacciHeapHolder {
		FibonacciHeap heap = new FibonacciHeap();
		List<Integer> data = new ArrayList<Integer>();
		
		public void insert(int k) {
			heap.insert(k);
			heap.checkConstraint();
			data.add(k);
			assertEquals(data.size(), heap.size());
		}
		
		public int minimum() {
			int result = heap.minimum();
			assertEquals(min(data), result);
			return result;
		}
		
		private int min(List<Integer> d) {
			int min = d.get(0);
			for (int i = 1; i < d.size(); i++) {
				if (min > d.get(i)) min = d.get(i);
			}
			return min;
		}
		
		public int extractMin() {
			int result = heap.extractMin();
			heap.checkConstraint();
			int actual = min(data);
			data.remove((Integer) actual);
			
			assertEquals(actual, result);
			assertEquals(data.size(), heap.size());
			return result;
		}
		
		Node search(int k) {
			Node result = heap.search(k);
			if (data.contains(k)) {
				assertNotNull(result);
				assertEquals(k, result.key);
			} else {
				assertEquals(null, result);
			}
			return result;
		}
		
		boolean delete(int k) {
			Node n = search(k);
			if (n != null) {
				heap.delete(n);
				heap.checkConstraint();
				data.remove((Integer)k);
				assertEquals(data.size(), heap.size());
				return true;
			} else {
				return false;
			}
		}
		
		boolean descreaseKey(int k, int newKey) {
			Node n = search(k);
			if (n != null) {
				heap.decreaseKey(n, newKey);
				heap.checkConstraint();
				data.remove((Integer)k);
				data.add((Integer)newKey);
				return true;
			} else {
				return false;
			}
		}
		
		public void union(FibonacciHeapHolder heap) {
			this.data.addAll(heap.data);
			this.heap.union(heap.heap);
			this.heap.checkConstraint();
			assertEquals(data.size(), this.heap.size());
		}
		
		public void validateData() {
			IntIterator itor = heap.iterator();
			int[] array = new int[heap.size()];
			for (int i = 0; i < array.length; i++) {
				array[i] = itor.next();
			}
			Arrays.sort(array);
			
			int[] expectedData = new int[data.size()];
			for (int i = 0; i < data.size(); i++) {
				expectedData[i] = data.get(i);
			}
			Arrays.sort(expectedData);
			
			assertEquals(true, Arrays.equals(expectedData, array));
		}
		
		public IntIterator iterator() { return heap.iterator(); }
		public int size() { return heap.size(); }
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	public void testMinimum() {
		p("testMinimum");
		FibonacciHeapHolder heap = new FibonacciHeapHolder();
		try {
			heap.minimum();
			fail("should fail");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		heap.insert(5);
		assertEquals(5, heap.minimum());
		
		heap.insert(3);
		assertEquals(3, heap.minimum());
		
		heap.insert(8);
		heap.minimum();
		
		heap.insert(2);
		heap.minimum();
		
		heap = new FibonacciHeapHolder();
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			int k = random.nextInt(20);
			p("insert " + k);
			heap.insert(k);
		}
	}

	private void p(String msg) {
		if (DEBUG) System.out.println(msg);
	}
		
	public void testInsert() {
		FibonacciHeapHolder heap = new FibonacciHeapHolder();
		
		Random random = new Random();
		int num = random.nextInt(200) + 100;
		for (int i = 0; i < num; i++) {
			int k = random.nextInt(num);
			heap.insert(k);
		}
		heap.validateData();
	}

	public void testExtractMin() {
		FibonacciHeapHolder heap = new FibonacciHeapHolder();

		heap.insert(5);
		assertEquals(5, heap.extractMin());
		try {
			heap.extractMin();
			fail("should fail");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		heap.insert(2); heap.insert(8); heap.insert(3); heap.insert(7);
		heap.extractMin();
		heap.extractMin();
		heap.extractMin();
		heap.extractMin();
		
		Random random = new Random();
		int num = random.nextInt(200) + 100;
		for (int i = 0; i < num; i++) {
			int k = random.nextInt(num);
			heap.insert(k);
		}
		for (int i = 0; i < num; i++) {
			heap.extractMin();
		}
		assertEquals(0, heap.size());
	}

	public void testRandomInsertAndExtractMin() {
		FibonacciHeapHolder heap = new FibonacciHeapHolder();
		Random methodRandom = new Random();
		Random random = new Random();
		
		int runCount = new Random().nextInt(1000);
		for (int i = 0; i < runCount; i++) {
			int method = methodRandom.nextInt(2);
			if (method == 0) { // extract
				if (heap.size() == 0) i--;
				else heap.extractMin();
			} else if (method == 1) { // insert
				heap.insert(random.nextInt(runCount << 1));
			} else {
				throw new RuntimeException("Impossible");
			}
		}
		for (int i = 0; i < heap.size(); i++) {
			heap.extractMin();
		}
	}
	
	public void testMethods() {
		FibonacciHeapHolder heap = new FibonacciHeapHolder();
		Random methodRandom = new Random();
		Random random = new Random();
		
		int runCount = new Random().nextInt(2000) + 2000;
		for (int i = 0; i < runCount; i++) {
			int method = methodRandom.nextInt(4);
			if (method == 0) {
				if (heap.size() > 0) heap.extractMin();
			} else if (method == 1) {
				heap.insert(random.nextInt(runCount << 1));
			} else if (method == 2) {
				int k = random.nextInt(runCount << 1);
				if (k > 0)
					heap.descreaseKey(k, random.nextInt(k));
			} else {
				heap.delete(random.nextInt(runCount << 1));
			}
		}
	}
	
	public void testUnion() {
		FibonacciHeapHolder holder1 = new FibonacciHeapHolder();
		Random random = new Random();
		int num = 0x1A;
		for (int i = 0; i < num; i++) {
			holder1.insert(random.nextInt(50));
		}
		
		FibonacciHeapHolder holder2 = new FibonacciHeapHolder();
		num = 0x24;
		for (int i = 0; i < num; i++) {
			holder2.insert(random.nextInt(50));
		}
		holder1.union(holder2);
		holder1.validateData();
		
		holder1 = new FibonacciHeapHolder();
		num = random.nextInt(100) + 200;
		for (int i = 0; i < num; i++) {
			holder1.insert(random.nextInt(50));
		}
		holder2 = new FibonacciHeapHolder();
		num = random.nextInt(100) + 200;
		for (int i = 0; i < num; i++) {
			holder2.insert(random.nextInt(50));
		}
		holder1.union(holder2);
		holder1.validateData();
	}

	public void testIterator() {
		FibonacciHeapHolder heap = new FibonacciHeapHolder();
		heap.insert(5);
		heap.insert(3);		
		heap.insert(8);
		heap.insert(2);
		heap.extractMin();
		
		int[] a = new int[] { 3, 8, 5};
		int i = 0;
		for (IntIterator itor = heap.iterator(); itor.hasNext();) {
			int next = itor.next();
			p("" + next);
			assertEquals(a[i++], next);
			
		}
	}
	
	public void testSearch() {
		FibonacciHeapHolder heap = new FibonacciHeapHolder();
		int [] a = { 5, 3, 8, 2, 7};
		for (int i = 0; i < a.length; i++) heap.insert(a[i]);
		
		for (int i = 0; i < a.length; i++) {
			assertNotNull(heap.search(a[i]));
		}
		assertNull(heap.search(6));
		
		Random random = new Random();
		int num = random.nextInt(200) + 100;
		for (int i = 0; i < num; i++) {
			int k = random.nextInt(num);
			heap.insert(k);
		}
		for (int i = 0; i < 2 * num; i++) {
			int k = random.nextInt(num);
			heap.search(k);
		}
	}
	
	public void testDelete() {
		FibonacciHeapHolder heap = new FibonacciHeapHolder();
		int [] a = { 5, 3, 8, 2, 7};
		for (int i = 0; i < a.length; i++) heap.insert(a[i]);
		
		assertEquals(true, heap.delete(3));
		assertEquals(4, heap.size());
		assertEquals(false, heap.delete(3));
		assertEquals(true, heap.delete(7));
		
		Random random = new Random();
		int num = random.nextInt(200) + 100;
		for (int i = 0; i < num; i++) {
			int k = random.nextInt(num);
			heap.insert(k);
		}
		for (int i = 0; i < 2 * num; i++) {
			int k = random.nextInt(num);
			heap.delete(k);
		}
		heap.validateData();
		
		p("now size: " + heap.size());
		
	}
	
}
