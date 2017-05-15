package com.abc.basic.algoritms.thomas.chapter02;

import com.abc.basic.algoritms.thomas.common.SorterTestCase;

import java.util.Comparator;

//import common.SorterTestCase;

public class InsertionSorterTest extends SorterTestCase {

	@Override
	public void sort(int[] a) {
		InsertionSorter.sort(a);
	}

	@Override
	public void sort(Object[] a) {
		InsertionSorter.sort(a);
	}

	@Override
	public <T> void sort(T[] a, Comparator<? super T> c) {
		InsertionSorter.sort(a, c);
	}
}
