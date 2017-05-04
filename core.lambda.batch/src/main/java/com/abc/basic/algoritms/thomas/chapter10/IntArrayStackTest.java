package com.abc.basic.algoritms.thomas.chapter10;

import junit.framework.TestCase;

public class IntArrayStackTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGet() {
		IntArrayStack stack = new IntArrayStack();
		
		try {
			stack.get(0);
			fail("should fail to get element at 0");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		stack.push(3);
		assertEquals(3, stack.get(0));
		assertEquals(1, stack.size());
		
		try {
			stack.get(1);
			fail("should fail to get element at 0");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}

	public void testPushPop() {
		IntArrayStack stack = new IntArrayStack();
		try {
			stack.top();
			fail("should fail to get top element");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		stack.push(3);
		assertEquals(3, stack.top());
		stack.push(5);
		assertEquals(5, stack.top());
		assertEquals(5, stack.pop());
		assertEquals(3, stack.top());		
		assertEquals(1, stack.size());
		
		try {
			stack.pop(2);
			fail("should fail to pop 2 elements");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
		// 判断pop(2)并没有改变堆栈的状态
		assertEquals(1, stack.size());
		assertEquals(3, stack.top());
		
		assertEquals(3, stack.pop(1));
		assertEquals(true, stack.isEmpty());
	}

	// push many data
	public void testPush2() {
		IntArrayStack stack = new IntArrayStack();
		int n = 100;
		for (int i = 0; i < n; i++) {
			stack.push(i);
		}
		assertEquals(n, stack.size());
		assertEquals(n - 1, stack.top());
		
		assertEquals(50, stack.pop(50));
		assertEquals(49, stack.top());
		assertEquals(50, stack.size());
		
		for (int i = 49; i >= 0; i--) {
			assertEquals(i, stack.pop());
		}
	}
}
