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
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class PropertyMappingForSimpleMapingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance=new PropertyMappingForSimpleMapingDescriptorsHolder();
	private static final String GENERAL_CATEGORY=Messages.PropertyMappingForSimpleMapingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.PropertyMappingForSimpleMapingDescriptorsHolder_AdvancedCategory;

	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	
	protected PropertyMappingForSimpleMapingDescriptorsHolder(){

		PropertyDescriptor pd=new PropertyDescriptor("name",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_NameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("updateable",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_UpdateableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_UpdateableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("insertable",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_InsertableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_InsertableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("optimisticLocked",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_OptimisticLockedN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_OptimisticLockedD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("lazy",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_LazyN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_LazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
		
		pd=new EditableListPropertyDescriptor("propertyAccessorName",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_PropertyAccessorNameN,OrmConfiguration.ACCESS_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_PropertyAccessorNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ACCESS);

		pd=new ListPropertyDescriptor("notNull",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_NotNullN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_NotNullD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("unique",Messages.PropertyMappingForSimpleMapingDescriptorsHolder_UniqueN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForSimpleMapingDescriptorsHolder_UniqueD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
	}
}
