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


public class ManyToOneMappingDescriptorsHolder extends PropertyDescriptorsHolder{
	private static ManyToOneMappingDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.ManyToOneMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.ManyToOneMappingDescriptorsHolder_AdvancedCategory;
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new ManyToOneMappingDescriptorsHolder(table);
		return instance;
	}
	
	private ManyToOneMappingDescriptorsHolder(IDatabaseTable table){
	
		PropertyDescriptor pd;
		pd=new PersistentClassPropertyDescriptor("referencedEntityName",Messages.ManyToOneMappingDescriptorsHolder_ReferencedEntityNameN, null, table.getSchema().getProjectMapping(),false); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToOneMappingDescriptorsHolder_ReferencedEntityNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

		pd=new DialogDBColumnPropertyDescriptor("mappingColumn",Messages.ManyToOneMappingDescriptorsHolder_MappingColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToOneMappingDescriptorsHolder_MappingColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
		String[] fetch={"join","select"};  //$NON-NLS-1$ //$NON-NLS-2$
		pd=new ListPropertyDescriptor("fetchMode",Messages.ManyToOneMappingDescriptorsHolder_FetchModeN,fetch); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToOneMappingDescriptorsHolder_FetchModeD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),fetch[1]);

		pd=new DialogTextPropertyDescriptor("formula",Messages.ManyToOneMappingDescriptorsHolder_FormulaN); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToOneMappingDescriptorsHolder_FormulaD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("referencedPropertyName",Messages.ManyToOneMappingDescriptorsHolder_ReferencedPropertyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToOneMappingDescriptorsHolder_ReferencedPropertyNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		String[] not_found={"exception","ignore"};  //$NON-NLS-1$ //$NON-NLS-2$
		pd=new ListPropertyDescriptor("ignoreNotFound",Messages.ManyToOneMappingDescriptorsHolder_IgnoreNotFoundN,not_found,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToOneMappingDescriptorsHolder_IgnoreNotFoundD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[1]);

		pd=new TextPropertyDescriptor("foreignKeyName",Messages.ManyToOneMappingDescriptorsHolder_ForeignKeyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ManyToOneMappingDescriptorsHolder_ForeignKeyNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
	}

}
