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
package org.hibernate.console.preferences;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.w3c.dom.Element;

/**
 * @author max
 *
 */
public interface ConsoleConfigurationPreferences {
	
	final String PATH_TAG = "path";
	final String CLASSPATH_TAG = "classpath";
	final String MAPPING_TAG = "mapping";
	final String MAPPINGS_TAG = "mappings";
	final String HIBERNATE_PROPERTIES_TAG = "hibernate-properties";
	final String LOCATION_ATTRIB = "location";
	final String HIBERNATE_CONFIG_XML_TAG = "hibernate-config-xml";
	final String NAME_ATTRIB = "name";	
	final String CONFIGURATION_TAG = "configuration";
	final String ANNOTATIONS_ATTRIB = "annotations";
	final String ENTITYRESOLVER_ATTRIB = "entityresolver";
	
	
	public abstract boolean useAnnotations();
	
	public abstract String getName();

	/**
	 * @return return non-null array of URLs for a customclasspath
	 */
	public abstract URL[] getCustomClassPathURLS();

	/**
	 * @return return non-null array of URLs for mapping files
	 */
	public abstract File[] getMappingFiles();

	public abstract Properties getProperties();

	public abstract File getConfigXMLFile();

	public abstract File getPropertyFile();
	
	public abstract void writeStateTo(Element node);

	public abstract void readStateFrom(Element element);

	public abstract void setName(String name);

	public abstract String getEntityResolverName();
}