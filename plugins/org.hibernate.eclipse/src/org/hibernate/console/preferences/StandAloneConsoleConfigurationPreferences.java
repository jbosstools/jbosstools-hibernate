/*
 * Created on 2004-10-31 by max
 * 
 */
package org.hibernate.console.preferences;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.console.HibernateConsoleRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class StandAloneConsoleConfigurationPreferences extends AbstractConsoleConfigurationPreferences {

	private File cfgFile;
	private File propertyFilename;
	private File[] mappings;
	private File[] customClasspath;	

	public StandAloneConsoleConfigurationPreferences(String name, File xmlconfig, File propertiesFile, File[] mappingFiles, File[] customClasspath) {
		super(name);
		this.cfgFile = xmlconfig;
		this.propertyFilename = propertiesFile;
		this.mappings = mappingFiles;
		this.customClasspath = customClasspath;			
	}

	protected StandAloneConsoleConfigurationPreferences() {
		// hidden for others
	}
	
	protected StandAloneConsoleConfigurationPreferences(String name) {
		this(name,null,null,new File[0],new File[0]);
	}
	
	/**
	 * @return return non-null array of URLs for a customclasspath
	 */
	public URL[] getCustomClassPathURLS() {
		URL[] result = new URL[customClasspath.length];
		
		for (int i = 0; i < customClasspath.length; i++) {
			File file = customClasspath[i];
			try {
				result[i] = file.toURL();
			} 
			catch (MalformedURLException mue) {
				throw new HibernateConsoleRuntimeException("Could not resolve classpaths", mue);
			}
		}
		return result;
	}
	
	/**
	 * @return return non-null array of URLs for mapping files
	 */
	public File[] getMappingFiles() {
		return mappings;
	}
	
	public File getConfigXMLFile() {
		return cfgFile;
	}

	public void writeStateTo(Element node) {
		writeStateTo(node, getName(), useAnnotations(), cfgFile, propertyFilename, mappings, customClasspath);		
	}

	
	public File getPropertyFile() {
		return propertyFilename;
	}


	protected void setConfigFile(String cfgFile) {
		this.cfgFile = cfgFile==null?null:new File(cfgFile);
	}

	protected void setPropertyFile(String cfgFile) {
		this.propertyFilename = cfgFile==null?null:new File(cfgFile);
	}

	protected void setMappings(String[] mappings) {
		this.mappings = new File[mappings.length];
		for (int i = 0; i < mappings.length; i++) {
			String str = mappings[i];
			this.mappings[i] = new File(str);	
		}
	}

	protected void setCustomClassPath(String[] mappings) {
		this.customClasspath = new File[mappings.length];
		for (int i = 0; i < mappings.length; i++) {
			String str = mappings[i];
			this.customClasspath[i] = new File(str);	
		}
	}	
	public static StandAloneConsoleConfigurationPreferences[] readStateFrom(File f) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		try {
			parser = factory.newDocumentBuilder();
			
			Document doc = parser.parse(f);
			
			Element root = doc.getDocumentElement();
			
			NodeList elementsByTagName = root.getElementsByTagName(CONFIGURATION_TAG);
			StandAloneConsoleConfigurationPreferences[] result = new StandAloneConsoleConfigurationPreferences[elementsByTagName.getLength()];
			
			for(int i = 0; i < elementsByTagName.getLength(); i++) {
				result[i] = new StandAloneConsoleConfigurationPreferences();
				result[i].readStateFrom( (Element)elementsByTagName.item(i) );
			}
			return result;
		} 
		catch(SAXException sa) {
			throw new HibernateConsoleRuntimeException("Errors while parsing " + f,sa);
		} 
		catch (ParserConfigurationException e) {
			throw new HibernateConsoleRuntimeException("Errors while parsing " + f,e);
		} 
		catch (IOException e) {
			throw new HibernateConsoleRuntimeException("Errors while parsing " + f,e);		
		}    
	}

}