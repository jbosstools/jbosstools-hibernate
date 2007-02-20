/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;

public class ConsoleConfigurationPropertySource implements IPropertySource {

	private final ConsoleConfiguration cfg;

	
	static IPropertyDescriptor[] pd;
	static {
		List l = new ArrayList();
		l.add(new TextPropertyDescriptor("name", "Name"));
		l.add(new PropertyDescriptor("hibernate.cfg.xml", "Configuration file"));
		l.add(new PropertyDescriptor("hibernate.properties", "Properties file"));
		l.add(new PropertyDescriptor("mapping.files", "Additonal mapping files"));
		
		
		pd = (IPropertyDescriptor[]) l.toArray( new IPropertyDescriptor[l.size()] );
	}
	
	public ConsoleConfigurationPropertySource(ConsoleConfiguration cfg) {
		this.cfg = cfg;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return pd;
	}

	public Object getPropertyValue(Object id) {
		try {
		if("name".equals(id)) {
			return cfg.getName();
		}
		// TODO: bring back more eclipse friendly file names
		ConsoleConfigurationPreferences preferences = cfg.getPreferences();
		
		if("hibernate.cfg.xml".equals(id)) {
			return preferences.getConfigXMLFile();
		}
		if("hibernate.properties".equals(id)) {
			return preferences.getPropertyFile();
		}
		if("mapping.files".equals(id)) {
			return new Integer(preferences.getMappingFiles().length);
		}
		
		return null;
		} catch(RuntimeException e) {
			return "Error: " + e.getMessage();
		}
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		}

}
