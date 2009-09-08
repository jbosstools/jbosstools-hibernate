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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.util.StringHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author max
 *
 */
public abstract class AbstractConsoleConfigurationPreferences implements
		ConsoleConfigurationPreferences {

	static final String PROJECT_ATTRIB = "project"; //$NON-NLS-1$
	static final String USE_PROJECT_CLASSPATH_ATTRIB = "use-project-classpath"; //$NON-NLS-1$


	private String projectName;

	private String name = ConsoleMessages.AbstractConsoleConfigurationPreferences_unknown;
	protected String entityResolverName = null;
	private boolean useProjectClasspath;
	private ConfigurationMode configurationMode;
	private String persistenceUnitName;
	private String namingStrategy;
	private String connectionProfile;
	private String dialectName;
	

	public AbstractConsoleConfigurationPreferences(String name, ConfigurationMode configurationMode,
			String projectName, boolean useProjectclassPath, String entityResolver,
			String persistenceUnitName, String namingStrategy,
			String connectionProfile, String dialectName) {
		setName(name);
		this.persistenceUnitName = persistenceUnitName;
		this.namingStrategy = namingStrategy;
		this.configurationMode = configurationMode;
		entityResolverName = entityResolver;
		this.projectName = projectName;
		this.useProjectClasspath = useProjectclassPath;
		this.connectionProfile = connectionProfile;
		this.dialectName = dialectName;
	}

	protected AbstractConsoleConfigurationPreferences() {

	}

	public ConfigurationMode getConfigurationMode() {
		return configurationMode;
	}

	public String getPersistenceUnitName() {
		return persistenceUnitName;
	}

	public String getNamingStrategy() {
		return namingStrategy;
	}

	public String getConnectionProfileName() {
		return connectionProfile;
	}
	
	public String getDialectName() {
		return dialectName;
	}

	public void setName(String name) {
		if(name==null || name.trim().length()==0) {
			throw new IllegalArgumentException(ConsoleMessages.AbstractConsoleConfigurationPreferences_name_not_null_or_empty);
		}

		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final Properties getProperties() {
		File propFile = getPropertyFile();
		if(propFile==null) return null;
		FileInputStream inStream = null;		
		try {
			Properties p = new Properties();
			inStream = new FileInputStream(propFile);
			p.load(inStream );
			return p;
		}
		catch(IOException io) {
			throw new HibernateConsoleRuntimeException(ConsoleMessages.AbstractConsoleConfigurationPreferences_could_not_load_prop_file + propFile, io);
		} finally {
			if(inStream!=null)
				try {
					inStream.close();
				} catch (IOException e) {
					//ignore
				}
		}
	}

	/** generic xml dumper that just dumps the toString representation of the paramters
	 * @param useAnnotations */
	protected static void writeStateTo(Node node, String name, String entityResolver, ConfigurationMode configurationMode, String projectName, boolean useProjectClasspath, Object cfgFile, Object propertyFilename, Object[] mappings, Object[] customClasspath) {
		Document doc = node.getOwnerDocument();
		Element n = createElementWithAttribute(doc, CONFIGURATION_TAG, NAME_ATTRIB, name);
		/*if(useAnnotations) {
			n.setAttribute(ANNOTATIONS_ATTRIB, "true");
		}*/
		n.setAttribute(CONFIGURATION_MODE_ATTRIB, configurationMode.toString());

		if(StringHelper.isNotEmpty(entityResolver)) {
			n.setAttribute(ENTITYRESOLVER_ATTRIB, entityResolver);
		}

		if(useProjectClasspath) {
			n.setAttribute( USE_PROJECT_CLASSPATH_ATTRIB, "true" ); //$NON-NLS-1$
		}

		if(StringHelper.isNotEmpty(projectName)) {
			n.setAttribute(PROJECT_ATTRIB, projectName);
		}

		node.appendChild(n);

		if(cfgFile!=null) {
			n.appendChild(createElementWithAttribute(doc, HIBERNATE_CONFIG_XML_TAG, LOCATION_ATTRIB, cfgFile.toString() ) );
		}

		if(propertyFilename!=null) {
			n.appendChild(createElementWithAttribute(doc, HIBERNATE_PROPERTIES_TAG, LOCATION_ATTRIB, propertyFilename.toString() ) );
		}

		if(mappings.length>0) {
			Element cc = createElementWithAttribute(doc, MAPPINGS_TAG, null, null);
			n.appendChild(cc);

			for (int i = 0; i < mappings.length; i++) {
				Object path = mappings[i];
				cc.appendChild(createElementWithAttribute(doc, MAPPING_TAG, LOCATION_ATTRIB, path.toString() ) );
			}
		}

		if(customClasspath.length>0) {
			Element cc = createElementWithAttribute(doc, CLASSPATH_TAG, null, null);
			n.appendChild(cc);

			for (int i = 0; i < customClasspath.length; i++) {
				Object path = customClasspath[i];
				cc.appendChild(createElementWithAttribute(doc, PATH_TAG, LOCATION_ATTRIB, path.toString() ) );
			}
		}
	}

	public boolean useProjectClasspath() {
		return useProjectClasspath;
	}

	protected void setUseProjectClasspath(boolean useProjectClasspath) {
		this.useProjectClasspath = useProjectClasspath;
	}



	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void readStateFrom(Element node) {

		String entityResolver = null;
		String cfgName = null;
		String cfgFile = null;
		String propFile = null;
		String[] mappings = new String[0];
		String[] classpath = new String[0];

		if (node.hasAttribute(NAME_ATTRIB)) {
			cfgName = node.getAttribute(NAME_ATTRIB);
		}

		String attribute = node.getAttribute(ANNOTATIONS_ATTRIB);
		if(StringHelper.isNotEmpty( attribute )) {
			boolean oldAnnotationFlag = ((attribute != null) && attribute.equalsIgnoreCase("true")); //$NON-NLS-1$
			if(oldAnnotationFlag) {
				configurationMode = ConfigurationMode.ANNOTATIONS;
			} else {
				configurationMode = ConfigurationMode.CORE;
			}
		} else {
			attribute = node.getAttribute(CONFIGURATION_MODE_ATTRIB);
			configurationMode = ConfigurationMode.parse( attribute );
		}


		attribute = node.getAttribute( PROJECT_ATTRIB );
		setProjectName( attribute );

		attribute = node.getAttribute( USE_PROJECT_CLASSPATH_ATTRIB );
		setUseProjectClasspath("true".equalsIgnoreCase(attribute)); //$NON-NLS-1$

		attribute = node.getAttribute(ENTITYRESOLVER_ATTRIB);
		if(attribute!=null && attribute.trim().length()>0) {
			entityResolver = attribute;
		}

		NodeList elements = node.getElementsByTagName(HIBERNATE_CONFIG_XML_TAG);
		if(elements.getLength()==1) {
			final Element el = (Element)elements.item(0);
			if (el.hasAttribute(LOCATION_ATTRIB)) {
				cfgFile = el.getAttribute(LOCATION_ATTRIB);
			}
		}

		elements = node.getElementsByTagName(HIBERNATE_PROPERTIES_TAG);
		if(elements.getLength()==1) {
			final Element el = (Element)elements.item(0);
			if (el.hasAttribute(LOCATION_ATTRIB)) {
				propFile = el.getAttribute(LOCATION_ATTRIB);
			}
		}


		mappings = parseListOfLocations(node, MAPPINGS_TAG, MAPPING_TAG);
		classpath = parseListOfLocations(node, CLASSPATH_TAG, PATH_TAG);


		setName(cfgName);
		setEntityResolverName(entityResolver);
		setConfigFile(cfgFile);
		setPropertyFile(propFile);
		setMappings(mappings);
		setCustomClassPath(classpath);

	}

	private void setEntityResolverName(String entityResolver) {
		this.entityResolverName = entityResolver;

	}

	private String[] parseListOfLocations(Element node, String parenttag, String elementag) {
		String[] result = new String[0];
		NodeList elements;
		elements = node.getElementsByTagName(parenttag);
		if(elements.getLength()==1) {
			NodeList maps = ( (Element) elements.item(0) ).getElementsByTagName(elementag);
			result = new String[maps.getLength()];
			for (int j = 0; j < maps.getLength(); j++) {
				Element child = (Element) maps.item(j);
				result[j] = child.hasAttribute(LOCATION_ATTRIB) ?
						child.getAttribute(LOCATION_ATTRIB) : null;
			}
		}
		return result;
	}

	/**
	 * generic XML handling. Ugly like hell - but done to reduce jar requirements and code duplication.
	 * TODO: make better ;)
	 **/

	public String getEntityResolverName() { return entityResolverName; };

	protected abstract void setConfigFile(String cfgFile);
	protected abstract void setPropertyFile(String cfgFile);
	protected abstract void setMappings(String[] mappings);
	protected abstract void setCustomClassPath(String[] mappings);


	protected static Element createElementWithAttribute(Document doc, String tagName, String attribName, String attributValue) {
		Element n;
		n = doc.createElement(tagName);
		if(attribName!=null) {
			n.setAttribute(attribName, attributValue);
		}
		return n;
	}

}
