/*
 * Created on 2004-11-01 by max
 * 
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