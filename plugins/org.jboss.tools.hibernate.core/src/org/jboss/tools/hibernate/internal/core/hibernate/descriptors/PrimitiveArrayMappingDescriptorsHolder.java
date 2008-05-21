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
import org.jboss.tools.hibernate.internal.core.properties.DBColumnPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author kaa
 *
 */
public class PrimitiveArrayMappingDescriptorsHolder extends CollectionBaseMappingDescriptorsHolder {
	private static CollectionBaseMappingDescriptorsHolder instance;
	private static final String INDEX_CATEGORY=Messages.PrimitiveArrayMappingDescriptorsHolder_IndexCategory;	
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new PrimitiveArrayMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected PrimitiveArrayMappingDescriptorsHolder(IDatabaseTable table){
		super();
		
		PropertyDescriptor pd=new TextPropertyDescriptor("baseIndex",Messages.PrimitiveArrayMappingDescriptorsHolder_BaseIndexN); //$NON-NLS-1$
		pd.setDescription(Messages.PrimitiveArrayMappingDescriptorsHolder_BaseIndexD);
		pd.setCategory(INDEX_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new DBColumnPropertyDescriptor("index.mappingColumn",Messages.PrimitiveArrayMappingDescriptorsHolder_IndexMappingColumnN,null,table); //$NON-NLS-1$
		pd.setDescription(Messages.PrimitiveArrayMappingDescriptorsHolder_IndexMappingColumnD);
		pd.setCategory(INDEX_CATEGORY);
		addPropertyDescriptor(pd);
		
	}
}
