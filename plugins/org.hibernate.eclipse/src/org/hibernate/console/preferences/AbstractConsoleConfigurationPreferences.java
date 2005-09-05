/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.console.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

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


	private String name = "<unknown>";
	private boolean useAnnotations = false;
	protected String entityResolverName = null;
	
	public AbstractConsoleConfigurationPreferences(String name) {
		setName(name);
	}
	
	protected AbstractConsoleConfigurationPreferences() {
			// hidden 	
	}
	
	public boolean useAnnotations() {
		return useAnnotations ;
	}
	
	public void setName(String name) {
		if(name==null || name.trim().length()==0) {
			throw new IllegalArgumentException("Name cannot be null or empty");
		}
		
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public final Properties getProperties() {
		File propFile = getPropertyFile();
		if(propFile==null) return null;
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(propFile) );
			return p; 
		} 
		catch(IOException io) {
			throw new HibernateConsoleRuntimeException("Could not load property file " + propFile, io);
		}
	}

	/** generic xml dumper that just dumps the toString representation of the paramters 
	 * @param useAnnotations */
	protected static void writeStateTo(Node node, String name, String entityResolver, boolean useAnnotations, Object cfgFile, Object propertyFilename, Object[] mappings, Object[] customClasspath) {
		Document doc = node.getOwnerDocument();
		Element n = createElementWithAttribute(doc, CONFIGURATION_TAG, NAME_ATTRIB, name);
		if(useAnnotations) {
			n.setAttribute(ANNOTATIONS_ATTRIB, "true");
		}
		if(StringHelper.isNotEmpty(entityResolver)) {
			n.setAttribute(ENTITYRESOLVER_ATTRIB, entityResolver);
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
	
	public void readStateFrom(Element node) {
		    
		String entityResolver = null;
		String cfgName = null;
		String cfgFile = null;
		String propFile = null;
		String[] mappings = new String[0];
		String[] classpath = new String[0];
		
		cfgName = node.getAttribute(NAME_ATTRIB);
		
		String attribute = node.getAttribute(ANNOTATIONS_ATTRIB);
		useAnnotations = ((attribute != null) && attribute.equalsIgnoreCase("true"));
		
		attribute = node.getAttribute(ENTITYRESOLVER_ATTRIB);
		if(attribute!=null && attribute.trim().length()>0) {
			entityResolver = attribute;
		}
			
		NodeList elements = node.getElementsByTagName(HIBERNATE_CONFIG_XML_TAG);
		if(elements.getLength()==1) {
			cfgFile = ( (Element)elements.item(0) ).getAttribute(LOCATION_ATTRIB);
		}
		
		elements = node.getElementsByTagName(HIBERNATE_PROPERTIES_TAG);
		if(elements.getLength()==1) {
			propFile = ( (Element)elements.item(0) ).getAttribute(LOCATION_ATTRIB);
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
				result[j] = child.getAttribute(LOCATION_ATTRIB);
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
