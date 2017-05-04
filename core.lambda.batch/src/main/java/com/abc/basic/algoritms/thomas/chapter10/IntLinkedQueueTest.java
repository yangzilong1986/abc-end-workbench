package com.abc.basic.algoritms.thomas.chapter10;

import junit.framework.TestCase;

public class IntLinkedQueueTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSize() {
		IntLinkedQueue queue = new IntLinkedQueue();
		assertEquals(true, queue.isEmpty());
		assertEquals(0, queue.size());
		
		queue.enqueue(3);
		assertEquals(false, queue.isEmpty());
		assertEquals(1, queue.size());
		

		queue.dequeue();
		assertEquals(0, queue.size());
	}

	public void testEnqueue() {
		IntLinkedQueue queue = new IntLinkedQueue();
		for (int i = 0; i < 200; i += 2) {
			queue.enqueue(i);
			queue.enqueue(i+1);
			assertEquals(i / 2, queue.dequeue());
		}
		
		assertEquals(100, queue.size());
		assertEquals(100, queue.peek());
	}

	public void testDequeue() {
		IntLinkedQueue queue = new IntLinkedQueue();
		
		try {
			queue.peek();
			fail("cannot dequeue empty queue");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		queue.enqueue(3);
		assertEquals(3, queue.dequeue());
		assertEquals(0, queue.size());
	}

	public void testPeek() {
		IntLinkedQueue queue = new IntLinkedQueue();
		try {
			queue.peek();
			fail("cannot peek empty queue");
		} catch (IndexOutOfBoundsException e) {
			assertTrue(true);
		}
		
		queue.enqueue(3);
		assertEquals(3, queue.peek());
		assertEquals(1, queue.size());
	}

}
