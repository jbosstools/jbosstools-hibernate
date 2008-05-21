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


/**
 * 
 * @author akuzmin - akuzmin@exadel.com
 * May 26, 2005
 * 
 */
public class PropertyMappingForComponentMapingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance=new PropertyMappingForComponentMapingDescriptorsHolder();
	private static final String GENERAL_CATEGORY=Messages.PropertyMappingForComponentMapingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.PropertyMappingForComponentMapingDescriptorsHolder_AdvancedCategory;

	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	
	protected PropertyMappingForComponentMapingDescriptorsHolder(){

		PropertyDescriptor pd=new PropertyDescriptor("name",Messages.PropertyMappingForComponentMapingDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForComponentMapingDescriptorsHolder_NameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new EditableListPropertyDescriptor("propertyAccessorName",Messages.PropertyMappingForComponentMapingDescriptorsHolder_PropertyAccessorNameN,OrmConfiguration.ACCESS_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForComponentMapingDescriptorsHolder_PropertyAccessorNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ACCESS);

		pd=new ListPropertyDescriptor("updateable",Messages.PropertyMappingForComponentMapingDescriptorsHolder_UpdateableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForComponentMapingDescriptorsHolder_UpdateableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("insertable",Messages.PropertyMappingForComponentMapingDescriptorsHolder_InsertableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForComponentMapingDescriptorsHolder_InsertableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("lazy",Messages.PropertyMappingForComponentMapingDescriptorsHolder_LazyN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForComponentMapingDescriptorsHolder_LazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);

		pd=new ListPropertyDescriptor("optimisticLocked",Messages.PropertyMappingForComponentMapingDescriptorsHolder_OptimisticLockedN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForComponentMapingDescriptorsHolder_OptimisticLockedD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("unique",Messages.PropertyMappingForComponentMapingDescriptorsHolder_UniqueN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForComponentMapingDescriptorsHolder_UniqueD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
	}

}
