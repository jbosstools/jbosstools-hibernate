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

import java.util.Iterator;

import org.jboss.tools.hibernate.core.IPersistentClass;



/**
 * @author alex
 *
 */
public interface IComponentMapping extends ISimpleValueMapping/* 9.06.2005 by Nick*/, IPropertyMappingHolder /* by Nick */ {
	
	//added 06/07/05 by alex
	public IPersistentClass getComponentClass();
	
	public String getComponentClassName();

	public String getParentProperty();

	public void setComponentClassName(String componentClass);

	public void setParentProperty(String parentProperty);

	public boolean isDynamic();

	public void setDynamic(boolean dynamic);

	public java.util.Map getMetaAttributes();

	public IMetaAttribute getMetaAttribute(String attributeName);

	public void setMetaAttributes(java.util.Map metas);
	
	public Iterator<IPropertyMapping> getPropertyIterator();
	
	public void addProperty(IPropertyMapping p);
//	akuzmin 17.05.2005	
	public void removeProperty(IPropertyMapping p);

// added by yk 13.07.2005
	public boolean 	isEmbedded();
	public void 	setEmbedded(boolean embedded);
	public boolean 	isProperties_component();
	public void 	setProperties_component(boolean properties_component);
// added by yk 13.07.2005 stop
	
}