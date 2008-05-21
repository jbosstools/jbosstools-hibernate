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
package org.jboss.tools.hibernate.internal.core.hibernate.descriptors;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author kaa
 *
 */
public class PropertyMappingForPrimitiveArrayDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance=new PropertyMappingForPrimitiveArrayDescriptorsHolder();
	private static final String GENERAL_CATEGORY=Messages.PropertyMappingForPrimitiveArrayDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.PropertyMappingForPrimitiveArrayDescriptorsHolder_AdvancedCategory;

	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	
	protected PropertyMappingForPrimitiveArrayDescriptorsHolder(){

		PropertyDescriptor pd=new PropertyDescriptor("name",Messages.PropertyMappingForPrimitiveArrayDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForPrimitiveArrayDescriptorsHolder_NameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new EditableListPropertyDescriptor("propertyAccessorName",Messages.PropertyMappingForPrimitiveArrayDescriptorsHolder_PropertyAccessorNameN,OrmConfiguration.ACCESS_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForPrimitiveArrayDescriptorsHolder_PropertyAccessorNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ACCESS);

	}

}
