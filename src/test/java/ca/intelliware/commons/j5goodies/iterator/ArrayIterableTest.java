package ca.intelliware.commons.j5goodies.iterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class ArrayIterableTest extends TestCase {

	public void testReverseArray() throws Exception {
		List<String> expected = new ArrayList<String>(Arrays.asList("wilma", "barney", "fred"));
		for (String s : ArrayIterable.reverse(new String[] { "fred", "barney", "wilma" })) {
			assertEquals(s, expected.remove(0), s);
		}
	}
}
