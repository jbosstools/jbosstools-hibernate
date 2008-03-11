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
import org.jboss.tools.hibernate.internal.core.properties.DBTablePropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class UnionSubclassMappingPropertyDescriptorsHolder extends ClassMappingPropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance; 
	public static PropertyDescriptorsHolder getInstance(IPersistentClassMapping pcm){
		instance=new UnionSubclassMappingPropertyDescriptorsHolder(pcm);
		return instance;
	}
	
	private UnionSubclassMappingPropertyDescriptorsHolder(IPersistentClassMapping pcm){
		super(pcm);
		PropertyDescriptor pd;		
		//Spec property descriptor: select database
		pd=new DBTablePropertyDescriptor("databaseTable",Messages.UnionSubclassMappingPropertyDescriptorsHolder_DatabaseTableN,null,pcm); //$NON-NLS-1$
		pd.setDescription(Messages.UnionSubclassMappingPropertyDescriptorsHolder_DatabaseTableD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
//		setDefaultPropertyValue(pd.getId(),"databaseTable");
	
		pd=new TextPropertyDescriptor("check",Messages.UnionSubclassMappingPropertyDescriptorsHolder_CheckN); //$NON-NLS-1$
		pd.setDescription(Messages.UnionSubclassMappingPropertyDescriptorsHolder_CheckD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new DialogTextPropertyDescriptor("subselect",Messages.UnionSubclassMappingPropertyDescriptorsHolder_SubselectN); //$NON-NLS-1$
		pd.setDescription(Messages.UnionSubclassMappingPropertyDescriptorsHolder_SubselectD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new PropertyDescriptor("superclass.name",Messages.UnionSubclassMappingPropertyDescriptorsHolder_SuperclassNameN); //$NON-NLS-1$
		pd.setDescription(Messages.UnionSubclassMappingPropertyDescriptorsHolder_SuperclassNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
	}

}
