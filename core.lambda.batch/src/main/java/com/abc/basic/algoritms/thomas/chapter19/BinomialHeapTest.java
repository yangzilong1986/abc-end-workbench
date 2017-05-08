package com.abc.basic.algoritms.thomas.chapter19;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;
import com.abc.basic.algoritms.thomas.chapter12.IntIterator;
import com.abc.basic.algoritms.thomas.chapter19.BinomialHeap.Node;

public class BinomialHeapTest extends TestCase {
	private static final boolean DEBUG = true;
	
	private static class BinomialHeapHolder {
		BinomialHeap heap = new BinomialHeap();
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
				data.remove((Integer)k);
				assertEquals(data.size(), heap.size());
				return true;
			} else {
				return false;
			}
		}
		public void union(BinomialHeapHolder heap) {
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
	


	public void testMinimum() {
		BinomialHeapHolder heap = new BinomialHeapHolder();
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
		
		heap = new BinomialHeapHolder();
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			int k = random.nextInt(20);
			p("insert " + k);
			heap.insert(k);
			p("minimum " + heap.minimum());
		}
	}

	private void p(String msg) {
		if (DEBUG) System.out.println(msg);
	}
		
	public void testInsert() {
		BinomialHeapHolder heap = new BinomialHeapHolder();
		
		Random random = new Random();
		int num = random.nextInt(200) + 100;
		for (int i = 0; i < num; i++) {
			int k = random.nextInt(num);
			heap.insert(k);
		}
		heap.validateData();
	}

	public void testExtractMin() {
		BinomialHeapHolder heap = new BinomialHeapHolder();

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

	public void testIterator() {
		BinomialHeapHolder heap = new BinomialHeapHolder();
		heap.insert(5);
		heap.insert(3);		
		heap.insert(8);	
		
		int[] a = new int[] { 8, 3, 5};
		int i = 0;
		for (IntIterator itor = heap.iterator(); itor.hasNext();) {
			int next = itor.next();
			p("" + next);
			assertEquals(a[i++], next);
			
		}
	}
	
	public void testSearch() {
		BinomialHeapHolder heap = new BinomialHeapHolder();
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
		BinomialHeapHolder heap = new BinomialHeapHolder();
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
	
	public void testUnion() {
		BinomialHeapHolder holder1 = new BinomialHeapHolder();
		Random random = new Random();
		int num = 0x1A;
		for (int i = 0; i < num; i++) {
			holder1.insert(random.nextInt(50));
		}
		
		BinomialHeapHolder holder2 = new BinomialHeapHolder();
		num = 0x24;
		for (int i = 0; i < num; i++) {
			holder2.insert(random.nextInt(50));
		}
		holder1.union(holder2);
		holder1.validateData();
		
		holder1 = new BinomialHeapHolder();
		num = random.nextInt(100) + 200;
		for (int i = 0; i < num; i++) {
			holder1.insert(random.nextInt(50));
		}
		holder2 = new BinomialHeapHolder();
		num = random.nextInt(100) + 200;
		for (int i = 0; i < num; i++) {
			holder2.insert(random.nextInt(50));
		}
		holder1.union(holder2);
		holder1.validateData();
	}
	
}
