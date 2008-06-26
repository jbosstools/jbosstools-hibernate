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

import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.IPersistentValueMapping;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;

public abstract class AbstractValueMapping extends AbstractOrmElement implements
		IPersistentValueMapping {

	private IPersistentFieldMapping fieldMapping;

	public IPersistentFieldMapping getFieldMapping() {
		return fieldMapping;
	}

	/*
	 * @see org.jboss.tools.hibernate.core.IPersistentValueMapping#setFieldMapping(org.jboss.tools.hibernate.core.IPersistentFieldMapping)
	 */
	public void setFieldMapping(IPersistentFieldMapping mapping) {
		fieldMapping = mapping;
	}

//	akuzmin 21/04/2005	
	public abstract IPropertySource2 getPropertySource();
//	akuzmin 05/05/2005
	public abstract PropertyDescriptorsHolder getPropertyDescriptorHolder();
//	akuzmin 24/05/2005	
	public abstract PropertyDescriptorsHolder getPropertyMappingDescriptorHolder();

	/*
	 * @see org.jboss.tools.hibernate.core.IPersistentValueMapping#update()
	 */
	public void update() {		
	}
	
}
