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


public class PropertyMappingForOnetoOneMapingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance=new PropertyMappingForOnetoOneMapingDescriptorsHolder();
	private static final String GENERAL_CATEGORY=Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_AdvancedCategory;

	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	
	protected PropertyMappingForOnetoOneMapingDescriptorsHolder(){

		PropertyDescriptor pd=new PropertyDescriptor("name",Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_NameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		
		pd=new ListPropertyDescriptor("toOneLazy",Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_ToOneLazyN,OrmConfiguration.ASSOCIATIONS_LAZY_VALUES); // changed by Nick 20.09.2005 //$NON-NLS-1$
//		String[] lazy={"true","proxy","false"};		
//		pd=new ListPropertyDescriptor("lazy","lazy",lazy);
		pd.setDescription(Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_ToOneLazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.ASSOCIATIONS_DEFAULT_LAZY); // changed by Nick 20.09.2005
		
		pd=new EditableListPropertyDescriptor("propertyAccessorName",Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_PropertyAccessorNameN,OrmConfiguration.ACCESS_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_PropertyAccessorNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ACCESS);
		
		pd=new EditableListPropertyDescriptor("cascade",Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_CascadeN,OrmConfiguration.CASCADE_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.PropertyMappingForOnetoOneMapingDescriptorsHolder_CascadeD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_CASCADE);				
	}

}
