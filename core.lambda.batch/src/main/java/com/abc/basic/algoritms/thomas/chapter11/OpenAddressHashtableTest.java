package com.abc.basic.algoritms.thomas.chapter11;

import junit.framework.TestCase;

public class OpenAddressHashtableTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private static class MockInt {
		private int d;
		
		public MockInt(int d) {
			this.d = d;
		}
		
		/** 每五个一组返回相同的hashCode，这是为了测试冲突发生的情况 */
		public int hashCode() {
			return d / 5;
		}
		
		public boolean equals(Object o) {
			if (o == null || !(o instanceof MockInt)) return false;
			MockInt other = (MockInt) o;
			return d == other.d;
		}
		
		public String toString() { return String.valueOf(d); };
	}
	
	public void testPut() {
		OpenAddressHashtable<MockInt, String> map = new OpenAddressHashtable<MockInt, String>();
		assertEquals(0, map.size());
		
		assertEquals(true, map.put(new MockInt(1), "one"));
		assertEquals(1, map.size());
		assertEquals("one", map.get(new MockInt(1)));
		
		assertEquals(false, map.put(new MockInt(1), "oneone"));
		assertEquals(1, map.size());
		assertEquals("oneone", map.get(new MockInt(1)));
		
		assertEquals(true, map.put(new MockInt(2), "two"));
		assertEquals(2, map.size());
		
		for (int i = 0; i < 200; i++) {
			map.put(new MockInt(i), "value " + i);
		}
		assertEquals(200, map.size());
		assertEquals("value 1", map.get(new MockInt(1)));
	}

	public void testDelete() {
		OpenAddressHashtable<MockInt, String> map = new OpenAddressHashtable<MockInt, String>();
		assertEquals(0, map.size());
		
		map.put(new MockInt(1), "one");
		assertEquals(1, map.size());
		assertEquals("one", map.get(new MockInt(1)));
		
		map.delete(new MockInt(1));
		assertEquals(false, map.contains(new MockInt(1)));
	}

	public void testContains() {
		OpenAddressHashtable<MockInt, String> map = new OpenAddressHashtable<MockInt, String>();
		assertEquals(false, map.contains(new MockInt(1)));
		
		map.put(new MockInt(1), "one");
		assertEquals(true, map.contains(new MockInt(1)));
	}

}
