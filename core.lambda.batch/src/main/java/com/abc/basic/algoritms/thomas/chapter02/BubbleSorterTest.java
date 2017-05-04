package com.abc.basic.algoritms.thomas.chapter02;

import com.abc.basic.algoritms.thomas.common.SorterTestCase;

import java.util.Comparator;

//import common.SorterTestCase;

public class BubbleSorterTest extends SorterTestCase {

	@Override
	public void sort(int[] a) {
		BubbleSorter.sort(a);
	}

	@Override
	public void sort(Object[] a) {
		BubbleSorter.sort(a);
	}

	@Override
	public <T> void sort(T[] a, Comparator<? super T> c) {
		BubbleSorter.sort(a, c);
	}
}
