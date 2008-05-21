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
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class CollectionMappingDescriptorsHolder extends CollectionBaseMappingDescriptorsHolder {
	private static CollectionMappingDescriptorsHolder instance;
	private static final String ADVANCED_CATEGORY=Messages.CollectionMappingDescriptorsHolder_AdvancedCategory;
	private static final String GENERAL_CATEGORY=Messages.CollectionMappingDescriptorsHolder_GeneralCategory;
	public static PropertyDescriptorsHolder getInstance(){
		instance=new CollectionMappingDescriptorsHolder();
		return instance;
	}
	
	
	protected CollectionMappingDescriptorsHolder(){
		super();
		PropertyDescriptor pd;

		pd=new TextPropertyDescriptor("orderBy",Messages.CollectionMappingDescriptorsHolder_OrderByN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingDescriptorsHolder_OrderByD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		String[] sorts={"unsorted","natural"};		 //$NON-NLS-1$ //$NON-NLS-2$
		pd=new EditableListPropertyDescriptor("sort",Messages.CollectionMappingDescriptorsHolder_SortN,sorts); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingDescriptorsHolder_SortD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"unsorted"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("inverse",Messages.CollectionMappingDescriptorsHolder_InverseN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingDescriptorsHolder_InverseD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("lazy",Messages.CollectionMappingDescriptorsHolder_LazyN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingDescriptorsHolder_LazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
	}

}
