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
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author kaa - akuzmin@exadel.com
 * Jul 1, 2005
 */
public class VersionMappingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.VersionMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.VersionMappingDescriptorsHolder_AdvancedCategory;	

	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new VersionMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected VersionMappingDescriptorsHolder(IDatabaseTable table){
		PropertyDescriptor pd;
		
		//Spec property descriptor: select database
//		pd=new PropertyDescriptor("tableName","table");
//		pd.setDescription("Database tabl");
//		pd.setCategory(GENERAL_CATEGORY);
//		addPropertyDescriptor(pd);
//		setDefaultPropertyValue(pd.getId(),"");

//		pd=new DBColumnPropertyDescriptor("mappingColumn","column",null,table);
		pd=new PropertyDescriptor("mappingColumn",Messages.VersionMappingDescriptorsHolder_MappingColumnN); //$NON-NLS-1$
		pd.setDescription(Messages.VersionMappingDescriptorsHolder_MappingColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
//		String[] types=new String[Type.getHibernateTypes().length];
//		for(int i=0;i<Type.getHibernateTypes().length;i++)
//		{
//			types[i]=Type.getHibernateTypes()[i].getName();	
//		}
//		pd=new EditableListPropertyDescriptor("typeByString","type",types);
		pd=new PropertyDescriptor("typeByString",Messages.VersionMappingDescriptorsHolder_TypeByStringN); //$NON-NLS-1$
		pd.setDescription(Messages.VersionMappingDescriptorsHolder_TypeByStringD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ID_DATATYPE);

//		pd=new ListPropertyDescriptor("nullValue","unsaved-value",OrmConfiguration.VERSION_UNSAVED_VALUES);
		pd=new PropertyDescriptor("nullValue",Messages.VersionMappingDescriptorsHolder_NullValueN); //$NON-NLS-1$
		pd.setDescription(Messages.VersionMappingDescriptorsHolder_NullValueD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_VERSION_UNSAVED);		
		
	}

}
