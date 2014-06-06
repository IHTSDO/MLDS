package ca.intelliware.commons.j5goodies.iterator;

import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;

public class ArrayIterable<T> implements Iterable<T> {

	public class IteratorImpl implements Iterator<T> {
		private int index;
		public boolean hasNext() {
			return this.index < ArrayIterable.this.t.length;
		}
		public T next() {
			return ArrayIterable.this.t[this.index++];
		}
		public void remove() {
		}
	}

	private final T[] t;

	ArrayIterable(T[] t) {
		this.t = t;
	}

	public Iterator<T> iterator() {
		return new IteratorImpl();
	}

	@SuppressWarnings("unchecked")
	public static <T> Iterable<T> reverse(T[] array) {
		T[] copy = (T[]) ArrayUtils.clone(array);
		ArrayUtils.reverse(copy);
		return new ArrayIterable<T>(copy);
	}
}
