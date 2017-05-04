package com.abc.basic.algoritms.thomas.chapter07;

import com.abc.basic.algoritms.thomas.common.SorterTestCase;

import java.util.Comparator;

public class QuickSorterTest extends SorterTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	@Override
	public void sort(int[] a) {
		QuickSorter.sort(a);
	}

	@Override
	public void sort(Object[] a) {
		QuickSorter.sort(a);
	}

	@Override
	public <T> void sort(T[] a, Comparator<? super T> c) {
		QuickSorter.sort(a, c);
	}
}
