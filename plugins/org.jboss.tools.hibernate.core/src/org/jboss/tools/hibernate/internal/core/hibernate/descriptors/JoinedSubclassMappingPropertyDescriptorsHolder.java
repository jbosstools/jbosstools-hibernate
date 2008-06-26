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
import org.jboss.tools.hibernate.internal.core.properties.DialogDBColumnPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.DialogTextPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class JoinedSubclassMappingPropertyDescriptorsHolder extends ClassMappingPropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance; 
	public static PropertyDescriptorsHolder getInstance(IPersistentClassMapping pcm){
		instance=new JoinedSubclassMappingPropertyDescriptorsHolder(pcm);
		return instance;
	}
	
	private JoinedSubclassMappingPropertyDescriptorsHolder(IPersistentClassMapping pcm){
		super(pcm);
		PropertyDescriptor pd;		
		//Spec property descriptor: select database
		String[] dep={"keyColumn"}; //$NON-NLS-1$
		pd=new DBTablePropertyDescriptor("databaseTable",Messages.JoinedSubclassMappingPropertyDescriptorsHolder_DatabaseTableN,dep,pcm); //$NON-NLS-1$
		pd.setDescription(Messages.JoinedSubclassMappingPropertyDescriptorsHolder_DatabaseTableD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
		pd=new PropertyDescriptor("superclass.name",Messages.JoinedSubclassMappingPropertyDescriptorsHolder_SuperclassNameN); //$NON-NLS-1$
		pd.setDescription(Messages.JoinedSubclassMappingPropertyDescriptorsHolder_SuperclassNameD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
		pd=new DialogDBColumnPropertyDescriptor("keyColumn",Messages.JoinedSubclassMappingPropertyDescriptorsHolder_KeyColumnN,null,pcm.getDatabaseTable()); //$NON-NLS-1$
		pd.setDescription(Messages.JoinedSubclassMappingPropertyDescriptorsHolder_KeyColumnD);
		pd.setCategory(GENERAL_CATEGORY);
		addPropertyDescriptor(pd);
		
		pd=new TextPropertyDescriptor("check",Messages.JoinedSubclassMappingPropertyDescriptorsHolder_CheckN); //$NON-NLS-1$
		pd.setDescription(Messages.JoinedSubclassMappingPropertyDescriptorsHolder_CheckD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		pd=new DialogTextPropertyDescriptor("subselect",Messages.JoinedSubclassMappingPropertyDescriptorsHolder_SubselectN); //$NON-NLS-1$
		pd.setDescription(Messages.JoinedSubclassMappingPropertyDescriptorsHolder_SubselectD);
		pd.setCategory(ADVANCED_CATEGORY);
		addPropertyDescriptor(pd);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$
		
		
		}
}
