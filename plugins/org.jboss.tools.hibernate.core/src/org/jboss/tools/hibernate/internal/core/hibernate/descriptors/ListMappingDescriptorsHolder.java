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
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.DBColumnPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * 
 * @author akuzmin - akuzmin@exadel.com
 * May 30, 2005
 * 
 */
public class ListMappingDescriptorsHolder extends CollectionBaseMappingDescriptorsHolder {
	private static CollectionBaseMappingDescriptorsHolder instance;
	private static final String INDEX_CATEGORY=Messages.ListMappingDescriptorsHolder_IndexCategory;
	private static final String GENERAL_CATEGORY=Messages.ListMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.ListMappingDescriptorsHolder_AdvancedCategory;	
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new ListMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected ListMappingDescriptorsHolder(IDatabaseTable table){
		super();
		PropertyDescriptor pd=new TextPropertyDescriptor("baseIndex",Messages.ListMappingDescriptorsHolder_BaseIndexN); //$NON-NLS-1$
		pd.setDescription(Messages.ListMappingDescriptorsHolder_BaseIndexD);
		pd.setCategory(INDEX_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new DBColumnPropertyDescriptor("index.mappingColumn",Messages.ListMappingDescriptorsHolder_IndexMappingColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.ListMappingDescriptorsHolder_IndexMappingColumnD);
		pd.setCategory(INDEX_CATEGORY);
		addPropertyDescriptor(pd);
		
		pd=new ListPropertyDescriptor("inverse",Messages.ListMappingDescriptorsHolder_InverseN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ListMappingDescriptorsHolder_InverseD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("lazy",Messages.ListMappingDescriptorsHolder_LazyN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ListMappingDescriptorsHolder_LazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
	}

}
