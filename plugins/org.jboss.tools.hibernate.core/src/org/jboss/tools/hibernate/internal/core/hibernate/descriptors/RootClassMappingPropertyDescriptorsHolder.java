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
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.DBTableEditablePropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class RootClassMappingPropertyDescriptorsHolder extends ClassMappingPropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance; 
	public static PropertyDescriptorsHolder getInstance(IPersistentClassMapping pcm){
		instance=new RootClassMappingPropertyDescriptorsHolder(pcm);
		return instance;
	}
	
	private RootClassMappingPropertyDescriptorsHolder(IPersistentClassMapping pcm){
		super(pcm);
		PropertyDescriptor pd;		
		//Spec property descriptor: select database
//		pd=new DBTablePropertyDescriptor("databaseTable","Table",null,pcm);
//		pd.setDescription("Database tabl");
//		pd.setCategory(GENERAL_CATEGORY);
//		addPropertyDescriptor(pd);
		pd=new DBTableEditablePropertyDescriptor("databaseTableByName",Messages.RootClassMappingPropertyDescriptorsHolder_DatabaseTableByNameN,null,pcm); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_DatabaseTableByNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
		
//		setDefaultPropertyValue(pd.getId(),"databaseTable");
		
		pd=new ListPropertyDescriptor("mutable",Messages.RootClassMappingPropertyDescriptorsHolder_MutableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_MutableD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);

		pd=new ListPropertyDescriptor("explicitPolymorphism",Messages.RootClassMappingPropertyDescriptorsHolder_ExplicitPolymorphismN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_ExplicitPolymorphismD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		
		pd=new TextPropertyDescriptor("where",Messages.RootClassMappingPropertyDescriptorsHolder_WhereN); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_WhereD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		
		pd=new TextPropertyDescriptor("discriminatorValue",Messages.RootClassMappingPropertyDescriptorsHolder_DiscriminatorValueN); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_DiscriminatorValueD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("optimisticLockMode",Messages.RootClassMappingPropertyDescriptorsHolder_OptimisticLockModeN, OrmConfiguration.OPTIMISTIC_STRATEGY_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_OptimisticLockModeD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.CHECK_VERSION);		

		pd=new TextPropertyDescriptor("check",Messages.RootClassMappingPropertyDescriptorsHolder_CheckN); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_CheckD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("rowId",Messages.RootClassMappingPropertyDescriptorsHolder_RowIdN); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_RowIdD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

		pd=new DialogTextPropertyDescriptor("subselect",Messages.RootClassMappingPropertyDescriptorsHolder_SubselectN); //$NON-NLS-1$
		pd.setDescription(Messages.RootClassMappingPropertyDescriptorsHolder_SubselectD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

	
	}
}
