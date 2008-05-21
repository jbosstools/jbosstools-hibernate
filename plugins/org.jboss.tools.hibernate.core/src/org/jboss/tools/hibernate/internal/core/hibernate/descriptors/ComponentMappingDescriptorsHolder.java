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
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PersistentClassPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class ComponentMappingDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance;
	protected static final String GENERAL_CATEGORY=Messages.ComponentMappingDescriptorsHolder_GeneralCategory;
	private static final String ADVANCED_CATEGORY=Messages.ComponentMappingDescriptorsHolder_AdvancedCategory;	
	public static PropertyDescriptorsHolder getInstance(IMapping mod){
		instance=new ComponentMappingDescriptorsHolder(mod);
		return instance;
	}
	
	protected ComponentMappingDescriptorsHolder(IMapping mod){
		PropertyDescriptor pd;
		
		pd=new PersistentClassPropertyDescriptor("componentClassName",Messages.ComponentMappingDescriptorsHolder_ComponentClassNameN,null,mod,false); //$NON-NLS-1$
		pd.setDescription(Messages.ComponentMappingDescriptorsHolder_ComponentClassNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new ListPropertyDescriptor("dynamic",Messages.ComponentMappingDescriptorsHolder_DynamicN,OrmConfiguration.BOOLEAN_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.ComponentMappingDescriptorsHolder_DynamicD);
		addPropertyDescriptor(pd);
		pd.setCategory(ADVANCED_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);

		pd=new TextPropertyDescriptor("parentProperty",Messages.ComponentMappingDescriptorsHolder_ParentPropertyN); //$NON-NLS-1$
		pd.setDescription(Messages.ComponentMappingDescriptorsHolder_ParentPropertyD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$

//		pd=new ListPropertyDescriptor("embedded","unique",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("Specifies that a unique constraint exists upon all mapped columns of the component");
//		addPropertyDescriptor(pd);
//		pd.setCategory(ADVANCED_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),OrmConfiguration.BOOLEAN_VALUES[0]);
		
	}

}
