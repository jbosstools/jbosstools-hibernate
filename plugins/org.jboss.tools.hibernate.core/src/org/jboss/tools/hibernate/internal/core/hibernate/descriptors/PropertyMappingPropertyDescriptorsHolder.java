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

//import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


public class PropertyMappingPropertyDescriptorsHolder extends PropertyDescriptorsHolder {
	private static PropertyDescriptorsHolder instance=new PropertyMappingPropertyDescriptorsHolder();
//	private static final String GENERAL_CATEGORY="General";

	public static PropertyDescriptorsHolder getInstance(){
		return instance;
	}
	
	protected PropertyMappingPropertyDescriptorsHolder(){

//		PropertyDescriptor pd;
//		=new TextPropertyDescriptor("cascade","cascade");
//		pd.setDescription("cascade");
//		pd.setCategory(GENERAL_CATEGORY);
//		addPropertyDescriptor(pd);
//		setDefaultPropertyValue(pd.getId(),"");
//
//		pd=new ListPropertyDescriptor("updateable","update",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("updateable");
//		addPropertyDescriptor(pd);
//		pd.setCategory(GENERAL_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),"true");
//		
//		pd=new ListPropertyDescriptor("insertable","insert",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("insertable");
//		addPropertyDescriptor(pd);
//		pd.setCategory(GENERAL_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),"true");
//
//		pd=new ListPropertyDescriptor("optimisticLocked","optimistic-lock",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("optimisticLocked");
//		addPropertyDescriptor(pd);
//		pd.setCategory(GENERAL_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),"true");
//
//		pd=new ListPropertyDescriptor("lazy","lazy",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("lazy");
//		addPropertyDescriptor(pd);
//		pd.setCategory(GENERAL_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),"false");
//		
//		
//		pd=new ListPropertyDescriptor("propertyAccessorName","access",OrmConfiguration.ACCESS_VALUES);
//		pd.setDescription("access");
//		addPropertyDescriptor(pd);
//		pd.setCategory(GENERAL_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ACCESS);
				
	}
	
}
