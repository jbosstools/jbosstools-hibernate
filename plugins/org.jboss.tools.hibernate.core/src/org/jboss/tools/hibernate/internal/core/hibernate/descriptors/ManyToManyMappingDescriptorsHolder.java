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
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PersistentClassPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class ManyToManyMappingDescriptorsHolder extends PropertyDescriptorsHolder{
	private static ManyToManyMappingDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.ManyToManyMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.ManyToManyMappingDescriptorsHolder_AdvancedCategory;
	
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new ManyToManyMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected ManyToManyMappingDescriptorsHolder(IDatabaseTable table){
		PropertyDescriptor pd;
		pd=new PersistentClassPropertyDescriptor("referencedEntityName",Messages.ManyToManyMappingDescriptorsHolder_ReferencedEntityNameN, null, table.getSchema().getProjectMapping(),false); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToManyMappingDescriptorsHolder_ReferencedEntityNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new DialogDBColumnPropertyDescriptor("mappingColumn",Messages.ManyToManyMappingDescriptorsHolder_MappingColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToManyMappingDescriptorsHolder_MappingColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);

		pd=new DialogTextPropertyDescriptor("formula",Messages.ManyToManyMappingDescriptorsHolder_FormulaN); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToManyMappingDescriptorsHolder_FormulaD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

		String[] not_found={"exception","ignore"};  //$NON-NLS-1$ //$NON-NLS-2$
		pd=new ListPropertyDescriptor("ignoreNotFound",Messages.ManyToManyMappingDescriptorsHolder_IgnoreNotFoundN,not_found,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToManyMappingDescriptorsHolder_IgnoreNotFoundD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[1]);
		
		String[] fetch={"join","select"};  //$NON-NLS-1$ //$NON-NLS-2$
		pd=new ListPropertyDescriptor("fetchMode",Messages.ManyToManyMappingDescriptorsHolder_FetchModeN,fetch); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToManyMappingDescriptorsHolder_FetchModeD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),fetch[1]);

		pd=new TextPropertyDescriptor("foreignKeyName",Messages.ManyToManyMappingDescriptorsHolder_ForeignKeyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToManyMappingDescriptorsHolder_ForeignKeyNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
	}

}
