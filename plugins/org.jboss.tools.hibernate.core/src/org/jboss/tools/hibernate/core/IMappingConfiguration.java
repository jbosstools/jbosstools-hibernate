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
package org.jboss.tools.hibernate.core;

import org.eclipse.ui.views.properties.IPropertySource2;


/**
 * @author alex
 *
 * Interface of a mapping configuration resource that stores connection props, datasource props etc. 
 */
public interface IMappingConfiguration extends IConfigurationResource, IPropertySource2{
	public String getProperty(String id);
	/**
	 * Set property value by given id. 
	 * */
	public void setProperty(String id, String value);
}
