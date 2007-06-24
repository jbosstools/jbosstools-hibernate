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
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * 
 * @author akuzmin - akuzmin@exadel.com
 * Jule 7, 2005
 * 
 */
public class IdBagMappingDescriptorsHolder extends CollectionBaseMappingDescriptorsHolder {
	private static CollectionBaseMappingDescriptorsHolder instance;
	private static final String ADVANCED_CATEGORY=Messages.IdBagMappingDescriptorsHolder_AdvancedCategory;
	public static PropertyDescriptorsHolder getInstance(){
		instance=new IdBagMappingDescriptorsHolder();
		return instance;
	}
	
	protected IdBagMappingDescriptorsHolder(){
		super();
		PropertyDescriptor pd;

		pd=new TextPropertyDescriptor("orderBy",Messages.IdBagMappingDescriptorsHolder_OrderByN); //$NON-NLS-1$
		pd.setDescription(Messages.IdBagMappingDescriptorsHolder_OrderByD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("lazy",Messages.IdBagMappingDescriptorsHolder_LazyN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.IdBagMappingDescriptorsHolder_LazyD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"false"); //$NON-NLS-1$
		
	}


}
