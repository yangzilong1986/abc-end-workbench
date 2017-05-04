package com.abc.basic.algoritms.thomas.chapter06;

import com.abc.basic.algoritms.thomas.common.SorterTestCase;

import java.util.Comparator;

//import common.SorterTestCase;

public class HeapSorterTest extends SorterTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	public void sort(int[] a) {
		HeapSorter.sort(a);
	}

	@Override
	public void sort(Object[] a) {
		HeapSorter.sort(a);
	}

	@Override
	public <T> void sort(T[] a, Comparator<? super T> c) {
		HeapSorter.sort(a, c);
	}
}
