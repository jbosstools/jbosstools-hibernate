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
 * @author kaa
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VersionPropertyDescriptorsHolder extends
PropertyDescriptorsHolder {
	private static final String VERSION_CATEGORY=Messages.VersionPropertyDescriptorsHolder_VersionCategory;
	private static VersionPropertyDescriptorsHolder instance=new VersionPropertyDescriptorsHolder();
	public static VersionPropertyDescriptorsHolder getInstance(){
		return instance;
	}

	private VersionPropertyDescriptorsHolder(){
		PropertyDescriptor pd=null;
		/* 05/24/05 by alex
		 * new TextPropertyDescriptor("version.name","Version name");
		pd.setDescription("Version name");
		addPropertyDescriptor(pd);
		pd.setCategory(VERSION_CATEGORY);
		*/
		
		pd=new PropertyDescriptor("version.value.type.name",Messages.VersionPropertyDescriptorsHolder_VersionValueTypeNameN); //$NON-NLS-1$
		pd.setDescription(Messages.VersionPropertyDescriptorsHolder_VersionValueTypeNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(VERSION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),"");		 //$NON-NLS-1$

		pd=new ListPropertyDescriptor("version.value.nullValue",Messages.VersionPropertyDescriptorsHolder_VersionValueNullValueN,OrmConfiguration.VERSION_UNSAVED_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.VersionPropertyDescriptorsHolder_VersionValueNullValueD);
		addPropertyDescriptor(pd);
		pd.setCategory(VERSION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_VERSION_UNSAVED);		
		
		pd=new ListPropertyDescriptor("version.propertyAccessorName",Messages.VersionPropertyDescriptorsHolder_VersionPropertyAccessorNameN,OrmConfiguration.ACCESS_VALUES); //$NON-NLS-1$
		pd.setDescription(Messages.VersionPropertyDescriptorsHolder_VersionPropertyAccessorNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(VERSION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.DEFAULT_ACCESS);
		
		pd=new PropertyDescriptor("versionColumnName",Messages.VersionPropertyDescriptorsHolder_VersionColumnNameN); //$NON-NLS-1$
		pd.setDescription(Messages.VersionPropertyDescriptorsHolder_VersionColumnNameD);
		addPropertyDescriptor(pd);
		pd.setCategory(VERSION_CATEGORY);
		setDefaultPropertyValue(pd.getId(),""); //$NON-NLS-1$


//		pd=new ListPropertyDescriptor("optimisticLocked","optimisticLocked",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("optimisticLocked");
//		addPropertyDescriptor(pd);
//		pd.setCategory(VERSION_CATEGORY);
//		
//		pd=new ListPropertyDescriptor("lazy","lazy",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("lazy");
//		addPropertyDescriptor(pd);
//		pd.setCategory(VERSION_CATEGORY);
//
//		pd=new ListPropertyDescriptor("optional","optional",OrmConfiguration.BOOLEAN_VALUES);
//		pd.setDescription("optional");
//		addPropertyDescriptor(pd);
//		pd.setCategory(VERSION_CATEGORY);

//		pd=new TextPropertyDescriptor("value","Field/Column");
//		pd.setDescription("Field/Column");
//		addPropertyDescriptor(pd);
//		pd.setCategory(FIELD_CATEGORY);
//		setDefaultPropertyValue(pd.getId(),"null");		

	}	
}
