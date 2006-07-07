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
package org.hibernate.eclipse.console.model.impl;

public class ExporterProperty
{
	private static ExporterProperty[] globalProperties = new ExporterProperty[] {
		new ExporterProperty ("jdk5", "Use Java 5 syntax", "false", false),
		new ExporterProperty ("ejb3", "Generate EJB3 annotations", "false", false)
	};
	
	private String defaultValue;
	private String description;
	private String name;
	private boolean required;
	
	public ExporterProperty () { }
	public ExporterProperty (String name, String description, String defaultValue, boolean required)
	{
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.required = required;
	}
	
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static ExporterProperty[] getGlobalProperties ()
	{
		return globalProperties;
	}
	
	public boolean equals(Object object) {
		if (object instanceof ExporterProperty)
		{
			ExporterProperty property = (ExporterProperty) object;
			return property.getName().equals(getName());
		}
		return false;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}
}