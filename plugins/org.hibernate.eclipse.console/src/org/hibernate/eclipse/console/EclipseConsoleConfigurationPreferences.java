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
package org.hibernate.eclipse.console;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.preferences.AbstractConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.utils.ClassLoaderHelper;
import org.hibernate.eclipse.console.utils.ProjectUtils;
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
	private IPath[] mappings = new IPath[0];
	private IPath[] customClasspath = new IPath[0];

	public EclipseConsoleConfigurationPreferences(String configName, 
			ConfigurationMode cmode, String projectName, boolean useProjectClasspath, 
			String entityResolver, IPath cfgFile, IPath propertyFilename, 
			IPath[] mappings, IPath[] classpaths, String persistenceUnitName, String namingStrategy,
			String connectionProfile, String dialectName) {
		super(configName, cmode, projectName, useProjectClasspath, entityResolver, persistenceUnitName, namingStrategy, connectionProfile, dialectName);		
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
			IJavaProject project = ProjectUtils.findJavaProject( getProjectName() );
			String[] additonal = new String[0];
			if(project != null && useProjectClasspath() && project.exists()) {
				try {
					additonal = JavaRuntime.computeDefaultRuntimeClassPath(project);
				}
				catch (CoreException e) {
					throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseConsoleConfigurationPreferences_could_not_compute_def_classpath + project );
				}

			}
			URL[] rawLocationsURLForResources = ClassLoaderHelper.getRawLocationsURLForResources(customClasspath);
			URL[] result = new URL[rawLocationsURLForResources.length+additonal.length];
			for (int i = 0; i < rawLocationsURLForResources.length; i++) {
				result[i] = rawLocationsURLForResources[i];
			}
			for (int i = 0; i < additonal.length; i++) {
				String url = additonal[i];
				result[i+rawLocationsURLForResources.length] = new File(url).toURL();
			}
			return result;
		} catch (MalformedURLException mue) {
			throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseConsoleConfigurationPreferences_could_not_resolve_classpaths, mue);
		}
	}

	public File[] getMappingFiles() {
		File[] files = new File[mappings.length];
		for (int i = 0; i < mappings.length; i++) {
			files[i] = pathToFile(mappings[i]);
		}
		return files;
	}

	private File pathToFile(IPath path) {
		if(path==null) return null;
		if (path.toFile().exists()) return path.toFile();
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
		String out = NLS.bind(HibernateConsoleMessages.EclipseConsoleConfigurationPreferences_could_not_resolve_to_file, path);
		throw new HibernateConsoleRuntimeException(out);
	}

	public File getConfigXMLFile() {
		return pathToFile(cfgFile);
	}

	public File getPropertyFile() {
		return pathToFile(propertyFilename);
	}

	public void writeStateTo(Element node) {
		writeStateTo(node, getName(), getEntityResolverName(), getConfigurationMode(), getProjectName(), useProjectClasspath(), cfgFile, propertyFilename, mappings, customClasspath);
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
			this.mappings[i] = new Path(mappings[i]);
		}
	}

	protected void setCustomClassPath(String[] mappings) {
		this.customClasspath = new IPath[mappings.length];
		for (int i = 0; i < mappings.length; i++) {
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

			NodeList elementsByTagName = root.getElementsByTagName(CONFIGURATION_TAG); //TODO: only get nearest children.
			EclipseConsoleConfigurationPreferences[] result = new EclipseConsoleConfigurationPreferences[elementsByTagName.getLength()];

			for(int i = 0; i < elementsByTagName.getLength(); i++) {
				result[i] = new EclipseConsoleConfigurationPreferences();
				result[i].readStateFrom( (Element)elementsByTagName.item(i) );
			}
			return result;
		} catch(SAXException sa) {
			throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseConsoleConfigurationPreferences_errors_while_parsing + f,sa);
		} catch (ParserConfigurationException e) {
			throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseConsoleConfigurationPreferences_errors_while_parsing + f,e);
		} catch (IOException e) {
			throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.EclipseConsoleConfigurationPreferences_errors_while_parsing + f,e);
		}
	}

	}
