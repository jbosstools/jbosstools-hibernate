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
package org.jboss.tools.hibernate.internal.core;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.ListPropertyDescriptor;
import org.jboss.tools.hibernate.internal.core.properties.PropertyDescriptorsHolder;


/**
 * @author Gavrs
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class JavaLoggingPropertiesHolder extends PropertyDescriptorsHolder {
	private static JavaLoggingPropertiesHolder instance=new JavaLoggingPropertiesHolder();
	public static JavaLoggingPropertiesHolder getInstance(){
		return instance;
	}
	
	private static final String GENERAL_CATEGORY=Messages.JavaLoggingPropertiesHolder_GeneralCategory;
	private JavaLoggingPropertiesHolder(){

		
		PropertyDescriptor pd=new ListPropertyDescriptor(OrmConfiguration.HIBERNATE_STORAGE,Messages.JavaLoggingPropertiesHolder_HibernateStorageN,OrmConfiguration.STORAGE_STRATEGY_NAMES,OrmConfiguration.STORAGE_STRATEGY_VALUES);
		pd.setDescription(Messages.JavaLoggingPropertiesHolder_HibernateStorageD);
		addPropertyDescriptor(pd);
		pd.setCategory(GENERAL_CATEGORY);
		setDefaultPropertyValue(pd.getId(),OrmConfiguration.STORAGE_STRATEGY_VALUES[0]);

}

}

