package com.abc.basic.algoritms.thomas.common;
import java.util.Arrays;

import junit.framework.TestCase;


public abstract class BaseTestCase extends TestCase
{

	public void assertArrayEquals(Object[] expected, Object[] actual) {
		if (!Arrays.equals(expected, actual)) {
			assertEquals(Arrays.toString(expected), Arrays.toString(actual));
//			 上面针对字符的比较有可能没有失败，所以。。。
			fail("array not equal");
		}
	}
	
	public void assertArrayEquals(String msg, Object[] expected, Object[] actual) {
		if (!Arrays.equals(expected, actual)) {
			assertEquals(msg, Arrays.toString(expected), Arrays.toString(actual));
			fail("array not equal");
		}
	}
	
	public void assertArrayEquals(int[] expected, int[] actual) {
		if (!Arrays.equals(expected, actual)) {
			assertEquals(Arrays.toString(expected), Arrays.toString(actual));
			fail("array not equal");
		}
	}
	
	public void assertArrayEquals(String msg, int[] expected, int[] actual) {
		if (!Arrays.equals(expected, actual)) {
			assertEquals(msg, Arrays.toString(expected), Arrays.toString(actual));
			fail("array not equal");
		}
	}
}
