package com.abc.basic.algoritms.thomas.chapter09;

import java.util.Arrays;

import junit.framework.TestCase;

public class OrderStatisticsTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSelect() {
		int[] a = new int[] { 3, 5, 1, 4, 7, 0, 8, 23, 17 };
		
		int[] copya = a.clone();
		Arrays.sort(copya);
		
		for (int i = 0; i < a.length; i++) {
			assertEquals(copya[i], OrderStatistics.select(a, i));
		}
	}

}
