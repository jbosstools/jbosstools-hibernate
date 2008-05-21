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
 * @author kaa
 * akuzmin@exadel.com
 * Sep 15, 2005
 */
public class KeyPropertyDescriptorsHolder extends PropertyDescriptorsHolder {
	private static KeyPropertyDescriptorsHolder instance;
	private static final String ADVANCED_CATEGORY=Messages.KeyPropertyDescriptorsHolder_AdvancedCategory;

	public static PropertyDescriptorsHolder getInstance(){
		instance=new KeyPropertyDescriptorsHolder();
		return instance;
	}
	
	protected KeyPropertyDescriptorsHolder(){
		PropertyDescriptor pd;

		pd=new TextPropertyDescriptor("foreignKeyName",Messages.KeyPropertyDescriptorsHolder_ForeignKeyNameN); //$NON-NLS-1$
		pd.setDescription(Messages.KeyPropertyDescriptorsHolder_ForeignKeyNameD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("cascadeDeleteEnabled",Messages.KeyPropertyDescriptorsHolder_CascadeDeleteEnabledN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.KeyPropertyDescriptorsHolder_CascadeDeleteEnabledD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("updateable",Messages.KeyPropertyDescriptorsHolder_UpdateableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.KeyPropertyDescriptorsHolder_UpdateableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("nullable",Messages.KeyPropertyDescriptorsHolder_NullableN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.KeyPropertyDescriptorsHolder_NullableD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"true"); //$NON-NLS-1$

		
	}

}
