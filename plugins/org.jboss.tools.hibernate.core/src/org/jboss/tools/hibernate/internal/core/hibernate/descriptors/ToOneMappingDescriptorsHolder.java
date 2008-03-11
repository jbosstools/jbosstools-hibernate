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
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class ToOneMappingDescriptorsHolder extends SimpleValueMappingDescriptorsHolder{
	private static ToOneMappingDescriptorsHolder instance;
	private static final String ToOne_CATEGORY=Messages.ToOneMappingDescriptorsHolder_ToOneCategory;

	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new ToOneMappingDescriptorsHolder(table);
		return instance;
	}
	
	protected ToOneMappingDescriptorsHolder(IDatabaseTable table){
		super(table);
		PropertyDescriptor pd;

		pd=new TextPropertyDescriptor("fetchMode",Messages.ToOneMappingDescriptorsHolder_FetchModeN); //$NON-NLS-1$
		pd.setDescription(Messages.ToOneMappingDescriptorsHolder_FetchModeD);
		pd.setCategory(ToOne_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("referencedPropertyName",Messages.ToOneMappingDescriptorsHolder_ReferencedPropertyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ToOneMappingDescriptorsHolder_ReferencedPropertyNameD);
		pd.setCategory(ToOne_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new TextPropertyDescriptor("referencedEntityName",Messages.ToOneMappingDescriptorsHolder_ReferencedEntityNameN); //$NON-NLS-1$
		pd.setDescription(Messages.ToOneMappingDescriptorsHolder_ReferencedEntityNameD);
		pd.setCategory(ToOne_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

	}
}
