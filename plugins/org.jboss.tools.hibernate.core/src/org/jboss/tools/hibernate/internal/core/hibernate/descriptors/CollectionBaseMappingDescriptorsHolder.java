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
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;

/**
 * @author akuzmin@exadel.com
 * Jul 6, 2005
 */
public class CollectionBaseMappingDescriptorsHolder extends PropertyDescriptorsHolder {

	private static final String ADVANCED_CATEGORY=Messages.CollectionBaseMappingDescriptorsHolder_AdvancedCategory;

	protected CollectionBaseMappingDescriptorsHolder(){
		PropertyDescriptor pd;

		pd=new DialogTextPropertyDescriptor("subselect",Messages.CollectionBaseMappingDescriptorsHolder_SubselectN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_SubselectD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("where",Messages.CollectionBaseMappingDescriptorsHolder_WhereN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_WhereD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
	
		pd=new TextPropertyDescriptor("persister",Messages.CollectionBaseMappingDescriptorsHolder_PersisterN); ///???? //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_PersisterD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new TextPropertyDescriptor("check",Messages.CollectionBaseMappingDescriptorsHolder_CheckN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_CheckD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("synchronizedTables",Messages.CollectionBaseMappingDescriptorsHolder_SynchronizedTablesN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_SynchronizedTablesD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new TextPropertyDescriptor("loaderName",Messages.CollectionBaseMappingDescriptorsHolder_LoaderNameN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_LoaderNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
//		String[] dep={"keyColumn"};
//		if (pfm.getOwnerClass()!=null)
//		{
//		pd=new DBTablePropertyDescriptor("collectionTable","table",null,pfm.getOwnerClass().getPersistentClassMapping(),pfm);
//		pd.setDescription("the name of the database collection table.");
//		pd.setCategory(GENERAL_CATEGORY);
//		addPropertyDescriptor(pd);
//		}
//		else 
//		{
//			ExceptionHandler.logInfo("Field "+pfm.getName()+" hasn't owner class");	
//		}
		
//		pd=new DBColumnPropertyDescriptor("keyColumn","Key Column",null,table);
//		pd.setDescription("Key Column");
//		pd.setCategory(ADVANCED_CATEGORY);
//		addPropertyDescriptor(pd);

//
		pd=new TextPropertyDescriptor("batchSize",Messages.CollectionBaseMappingDescriptorsHolder_BatchSizeN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_BatchSizeD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"1");		 //$NON-NLS-1$

		String[] fetch={"join","select","subselect"};  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pd=new ListPropertyDescriptor("fetchMode",Messages.CollectionBaseMappingDescriptorsHolder_FetchModeN,fetch); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_FetchModeD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),fetch[1]);
		
		pd=new ListPropertyDescriptor("optimisticLocked",Messages.CollectionBaseMappingDescriptorsHolder_OptimisticLockedN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionBaseMappingDescriptorsHolder_OptimisticLockedD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
	}

}
