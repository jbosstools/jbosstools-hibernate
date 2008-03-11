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
package org.jboss.tools.hibernate.core;

import java.util.Iterator;

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.hibernate.IHibernateKeyMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;


/**
 * @author alex
 *
 * An interface of a single class mapping.
 */
public interface IPersistentClassMapping extends IOrmElement {
	public IPersistentClass getPersistentClass();
	
	public boolean isIncludeSuperFields();
	
	public void setPersistentClass(IPersistentClass clazz);
	public IDatabaseTable getDatabaseTable();
	public void setDatabaseTable(IDatabaseTable table);
	public IMappingStorage getStorage();
	public void setStorage(IMappingStorage storage);
	public Iterator<IPropertyMapping> getFieldMappingIterator();
	public IPersistentClassMapping getSuperclassMapping();
	//akuzmin 15/03/2005	
	public IPropertySource2 getPropertySource();	
	public void deleteFieldMapping(String fieldName);
	public void renameFieldMapping(IPersistentFieldMapping fm, String newFieldName);
	public void clear();
    // added by Nick 02.08.2005
    public IHibernateKeyMapping getIdentifier();
    // by Nick

}
