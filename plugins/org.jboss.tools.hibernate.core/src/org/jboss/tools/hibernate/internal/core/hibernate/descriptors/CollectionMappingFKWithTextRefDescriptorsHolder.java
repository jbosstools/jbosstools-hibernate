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


/**
 * @author kaa
 * akuzmin@exadel.com
 * Oct 2, 2005
 */
public class CollectionMappingFKWithTextRefDescriptorsHolder extends
		CollectionMappingFKDescriptorsHolder {
	private static final String ADVANCED_CATEGORY=Messages.CollectionMappingFKWithTextRefDescriptorsHolder_AdvancedCategory;
	private static CollectionMappingFKWithTextRefDescriptorsHolder instance;
	public static PropertyDescriptorsHolder getInstance(IDatabaseTable table){
		instance=new CollectionMappingFKWithTextRefDescriptorsHolder(table);
		return instance;
	}

	protected CollectionMappingFKWithTextRefDescriptorsHolder(IDatabaseTable table) {
		super(table);

		PropertyDescriptor pd;
		
		pd=new TextPropertyDescriptor("referencedPropertyName",Messages.CollectionMappingFKWithTextRefDescriptorsHolder_ReferencedPropertyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.CollectionMappingFKWithTextRefDescriptorsHolder_ReferencedPropertyNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
	
	}

}
