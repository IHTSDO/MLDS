package ca.intelliware.commons.j5goodies.iterator;

import java.util.Iterator;
import java.util.Map;

public class EmptyIterable<T> implements Iterable<T> {

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            public boolean hasNext() {
                return false;
            }
            public T next() {
                return null;
            }
            public void remove() {
            }
        };
    }
    
    public static <T> Iterable<T> nullSafeIterable(Iterable<T> i) {
    	return i == null ? new EmptyIterable<T>() : i;
    }
    
    public static <T> Iterable<T> nullSafeIterable(T[] i) {
    	return i == null ? new EmptyIterable<T>() : new ArrayIterable<T>(i);
    }

    public static <K,V> Iterable<K> nullSafeKeySetIterable(Map<K,V> map) {
    	return map == null ? new EmptyIterable<K>() : nullSafeIterable(map.keySet());
    }
    
    public static <K,V> Iterable<V> nullSafeValuesIterable(Map<K,V> map) {
    	return map == null ? new EmptyIterable<V>() : nullSafeIterable(map.values());
    }
    
}
