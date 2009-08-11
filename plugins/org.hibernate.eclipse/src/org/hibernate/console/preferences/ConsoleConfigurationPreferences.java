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
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Element;

/**
 * @author max
 *
 */
public interface ConsoleConfigurationPreferences {

	static final String PATH_TAG = "path"; //$NON-NLS-1$
	static final String CLASSPATH_TAG = "classpath"; //$NON-NLS-1$
	static final String MAPPING_TAG = "mapping"; //$NON-NLS-1$
	static final String MAPPINGS_TAG = "mappings"; //$NON-NLS-1$
	static final String HIBERNATE_PROPERTIES_TAG = "hibernate-properties"; //$NON-NLS-1$
	static final String LOCATION_ATTRIB = "location"; //$NON-NLS-1$
	static final String HIBERNATE_CONFIG_XML_TAG = "hibernate-config-xml"; //$NON-NLS-1$
	static final String NAME_ATTRIB = "name";	 //$NON-NLS-1$
	static final String CONFIGURATION_TAG = "configuration"; //$NON-NLS-1$
	static final String ANNOTATIONS_ATTRIB = "annotations"; //$NON-NLS-1$
	static final String ENTITYRESOLVER_ATTRIB = "entityresolver"; //$NON-NLS-1$
	static final String CONFIGURATION_MODE_ATTRIB = "configuration-factory"; //$NON-NLS-1$

	// TODO: we should move this to some classhandler
	static public class ConfigurationMode implements Serializable {

		private static final Map<String, ConfigurationMode> INSTANCES = new HashMap<String, ConfigurationMode>();

		public static final ConfigurationMode CORE = new ConfigurationMode( "CORE" ); //$NON-NLS-1$
		public static final ConfigurationMode ANNOTATIONS = new ConfigurationMode( "ANNOTATIONS" ); //$NON-NLS-1$
		public static final ConfigurationMode JPA = new ConfigurationMode( "JPA" ); //$NON-NLS-1$

		static {
			INSTANCES.put( CORE.name, CORE );
			INSTANCES.put( ANNOTATIONS.name, ANNOTATIONS );
			INSTANCES.put( JPA.name, JPA );
		}

		private final String name;

		public ConfigurationMode(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}

		private Object readResolve() {
			return INSTANCES.get( name );
		}

		public static ConfigurationMode parse(String name) {
			ConfigurationMode rtn = INSTANCES.get( name );
			if ( rtn == null ) {
				// default is POJO
				rtn = CORE;
			}
			return rtn;
		}
		
		public static String[] values(){
			return INSTANCES.keySet().toArray(new String[INSTANCES.size()]);
		}
	}


	public abstract ConfigurationMode getConfigurationMode();

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

	public abstract String getPersistenceUnitName();

	public abstract String getNamingStrategy();

	/**
	 * 
	 * @return null if ConnectionProfile was not specified
	 */
	public abstract String getConnectionProfileName();
	
	public abstract String getDialectName();
}