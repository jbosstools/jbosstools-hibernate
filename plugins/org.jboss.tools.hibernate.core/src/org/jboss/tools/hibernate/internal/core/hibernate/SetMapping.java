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

import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor;
import org.jboss.tools.hibernate.core.hibernate.ISetMapping;

/**
 * A set with no nullable element columns. It will have a primary key
 * consisting of all table columns (ie. key columns + element columns).
 */
public class SetMapping extends CollectionMapping implements ISetMapping {
	private static final long serialVersionUID = 1L;
	/**
	 * Constructor for Set.
	 * @param owner
	 */
	public SetMapping(IHibernateClassMapping owner) {
		super(owner);
	}

	public boolean isSet() {
		return true;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		if(visitor instanceof IHibernateMappingVisitor) 
		    return ((IHibernateMappingVisitor)visitor).visitSetMapping(this,argument);
		return visitor.visitPersistentValueMapping(this,argument);
	}

}
