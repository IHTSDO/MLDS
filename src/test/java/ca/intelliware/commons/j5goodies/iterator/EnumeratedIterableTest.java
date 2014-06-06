package ca.intelliware.commons.j5goodies.iterator;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

public class EnumeratedIterableTest extends TestCase {
	
	public void testIterate() throws Exception {
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
		Vector<Integer> vector = new Vector<Integer>(list);
		
		int i = 0;
		for (Integer integer : EnumeratedIterable.iterable(vector.elements())) {
			i++;
			assertEquals("" + i, new Integer(i), integer);
		}
		assertEquals("final", 5, i);
	}
}
