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
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author kaa
 *
 */
public class BagMappingDescriptorsHolder extends CollectionBaseMappingDescriptorsHolder {
	private static CollectionBaseMappingDescriptorsHolder instance;
	private static final String ADVANCED_CATEGORY=Messages.BagMappingDescriptorsHolder_AdvancedCategory;
	private static final String GENERAL_CATEGORY=Messages.BagMappingDescriptorsHolder_GeneralCategory;
	public static PropertyDescriptorsHolder getInstance(){
		instance=new BagMappingDescriptorsHolder();
		return instance;
	}
	
	protected BagMappingDescriptorsHolder(){
		super();
		PropertyDescriptor pd;

		pd=new TextPropertyDescriptor("orderBy",Messages.BagMappingDescriptorsHolder_OrderByN); //$NON-NLS-1$
		pd.setDescription(Messages.BagMappingDescriptorsHolder_OrderByD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("inverse",Messages.BagMappingDescriptorsHolder_InverseN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.BagMappingDescriptorsHolder_InverseD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("lazy",Messages.BagMappingDescriptorsHolder_LazyN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.BagMappingDescriptorsHolder_LazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
	}



}
