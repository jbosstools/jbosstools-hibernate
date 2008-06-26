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
package org.jboss.tools.hibernate.internal.core.properties;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 21, 2005
 */
public class ParentListPropertyDescriptor extends ListPropertyDescriptor
		implements IAutoChangebleParentPropertyDescriptor {

private String dependentProperties[]={};

public ParentListPropertyDescriptor(Object id, String displayName, String[] viewValues, Object[] propertyValues,String[] dependentProperties) {
		super(id, displayName, viewValues, propertyValues);
		this.dependentProperties=dependentProperties;
	}

	public ParentListPropertyDescriptor(Object id, String displayName, String[] propertyValues,String[] dependentProperties) {
		super(id, displayName, propertyValues);
		this.dependentProperties=dependentProperties;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.properties.IAutoChangebleParentPropertyDescriptor#getDependentProperties()
	 */
	public String[] getDependentProperties() {
		return dependentProperties;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.properties.IAutoChangebleParentPropertyDescriptor#autoChangeModel()
	 */
	public boolean autoChangeModel() {
		return false;
	}

}
