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


public class PropertyMappingForManytoOneMapingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance=new PropertyMappingForManytoOneMapingDescriptorsHolder();
	private static final String GENERAL_CATEGORY=Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_AdvancedCategory;

	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	
	protected PropertyMappingForManytoOneMapingDescriptorsHolder(){

		PropertyDescriptor pd=new PropertyDescriptor("name",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_NameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("updateable",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_UpdateableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_UpdateableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("insertable",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_InsertableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_InsertableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("optimisticLocked",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_OptimisticLockedN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_OptimisticLockedD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("toOneLazy",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_ToOneLazyN,OrmConfiguration.ASSOCIATIONS_LAZY_VALUES); // changed by Nick 20.09.2005 //$NON-NLS-1$
//		String[] lazy={"true","proxy","false"};		
//		pd=new ListPropertyDescriptor("lazy","lazy",lazy);
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_ToOneLazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.ASSOCIATIONS_DEFAULT_LAZY); // changed by Nick 20.09.2005
		
		pd=new EditableListPropertyDescriptor("propertyAccessorName",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_PropertyAccessorNameN,OrmConfiguration.ACCESS_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_PropertyAccessorNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		
		pd=new EditableListPropertyDescriptor("cascade",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_CascadeN,OrmConfiguration.CASCADE_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_CascadeD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_CASCADE);				
		
		pd=new ListPropertyDescriptor("notNull",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_NotNullN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_NotNullD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("unique",Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_UniqueN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForManytoOneMapingDescriptorsHolder_UniqueD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		
	}

}
