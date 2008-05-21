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
package org.jboss.tools.hibernate.core.hibernate;

import java.io.Serializable;
import java.util.Iterator;

import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;

/**
 * A value is anything that is persisted by value, instead of
 * by reference. It is essentially a Hibernate Type, together
 * with zero or more columns. Values are wrapped by things with
 * higher level semantics, for example properties, collections,
 * classes.
 */
public interface IHibernateValueMapping extends Serializable, IPersistentValueMapping {
	public int getColumnSpan();
	public Iterator<IDatabaseColumn> getColumnIterator();
	public Type getType();
	public void setType(Type type);
	public String getFetchMode();
	//akuzmin 15.06.2005
	public void setFetchMode(String fetchMode);	
	public IDatabaseTable getTable();
	public void setTable(IDatabaseTable table);	
	public boolean hasFormula();
	public boolean isAlternateUniqueKey();
	public boolean isSimpleValue();
}
