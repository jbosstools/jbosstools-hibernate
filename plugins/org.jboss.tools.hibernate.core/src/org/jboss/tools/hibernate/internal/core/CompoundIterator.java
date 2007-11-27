/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author alex
 *
 * A compound iterator, which iterates over all of the elements in the
 * given iterators.
 */
public class CompoundIterator<T> implements Iterator<T> {

	protected List<Iterator<T>> iters;
	protected int index;
	
	public CompoundIterator(List<Iterator<T>> iterators) {
		iters = iterators;
	}

	public CompoundIterator(Iterator<T>[] iterators) {
		if( iterators==null )
			throw new NullPointerException("array is null");
		iters = new ArrayList<Iterator<T>>();
		for (int i = 0; i < iterators.length; i++) {
			iters.add(iterators[i]);
		}
	}

	public CompoundIterator(Iterator<T> first, Iterator<T> second) {
		iters = new ArrayList<Iterator<T>>();
		iters.add(first);
		iters.add(second);
	}
	
	
	public boolean hasNext() {
		for (; index < iters.size(); index++) 
	 	   if (iters.get(index) != null && iters.get(index).hasNext()) 
	 	      return true;
	    return false;
    }
	public T next() {
		if (!hasNext()) throw new NoSuchElementException();
       return iters.get(index).next();
	}
		 
	public void remove() {
		iters.get(index).remove();
	}
}
