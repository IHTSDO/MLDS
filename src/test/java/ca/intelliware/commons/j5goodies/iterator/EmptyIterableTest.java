package ca.intelliware.commons.j5goodies.iterator;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class EmptyIterableTest extends TestCase {
	
	public void testNullSafeIterable() throws Exception {
		List<String> thing = null;
		assertIterable(EmptyIterable.nullSafeIterable(thing));
	}
	private void assertIterable(Iterable<?> i) {
		assertNotNull("iterable", i);
		assertNotNull("iterator", i.iterator());
		assertFalse("has next", i.iterator().hasNext());
		assertNull("next", i.iterator().next());
	}
	public void testNullSafeIterableArray() throws Exception {
		String[] list = null;
		assertIterable(EmptyIterable.nullSafeIterable(list));
	}
	public void testArray() throws Exception {
		String[] list = new String[] { "Fred" };
		Iterable<String> i = EmptyIterable.nullSafeIterable(list);
		assertNotNull("iterable", i);
		Iterator<String> iterator = i.iterator();
		assertNotNull("iterator", iterator);
		assertNotNull("hasNext()", iterator.hasNext());
		assertEquals("next()", "Fred", iterator.next());
		assertFalse("no more", iterator.hasNext());
	}
	public void testNullSafeIterableMap() throws Exception {
		Map<String,Date> map = null;
		assertIterable(EmptyIterable.nullSafeKeySetIterable(map));
	}
	public void testNullSafeIterableMapValues() throws Exception {
		Map<String,Date> map = null;
		assertIterable(EmptyIterable.nullSafeValuesIterable(map));
	}
}
