package com.abc.basic.algoritms.thomas.chapter06;

import com.abc.basic.algoritms.thomas.common.BaseTestCase;

import java.util.Arrays;

//import common.BaseTestCase;

public class IntHeapTest extends BaseTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testBuildHeap() {
		int[] a = {4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
		IntHeap heap = new IntHeap(a);
		
		// whether a is changed during construct IntHeap?
		assertArrayEquals(new int[] {4, 1, 3, 2, 16, 9, 10, 14, 8, 7}, a);
		assertHeapStatus(heap, a.length, a);
	}
	
	private void assertHeapStatus(IntHeap heap, int expectedSize, int[] expectedElements) {
		assertEquals(expectedSize, heap.size());
		int[] heapData = getHeapData(heap);
		Arrays.sort(heapData);
		int[] expectedData = Arrays.copyOf(expectedElements, expectedSize);
		Arrays.sort(expectedData);
		assertArrayEquals(expectedData, heapData);
		
		// check heap property
		heap.validateHeap();
	}
	private int[] getHeapData(IntHeap heap) {
		int[] data = new int[heap.size()];
		for (int i = 0; i < heap.size(); i++) {
			data[i] = heap.get(i);
		}
		return data;
	}
	
	public void testExtractMax() {
		int[] a = {4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
		IntHeap heap = new IntHeap(a);
		
		assertEquals("maximum value", 16, heap.maximum());
		assertEquals("size should not changed", a.length, heap.size());
		
		assertEquals("extract max value", 16, heap.extractMax());
		assertEquals("size should decrement 1", a.length - 1, heap.size());
		assertHeapStatus(heap, a.length - 1, new int[] {4, 1, 3, 2, 9, 10, 14, 8, 7});
	}

	public void testIncreaseKey() {
		int[] a = {4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
		IntHeap heap = new IntHeap(a);
		
		// 将值为4的key增加到15
		int i = 0;
		for (; i < heap.size(); i++) {
			if (heap.get(i) == 4)
				break;
		}
		heap.increaseKey(i, 15);
		a[0] = 15;
		assertHeapStatus(heap, a.length, a);
	}

	public void testInsert() {
		int[] a = {4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
		IntHeap heap = new IntHeap();
		for (int i = 0; i < a.length; i++) {
			heap.insert(a[i]);
		}
		assertHeapStatus(heap, a.length, a);
	}

	public void testHeapSort() {
		int[] a = {4, 1, 3, 2, 16, 9, 10, 14, 8, 7};
		IntHeap.heapSort(a);
		assertArrayEquals(new int[] {1, 2, 3, 4, 7, 8, 9, 10, 14, 16}, a);
	}
	
}
