package com.abc.basic.algoritms.thomas.chapter10;

import junit.framework.TestCase;

public class IntLinkedStackTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSize() {
		IntLinkedStack stack = new IntLinkedStack();
		assertEquals(true, stack.isEmpty());
		assertEquals(0, stack.size());
		
		stack.push(3);
		assertEquals(false, stack.isEmpty());
		assertEquals(1, stack.size());
	}

	public void testPushPop() {
		IntLinkedStack stack = new IntLinkedStack();
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
		
		assertEquals(3, stack.pop());
		assertEquals(true, stack.isEmpty());
		
		try {
			stack.pop();
			fail("should fail to pop");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
	}

}
