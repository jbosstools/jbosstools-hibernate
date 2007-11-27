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
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PersistentClassPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class OneToManyMappingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance;
	private static final String GENERAL_CATEGORY=Messages.OneToManyMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.OneToManyMappingDescriptorsHolder_AdvancedCategory;
	
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new OneToManyMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected OneToManyMappingDescriptorsHolder(IDatabaseTable table){
		IMapping mod=null;
		if ((table!=null)&&(table.getSchema()!=null))
			mod=table.getSchema().getProjectMapping();
		PropertyDescriptor pd=new PersistentClassPropertyDescriptor("referencedEntityName",Messages.OneToManyMappingDescriptorsHolder_ReferencedEntityNameN, null, mod,true); //$NON-NLS-1$
		pd.setDescription(Messages.OneToManyMappingDescriptorsHolder_ReferencedEntityNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

		String[] not_found={"exception","ignore"};  //$NON-NLS-1$ //$NON-NLS-2$
		pd=new ListPropertyDescriptor("ignoreNotFound",Messages.OneToManyMappingDescriptorsHolder_IgnoreNotFoundN,not_found,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.OneToManyMappingDescriptorsHolder_IgnoreNotFoundD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[1]);
		
	}
}
