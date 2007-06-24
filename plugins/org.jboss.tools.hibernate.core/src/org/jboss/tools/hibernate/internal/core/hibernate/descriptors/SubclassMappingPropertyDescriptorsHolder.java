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
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class SubclassMappingPropertyDescriptorsHolder extends ClassMappingPropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance; 
	public static PropertyDescriptorsHolder getInstance(IPersistentClassMapping pcm){
		instance=new SubclassMappingPropertyDescriptorsHolder(pcm);
		return instance;
	}
	
	private SubclassMappingPropertyDescriptorsHolder(IPersistentClassMapping pcm){
		super(pcm);
		PropertyDescriptor pd;		
//		//Spec property descriptor: select database
		pd=new PropertyDescriptor("databaseTable.name",Messages.SubclassMappingPropertyDescriptorsHolder_DatabaseTableNameN); //$NON-NLS-1$
		pd.setDescription(Messages.SubclassMappingPropertyDescriptorsHolder_DatabaseTableNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
////		setDefaultPropertyValue(pd.getId(),"databaseTable");
	
		
		pd=new PropertyDescriptor("superclass.name",Messages.SubclassMappingPropertyDescriptorsHolder_SuperclassNameN); //$NON-NLS-1$
		pd.setDescription(Messages.SubclassMappingPropertyDescriptorsHolder_SuperclassNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
		pd=new TextPropertyDescriptor("discriminatorValue",Messages.SubclassMappingPropertyDescriptorsHolder_DiscriminatorValueN); //$NON-NLS-1$
		pd.setDescription(Messages.SubclassMappingPropertyDescriptorsHolder_DiscriminatorValueD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$
		
		
	}
}
