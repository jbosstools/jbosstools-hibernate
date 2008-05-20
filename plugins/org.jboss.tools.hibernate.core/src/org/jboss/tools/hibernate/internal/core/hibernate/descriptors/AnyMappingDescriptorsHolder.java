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
import org.jboss.tools.hibernate.internal.core.properties.DBColumnPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;

/**
 * 
 * @author akuzmin - akuzmin@exadel.com
 * 
 */
public class AnyMappingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance;
	protected static final String GENERAL_CATEGORY=Messages.AnyMappingDescriptorsHolder_GeneralCategory;
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new AnyMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected AnyMappingDescriptorsHolder(IDatabaseTable table){
		PropertyDescriptor pd;
		
		String[] types=new String[Type.getHibernateTypes().length];
		for(int i=0;i<Type.getHibernateTypes().length;i++)
		{
			types[i]=Type.getHibernateTypes()[i].getName();	
		}

		pd=new EditableListPropertyDescriptor("metaType",Messages.AnyMappingDescriptorsHolder_MetaTypeN,types); //$NON-NLS-1$
		pd.setDescription(Messages.AnyMappingDescriptorsHolder_MetaTypeD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"string"); //$NON-NLS-1$

		pd=new EditableListPropertyDescriptor("identifierType",Messages.AnyMappingDescriptorsHolder_IdentifierTypeN,types); //$NON-NLS-1$
		pd.setDescription(Messages.AnyMappingDescriptorsHolder_IdentifierTypeD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"class");		 //$NON-NLS-1$
		
		pd=new DBColumnPropertyDescriptor("mappingColumn",Messages.AnyMappingDescriptorsHolder_MappingColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.AnyMappingDescriptorsHolder_MappingColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);

		pd=new DBColumnPropertyDescriptor("metaTypeColumn",Messages.AnyMappingDescriptorsHolder_MetaTypeColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.AnyMappingDescriptorsHolder_MetaTypeColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
	}
}
