/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.views;

import java.util.Map;
import java.util.Properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.hibernate.cfg.Configuration;

/**
 * Responsible to provide Hibernate configuration 
 * properties for Properties View.
 * Properties are not editable - just to view.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ConfigurationPropertySource implements IPropertySource {

	private Configuration cfg;

	public ConfigurationPropertySource(Configuration cfg) {
		this.cfg = cfg;
	}

	public Object getEditableValue() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public IPropertyDescriptor[] getPropertyDescriptors() {
		final Properties props = cfg.getProperties();
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[props.size()];
		int i = 0;
		for (Map.Entry prop : props.entrySet()) {
			propertyDescriptors[i++] = new PropertyDescriptor(prop.getKey(), prop.getKey().toString());
		}
		return propertyDescriptors;
	}

	public Object getPropertyValue(Object id) {
		final Properties props = cfg.getProperties();
		return props.get(id);
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}
}
