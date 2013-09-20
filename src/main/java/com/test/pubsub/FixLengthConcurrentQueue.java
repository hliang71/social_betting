package com.test.pubsub;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A delegation to the embedded ConcurentLinkedQueue, but support fix length
 * feature. extra length is removed during the add.
 * 
 * @author hliang
 * 
 * @param <T>
 */
public class FixLengthConcurrentQueue<T> implements Queue<T> {
	private final ConcurrentLinkedQueue<T> delegate = new ConcurrentLinkedQueue<T>();
	private final Integer maxSize;

	public FixLengthConcurrentQueue(int maxSize) {
		this.maxSize = maxSize < 0 ? 0 : maxSize;
	}

	@Override
	public boolean add(T t) {
		boolean result = delegate.add(t);
		if (delegate.size() > this.maxSize) {
			this.delegate.remove();
		}
		return result;
	}

	@Override
	public int size() {
		return this.delegate.size();
	}

	@Override
	public boolean isEmpty() {
		return this.delegate.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.delegate.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return this.delegate.iterator();
	}

	@Override
	public Object[] toArray() {
		return this.delegate.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.delegate.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return this.delegate.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.delegate.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (c == null || c.size() <= this.maxSize) {
			return this.delegate.addAll(c);
		}
		throw new IllegalArgumentException(
				"the sublist is too large to fit in the queue.");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.delegate.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.delegate.retainAll(c);
	}

	@Override
	public void clear() {
		this.delegate.clear();
	}

	@Override
	public boolean offer(T e) {
		return this.add(e);
	}

	@Override
	public T remove() {
		return this.delegate.remove();
	}

	@Override
	public T poll() {

		return this.delegate.poll();
	}

	@Override
	public T element() {
		return this.delegate.element();
	}

	@Override
	public T peek() {
		return this.delegate.peek();
	}

}
