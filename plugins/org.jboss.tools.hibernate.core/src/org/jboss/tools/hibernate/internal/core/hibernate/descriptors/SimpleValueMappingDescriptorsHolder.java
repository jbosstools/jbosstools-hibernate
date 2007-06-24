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
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.DBColumnPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class SimpleValueMappingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.SimpleValueMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.SimpleValueMappingDescriptorsHolder_AdvancedlCategory;
	
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new SimpleValueMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected SimpleValueMappingDescriptorsHolder(IDatabaseTable table){
		PropertyDescriptor pd;
		
		//Spec property descriptor: select database
		pd=new PropertyDescriptor("tableName",Messages.SimpleValueMappingDescriptorsHolder_TableNameN); //$NON-NLS-1$
		pd.setDescription(Messages.SimpleValueMappingDescriptorsHolder_TableNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new DBColumnPropertyDescriptor("mappingColumn",Messages.SimpleValueMappingDescriptorsHolder_MappingColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.SimpleValueMappingDescriptorsHolder_MappingColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
		
		pd=new DialogTextPropertyDescriptor("formula",Messages.SimpleValueMappingDescriptorsHolder_FormulaN);		 //$NON-NLS-1$
		pd.setDescription(Messages.SimpleValueMappingDescriptorsHolder_FormulaD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

		String[] types=new String[Type.getHibernateTypes().length];
		for(int i=0;i<Type.getHibernateTypes().length;i++)
		{
			types[i]=Type.getHibernateTypes()[i].getName();	
		}
		pd=new EditableListPropertyDescriptor("typeByString",Messages.SimpleValueMappingDescriptorsHolder_TypeByStringN,types); //$NON-NLS-1$
		pd.setDescription(Messages.SimpleValueMappingDescriptorsHolder_TypeByStringD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ID_DATATYPE);

	}
	}
