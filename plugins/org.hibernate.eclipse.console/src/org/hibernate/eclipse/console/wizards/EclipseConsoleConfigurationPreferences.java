/*
 * Created on 2004-11-01 by max
 * 
 */
package org.hibernate.eclipse.console.wizards;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hibernate.console.AbstractConsoleConfigurationPreferences;
import org.hibernate.console.ConsoleConfigurationPreferences;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.eclipse.console.utils.ClassLoaderHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author max
 *  
 */
public class EclipseConsoleConfigurationPreferences extends AbstractConsoleConfigurationPreferences {

	private IPath cfgFile;
	private IPath propertyFilename;
	private IPath[] mappings;
	private IPath[] customClasspath;

	public EclipseConsoleConfigurationPreferences(String configName, IPath cfgFile, IPath propertyFilename, IPath[] mappings, IPath[] classpaths) {
		super(configName);
		this.cfgFile = cfgFile;
		this.propertyFilename = propertyFilename;
		this.mappings = mappings;
		this.customClasspath = classpaths;		
	}

	/**
	 * @return Returns the cfgFile.
	 */
	public IPath getCfgFile() {
		return cfgFile;
	}
	
	/**
	 * @return Returns the propertyFilename.
	 */
	public IPath getPropertyFilename() {
		return propertyFilename;
	}
	
	/**
	 * @return Returns the mappings.
	 */
	public IPath[] getMappings() {
		return mappings;
	}
	
	/**
	 * @return Returns the customClasspath.
	 */
	public IPath[] getCustomClasspath() {
		return customClasspath;
	}
	
	protected EclipseConsoleConfigurationPreferences() {
		
	}

	public URL[] getCustomClassPathURLS() {
		try {
			return ClassLoaderHelper.getRawLocationsURLForResources(customClasspath);
		} catch (MalformedURLException mue) {
			throw new HibernateConsoleRuntimeException("Could not resolve classpaths", mue);
		}
	}

	public File[] getMappingFiles() {
		File[] files = new File[mappings.length]; 
		for (int i = 0; i < mappings.length; i++) {
			IPath path = mappings[i];
			files[i] = pathToFile(path);
			
		}
		return files;
	}

	private File pathToFile(IPath path) {
		if(path==null) return null;
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		
		return pathToFile(path.toString(), resource);
	}

	private File pathToFile(String path, IResource resource) {
		if(resource != null) {
			IPath rawLocation = resource.getRawLocation();
			if(rawLocation !=null) {
				return rawLocation.toFile();
			}
		}

		throw new HibernateConsoleRuntimeException("Could not resolve " + path + " to a file");
	}

	public File getConfigXMLFile() {
		return pathToFile(cfgFile);
	}

	public File getPropertyFile() {
		return pathToFile(propertyFilename);	
	}

	public void writeStateTo(Element node) {
		writeStateTo(node, getName(), cfgFile, propertyFilename, mappings, customClasspath);
	}

	protected void setConfigFile(String cfgFile) {
		this.cfgFile = cfgFile==null?null:new Path(cfgFile);
	}

	protected void setPropertyFile(String cfgFile) {
		this.propertyFilename = cfgFile==null?null:new Path(cfgFile);
	}
	protected void setMappings(String[] mappings) {
		this.mappings = new IPath[mappings.length];
		for (int i = 0; i < mappings.length; i++) {
			String str = mappings[i];
			this.mappings[i] = new Path(mappings[i]);	
		}
	}

	protected void setCustomClassPath(String[] mappings) {
		this.customClasspath = new IPath[mappings.length];
		for (int i = 0; i < mappings.length; i++) {
			String str = mappings[i];
			this.customClasspath[i] = new Path(mappings[i]);	
		}
	}
	
	public static EclipseConsoleConfigurationPreferences[] readStateFrom(File f) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder parser;
		try {
			parser = factory.newDocumentBuilder();
			
			Document doc = parser.parse(f);
			
			Element root = doc.getDocumentElement();
			
			NodeList elementsByTagName = root.getElementsByTagName(CONFIGURATION_TAG);
			EclipseConsoleConfigurationPreferences[] result = new EclipseConsoleConfigurationPreferences[elementsByTagName.getLength()];
			
			for(int i = 0; i < elementsByTagName.getLength(); i++) {
				result[i] = new EclipseConsoleConfigurationPreferences();
				result[i].readStateFrom((Element)elementsByTagName.item(i));
			}
			return result;
		} catch(SAXException sa) {
			throw new HibernateConsoleRuntimeException("Errors while parsing " + f,sa);
		} catch (ParserConfigurationException e) {
			throw new HibernateConsoleRuntimeException("Errors while parsing " + f,e);
		} catch (IOException e) {
			throw new HibernateConsoleRuntimeException("Errors while parsing " + f,e);		
		}    
	}
	
}
