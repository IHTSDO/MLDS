package ca.intelliware.commons.j5goodies.iterator;

import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.lang.NotImplementedException;

public class EnumeratedIterable {

	static class IteratorImpl<T> implements Iterator<T> {

		private final Enumeration<T> e;

		public IteratorImpl(Enumeration<T> e) {
			this.e = e;
		}

		public boolean hasNext() {
			return e.hasMoreElements();
		}

		public T next() {
			return e.nextElement();
		}

		public void remove() {
			throw new NotImplementedException("remove()");
		}

	}

	static class IterableImpl<T> implements Iterable<T> {
		private final Enumeration<T> e;
		public IterableImpl(Enumeration<T> e) {
			this.e = e;
		}
		public Iterator<T> iterator() {
			return new IteratorImpl<T>(e);
		}
	}

	public static <T> Iterable<T> iterable(Enumeration<T> e) {
		return new IterableImpl<T>(e);
	}
	
}
