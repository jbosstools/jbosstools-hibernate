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
import org.jboss.tools.hibernate.internal.core.properties.EditableListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author kaa - akuzmin@exadel.com
 * 07.07.2005
 */
public class IdBagIdentifireMappingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static IdBagIdentifireMappingDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.IdBagIdentifireMappingDescriptorsHolder_GeneralCategory;

	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new IdBagIdentifireMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected IdBagIdentifireMappingDescriptorsHolder(IDatabaseTable table){
		PropertyDescriptor pd;

		pd=new DBColumnPropertyDescriptor("identifierColumn",Messages.IdBagIdentifireMappingDescriptorsHolder_IdentifierColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.IdBagIdentifireMappingDescriptorsHolder_IdentifierColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);

		String[] types=new String[Type.getHibernateTypes().length];
		for(int i=0;i<Type.getHibernateTypes().length;i++)
		{
			types[i]=Type.getHibernateTypes()[i].getName();	
		}
		pd=new EditableListPropertyDescriptor("typeByString",Messages.IdBagIdentifireMappingDescriptorsHolder_TypeByStringN,types); //$NON-NLS-1$
		pd.setDescription(Messages.IdBagIdentifireMappingDescriptorsHolder_TypeByStringD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ID_DATATYPE);
		
		
		pd=new PropertyDescriptor("generatorStrategy",Messages.IdBagIdentifireMappingDescriptorsHolder_GeneratorStrategyN); //$NON-NLS-1$
		pd.setDescription(Messages.IdBagIdentifireMappingDescriptorsHolder_GeneratorStrategyD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
	}

}
