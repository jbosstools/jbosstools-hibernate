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
package org.jboss.tools.hibernate.internal.core.hibernate;

import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.IIndexedCollectionMapping;
import org.jboss.tools.hibernate.internal.core.AbstractOrmElement;


/**
 * Indexed collections include Lists, Maps, arrays and
 * primitive arrays.
 */
public class IndexedCollectionMapping extends CollectionMapping implements IIndexedCollectionMapping {
	private IHibernateValueMapping index;

	/**
	 * Constructor for IndexedCollection.
	 * @param owner
	 */
	public IndexedCollectionMapping(IHibernateClassMapping owner) {
		super(owner);
	}

	public IHibernateValueMapping getIndex() {
		return index;
	}
	public void setIndex(IHibernateValueMapping index) {
		if(index instanceof AbstractOrmElement){
			((AbstractOrmElement)index).setName( "index");
		}
		this.index = index;
	}
	public final boolean isIndexed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.hibernate.CollectionMapping#clear()
	 */
	public void clear() {
		super.clear();
		if(index!=null) index.clear();
	}

}
