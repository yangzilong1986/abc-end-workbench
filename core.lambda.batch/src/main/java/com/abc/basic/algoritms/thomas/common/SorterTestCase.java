package com.abc.basic.algoritms.thomas.common;

import java.util.Comparator;

public abstract class SorterTestCase extends BaseTestCase {

	public void testSortIntArray() {
		int[] a = {3, 2, 5, 1};
		sort(a);
		assertArrayEquals(new int[] {1,2,3,5}, a);
	}
	
	public void testSortObjectArray() {
		Integer[] a = {3, 2, 5, 1};
		sort(a);
		assertArrayEquals(new Integer[] {1,2,3,5}, a);
	}
	
	public void testSortObjectArrayByComparator() {
		Integer[] a = {3, 2, 5, 1};
		sort(a, new Comparator<Integer>() {
			public int compare(Integer o1, Integer o2) {
				return o2 - o1;
			}
		});
		assertArrayEquals(new Integer[] {5,3,2,1}, a);
	}
	
	public void sort(int[] a) {
		throw new UnsupportedOperationException();
	}
	
	public void sort(Object[] a) {
		throw new UnsupportedOperationException();
	}
	
	public <T> void sort(T[] a, Comparator<? super T> c) {
		throw new UnsupportedOperationException();
	}
}
