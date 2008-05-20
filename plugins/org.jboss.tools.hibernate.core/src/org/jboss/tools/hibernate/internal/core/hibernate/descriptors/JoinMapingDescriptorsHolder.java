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
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * 
 * @author akuzmin - akuzmin@exadel.com
 * Jun 5, 2005
 * 
 */
public class JoinMapingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance=new JoinMapingDescriptorsHolder();
	private static final String GENERAL_CATEGORY=Messages.JoinMapingDescriptorsHolder_GeneralCategory;

	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	
	protected JoinMapingDescriptorsHolder(){

		PropertyDescriptor pd=new PropertyDescriptor("name",Messages.JoinMapingDescriptorsHolder_NameN); //$NON-NLS-1$
		pd.setDescription(Messages.JoinMapingDescriptorsHolder_NameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

		pd=new ListPropertyDescriptor("inverse",Messages.JoinMapingDescriptorsHolder_InverseN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.JoinMapingDescriptorsHolder_InverseD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);
		
		pd=new ListPropertyDescriptor("optional",Messages.JoinMapingDescriptorsHolder_OptionalN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.JoinMapingDescriptorsHolder_OptionalD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);
		
		pd=new PropertyDescriptor("table.name",Messages.JoinMapingDescriptorsHolder_TableNameN); //$NON-NLS-1$
		pd.setDescription(Messages.JoinMapingDescriptorsHolder_TableNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
	}

}
