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
 * 02110-1301 USA, or see the FSF site: http:/*
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
package org.hibernate.eclipse.console.model.impl;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.tool.hbm2x.Exporter;

public class ExporterDefinition {
	final private String classname;

	final private String description;

	final private String id;

	private ImageDescriptor iconDescriptor;

	private HashMap properties;

	public ExporterDefinition(IConfigurationElement element) {
		this.classname = element.getAttribute( "classname" );
		this.description = element.getAttribute( "description" );
		this.id = element.getAttribute( "id" );
		createIcon( element );
		createProperties( element );
	}

	private void createIcon(IConfigurationElement element) {
		if ( element.getAttribute( "icon" ) != null ) {
			this.iconDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
					element.getNamespace(), element.getAttribute( "icon" ) );
		}
	}

	private void createProperties(IConfigurationElement element) {
		properties = new HashMap();

		IConfigurationElement propertyElements[] = element
				.getChildren( "property" );
		for (int i = 0; i < propertyElements.length; i++) {
			ExporterProperty property = new ExporterProperty(
				propertyElements[i].getAttribute("name"),
				propertyElements[i].getAttribute("description"),
				propertyElements[i].getAttribute("value"),
				Boolean.valueOf(propertyElements[i].getAttribute("required")).booleanValue());
			properties.put(property, propertyElements[i].getAttribute("value"));
		}
	}

	public Exporter createExporterInstance() {
	   Exporter exporter = null;

      try
      {
         Class exporterClass = Class.forName( classname );
         exporter = (Exporter) exporterClass.newInstance();
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (InstantiationException e)
      {
         e.printStackTrace();
      }
      catch (IllegalAccessException e)
      {
         e.printStackTrace();
      }


		return exporter;
	}

	public String getDescription() {
		return description;
	}

	public ImageDescriptor getIconDescriptor() {
		return iconDescriptor;
	}

	public HashMap getProperties() {
		return properties;
	}

	public boolean isEnabled(ILaunchConfiguration configuration) {
		boolean enabled = false;

		try {
			// if we put this in some "namespace" we should have a way to either
			// migrate an existing one...
			enabled = configuration.getAttribute( id, false );
		}
		catch (CoreException e) {
			e.printStackTrace(); // TODO-marshall: bad!
		}

		return enabled;
	}

	public void setEnabled(ILaunchConfigurationWorkingCopy configuration,
			boolean enabled) {
		configuration.setAttribute( id, enabled );
	}

	public String getId() {
		return id;
	}
}
