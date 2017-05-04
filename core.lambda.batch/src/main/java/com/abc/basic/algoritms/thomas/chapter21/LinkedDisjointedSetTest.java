package com.abc.basic.algoritms.thomas.chapter21;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;
import com.abc.basic.algoritms.thomas.chapter21.LinkedDisjointedSet.AppData;

public class LinkedDisjointedSetTest extends TestCase {

	private static class LinkedDisjointedSetHolder {
		private LinkedDisjointedSet set;
		private Set<AppData> hs = new HashSet<AppData>();
		
		public LinkedDisjointedSetHolder(AppData d) {
			set = new LinkedDisjointedSet(d);
			hs.add(d);
		}
		
		public int size() { 
			int result = set.size();
			assertEquals(hs.size(), result);
			return result;
		}
		public void union(LinkedDisjointedSetHolder sh) {
			set.union(sh.set);
			set.checkConstraint();
			hs.addAll(sh.hs);
		}
		public AppData getRepresent() {
			AppData result = set.getRepresent();
			assertEquals(true, hs.contains(result));
			return result;
		}
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private static class IntAppData extends AppData {
		private int d;
		public IntAppData(int i) { d = i; }
		public String toString() { return String.valueOf(d); }
	}
	
	public void testSize() {
		LinkedDisjointedSetHolder set = new LinkedDisjointedSetHolder(new IntAppData(3));
		assertEquals(1, set.size());
		set.union(new LinkedDisjointedSetHolder(new IntAppData(5)));
		assertEquals(2, set.size());
	}

	public void testUnion() {
		Random random = new Random();
		LinkedDisjointedSetHolder set1 = new LinkedDisjointedSetHolder(new IntAppData(random.nextInt(1000)));
		int size = random.nextInt(500) + 250;
		for (int i = 1; i < size; i++ ) {
			set1.union(new LinkedDisjointedSetHolder(new IntAppData(random.nextInt(2 * size))));
		}
		
		LinkedDisjointedSetHolder set2 = new LinkedDisjointedSetHolder(new IntAppData(random.nextInt(1000)));
		size = random.nextInt(500) + 250;
		for (int i = 1; i < size; i++ ) {
			set2.union(new LinkedDisjointedSetHolder(new IntAppData(random.nextInt(2 * size))));
		}
		
		set1.union(set2);
	}

	public void testFindSet() {
		IntAppData d1 = new IntAppData(3);
		IntAppData d2 = new IntAppData(5);
		
		LinkedDisjointedSetHolder set = new LinkedDisjointedSetHolder(d1);
		set.union(new LinkedDisjointedSetHolder(d2));
		
		assertSame(LinkedDisjointedSet.findSet(d1), LinkedDisjointedSet.findSet(d2));
	}

}
