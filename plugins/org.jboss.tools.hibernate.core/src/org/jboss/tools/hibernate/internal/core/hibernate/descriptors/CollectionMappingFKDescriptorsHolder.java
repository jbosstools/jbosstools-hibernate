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
import org.jboss.tools.hibernate.internal.core.properties.DialogDBColumnPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * 
 * @author akuzmin - akuzmin@exadel.com
 * May 30, 2005
 * 
 */
public class CollectionMappingFKDescriptorsHolder extends PropertyDescriptorsHolder {
	private static CollectionMappingFKDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.CollectionMappingFKDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.CollectionMappingFKDescriptorsHolder_AdvancedCategory;

	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new CollectionMappingFKDescriptorsHolder(table);
		return instance;
	}
	
	protected CollectionMappingFKDescriptorsHolder(IDatabaseTable table){
		PropertyDescriptor pd;

//		pd=new DBColumnPropertyDescriptor("keyColumn","column",null,table);
		pd=new DialogDBColumnPropertyDescriptor("keyColumn",Messages.CollectionMappingFKDescriptorsHolder_KeyColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingFKDescriptorsHolder_KeyColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);

		pd=new PropertyDescriptor("key.tableName",Messages.CollectionMappingFKDescriptorsHolder_KeyTableNameN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingFKDescriptorsHolder_KeyTableNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("key.foreignKeyName",Messages.CollectionMappingFKDescriptorsHolder_KeyForeignKeyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingFKDescriptorsHolder_KeyForeignKeyNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("key.cascadeDeleteEnabled",Messages.CollectionMappingFKDescriptorsHolder_KeyCascadeDeleteEnabledN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingFKDescriptorsHolder_KeyCascadeDeleteEnabledD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("key.updateable",Messages.CollectionMappingFKDescriptorsHolder_KeyUpdateableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingFKDescriptorsHolder_KeyUpdateableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("key.nullable",Messages.CollectionMappingFKDescriptorsHolder_KeyNullableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingFKDescriptorsHolder_KeyNullableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

	}

}
