package org.hibernate.eclipse.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JoinedIterator implements Iterator {

	private static final Iterator[] ITERATORS = {};

	// wrapped iterators
	private List<Iterator> iterators;

	// index of current iterator in the wrapped iterators array
	private int currentIteratorIndex;

	// the current iterator
	private Iterator currentIterator;

	// the last used iterator
	private Iterator lastUsedIterator;

	public JoinedIterator(List<Iterator> iterators) {
		if( iterators==null )
			throw new NullPointerException("Unexpected NULL iterators argument");
		this.iterators = iterators;
	}

	public JoinedIterator(Iterator[] iterators) {
		this.iterators = new ArrayList(iterators.length);
		for (Iterator iterator : iterators) {
			this.iterators.add(iterator);
		}
	}

	public JoinedIterator(Iterator first, Iterator second) {
		iterators = new ArrayList(2);
		iterators.add(first);
		iterators.add(second);
	}

	public boolean hasNext() {
		updateCurrentIterator();
		return currentIterator.hasNext();
	}

	public Object next() {
		updateCurrentIterator();
		return currentIterator.next();
	}

	public void remove() {
		updateCurrentIterator();
		lastUsedIterator.remove();
	}


	// call this before any Iterator method to make sure that the current Iterator
	// is not exhausted
	protected void updateCurrentIterator() {

		if (currentIterator == null) {
			if( iterators.size()==0  ) {
				currentIterator = EmptyIterator.INSTANCE;
			}
			else {
				currentIterator = iterators.get(0);
			}
			// set last used iterator here, in case the user calls remove
			// before calling hasNext() or next() (although they shouldn't)
			lastUsedIterator = currentIterator;
		}

		while (! currentIterator.hasNext() && currentIteratorIndex < iterators.size() - 1) {
			currentIteratorIndex++;
			currentIterator = iterators.get(currentIteratorIndex);
		}
	}

	private static final class EmptyIterator implements Iterator {

		public static final Iterator INSTANCE = new EmptyIterator();

		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new UnsupportedOperationException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private EmptyIterator() {}

	}
	
}
