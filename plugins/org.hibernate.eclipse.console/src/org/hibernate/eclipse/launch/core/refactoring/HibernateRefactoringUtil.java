/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.launch.core.refactoring;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.hibernate.eclipse.launch.ICodeGenerationLaunchConstants;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateRefactoringUtil {
	
	private static final String ERROR_MESS = "Error during refactoring";
	
	private static String[] pathKeys = new String[]{
		IConsoleConfigurationLaunchConstants.CFG_XML_FILE,
		IConsoleConfigurationLaunchConstants.PROPERTY_FILE,
		HibernateLaunchConstants.ATTR_TEMPLATE_DIR,
		HibernateLaunchConstants.ATTR_OUTPUT_DIR,
		HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS,		
		};
	
	private static String[] pathListKeys = new String[]{
		IConsoleConfigurationLaunchConstants.FILE_MAPPINGS,	
	};
	
	public static boolean isConfigurationAffected(ILaunchConfiguration config, IPath oldPath) throws CoreException{
		return isAttributesAffected(config, oldPath) || isClassPathAffected(config, oldPath);
	}
	
	private static boolean isAttributesAffected(ILaunchConfiguration config, IPath oldPath) throws CoreException{
		String attrib = null;
		for (int i = 0; i < pathKeys.length; i++) {
			attrib = config.getAttribute(pathKeys[i], (String)null);
			if (isAttributeChanged(attrib, oldPath)) 
				return true;			
		}
		
		for (int i = 0; i < pathListKeys.length; i++) {
			List<String> list = config.getAttribute(pathListKeys[i], Collections.EMPTY_LIST);
			List<String> newMappings = new ArrayList<String>();
			Iterator<String> iter = list.iterator();
			while ( iter.hasNext() ) {
				attrib = iter.next();
				if (isAttributeChanged(attrib, oldPath)){
					return true;
				}
				newMappings.add(attrib);
			}
		}
		return false;
	}
	
	private static boolean isClassPathAffected(ILaunchConfiguration config, IPath oldPath) throws CoreException{
		IRuntimeClasspathEntry[] entries;
		try {
			entries = JavaRuntime.computeUnresolvedRuntimeClasspath(config);
			return isRuntimeClassPathEntriesAffected(entries, oldPath);
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log( e );
			return false;
		}		
	}
	
	public static boolean isRuntimeClassPathEntriesAffected(IRuntimeClasspathEntry[] entries, IPath oldPath){
		String attrib = null;
		String projName = null;
		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry entry = entries[i];
			attrib = entry.getPath() == null ? null
					: entry.getPath().toString();
			projName = entry.getJavaProject() == null ? null
					: entry.getJavaProject().getElementName();
			if(isAttributeChanged(attrib, oldPath) || isAttributeChanged(projName, oldPath)){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isAttributeChanged(String attrib, IPath path){
		if (attrib == null || path == null) return false;
		return path.isPrefixOf(new Path(attrib));
	}
	
	public static ILaunchConfiguration updateLaunchConfig(ILaunchConfiguration config, IPath oldPath, IPath newPath) throws CoreException{
		final ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
		updateAttributes(oldPath, newPath, wc);
		
		//classpath
		try {
			IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(config);
			List oldMementos = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, Collections.EMPTY_LIST);
			List<String> newMementos = new ArrayList<String>();
			boolean isChanged = updateClasspathEntries(entries, oldMementos, newMementos, oldPath, newPath);
			if (isChanged) wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, newMementos);
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log( e );
		}
		
		//JavaMigrationDelegate.updateResourceMapping(wc);		
		if (wc.isDirty()) {
			return wc.doSave();
		} else {
			return config;
		}
	}
	
	/*
	 * Use  IRuntimeClasspathEntry[] and oldMementos instead of entries[i].getMemento(), because
	 * when resource renamed instead of internalArchive you can have externalArchive.
	 */
	public static boolean updateClasspathEntries(IRuntimeClasspathEntry[] entries, List<String> oldMementos, List<String> newMementos, IPath oldPath, IPath newPath)
			throws CoreException {
		Assert.isNotNull(newMementos);
		Assert.isNotNull(entries.length == oldMementos.size());
		boolean isChanged = false;
		String attrib;
		String projName;
		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry entry = entries[i];
			attrib = entry.getPath() == null ? null
					: entry.getPath().toString();
			projName = entry.getJavaProject() == null ? null
					: entry.getJavaProject().getElementName();
			if(isAttributeChanged(attrib, oldPath)){
				isChanged = true;
				String memento = getUpdatedMemento(oldMementos.get(i), new Path(getUpdatedPath(attrib, oldPath, newPath)), oldPath);
				newMementos.add(memento);
			} else if(isAttributeChanged(projName, oldPath)){
				isChanged = true;
				String memento = getUpdatedMemento(oldMementos.get(i), newPath, oldPath);
				newMementos.add(memento);
			} else {
				newMementos.add(entries[i].getMemento());
			}			
		}
		return isChanged;
	}
	
	private static String getUpdatedMemento(String memento, IPath newPath, IPath oldPath) throws CoreException{
		String error_mess = "Error occured while updating classpath.";
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(memento)));
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
				
			org.w3c.dom.NodeList nodeList = doc.getElementsByTagName("runtimeClasspathEntry");
				
			for (int i = 0; i < nodeList.getLength(); i++) {
				org.w3c.dom.Node node = nodeList.item(i);
				NamedNodeMap map = node.getAttributes();
				Node changedNode = null;
				//if (entry instanceof RuntimeClasspathEntry){
					String[] attrNames = new String[]{"projectName", "externalArchive", "internalArchive", 
							"containerPath", "javaProject"};
					for (int j = 0; j < attrNames.length; j++) {
						changedNode = map.getNamedItem(attrNames[j]);
						if (changedNode != null){
							Path attrPath = new Path(changedNode.getNodeValue());
							if (oldPath.isPrefixOf(attrPath)){
								if (attrNames[j].equals("projectName") || attrNames[j].equals("javaProject")){
									changedNode.setNodeValue(newPath.lastSegment());
								} else {
									changedNode.setNodeValue(newPath.toString());
								}
							}
						}
					}
				//} else if (entry instanceof DefaultProjectClasspathEntry){
					if (node.getNodeType() == Node.ELEMENT_NODE)
					{
						Element element = (Element) node;
						NodeList mementoList = element.getElementsByTagName("memento");
						for(int j=0; j < mementoList.getLength(); j++)
						{
							map = mementoList.item(j).getAttributes();
							changedNode = map.getNamedItem("project");							
							if (changedNode != null){
								Path attrPath = new Path(changedNode.getNodeValue());
								if (oldPath.isPrefixOf(attrPath)){
									changedNode.setNodeValue(newPath.lastSegment());
								}
							}
						}
					}
				}
			//}
			domSource = new DOMSource(doc);
		    /*writer = new StringWriter();
		    result = new StreamResult(writer);
		    tf = TransformerFactory.newInstance();
		    transformer = tf.newTransformer();
		    transformer.transform(domSource, result);
			return writer.toString();*/
			String newMemento = LaunchingPlugin.serializeDocument(doc);
			return newMemento;
		} catch (ParserConfigurationException e) {
			IStatus status = new Status(IStatus.ERROR, HibernateConsolePlugin.ID, error_mess, e); 
			throw new CoreException(status);
		} catch (SAXException e) {
			IStatus status = new Status(IStatus.ERROR, HibernateConsolePlugin.ID, error_mess, e); 
			throw new CoreException(status);
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, HibernateConsolePlugin.ID, error_mess, e); 
			throw new CoreException(status);
		} catch (TransformerException e) {
			IStatus status = new Status(IStatus.ERROR, HibernateConsolePlugin.ID, error_mess, e); 
			throw new CoreException(status);
		}
	}

	private static void updateAttributes(IPath oldPath, IPath newPath,
			final ILaunchConfigurationWorkingCopy wc) throws CoreException {
		String attrib = null;
		for (int i = 0; i < pathKeys.length; i++) {
			attrib = wc.getAttribute(pathKeys[i], (String)null);
			if (isAttributeChanged(attrib, oldPath)){
				attrib = getUpdatedPath(attrib, oldPath, newPath);
				wc.setAttribute(pathKeys[i], attrib);
			}
		}
		
		boolean isChanged = false;
		for (int i = 0; i < pathListKeys.length; i++) {
			List<String> list = wc.getAttribute(pathListKeys[i], Collections.EMPTY_LIST);
			isChanged = false;
			List<String> newMappings = new ArrayList<String>();
			Iterator<String> iter = list.iterator();
			while ( iter.hasNext() ) {
				attrib = iter.next();
				if (isAttributeChanged(attrib, oldPath)){
					attrib = getUpdatedPath(attrib, oldPath, newPath);
					isChanged = true;
				}
				newMappings.add(attrib);
			}
			if (isChanged) wc.setAttribute(pathListKeys[i], newMappings);
		}
	}
	
	private static String getUpdatedPath(String attrib, IPath oldPath, IPath newPath){
		IPath attribPath = new Path(attrib);
		IPath newAttribPath = new Path("/");
		for (int j = 0; j < attribPath.segmentCount(); j++){
			if (!oldPath.isPrefixOf(attribPath.removeFirstSegments(j))){
				//add prefix
				newAttribPath = newAttribPath.append(attribPath.segment(j));
			} else {
				newAttribPath = newAttribPath.append(newPath);	//add new path instead of old path
				// add suffix
				newAttribPath = newAttribPath.append(attribPath.removeFirstSegments(j + oldPath.segmentCount()));
				break;
			}
		}
		return newAttribPath.toOSString();
	}
	
	public static ILaunchConfiguration[] getAffectedLaunchConfigurations(IPath path){
		ILaunchConfiguration[] configs = null;
		try {
			configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
			List<ILaunchConfiguration> list = new ArrayList<ILaunchConfiguration>();
			for(int i = 0; i < configs.length; i++) {//refactor only hibernate launch configurations
				if (!ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID.equals(configs[i].getType().getIdentifier())) continue;
				if (HibernateRefactoringUtil.isConfigurationAffected(configs[i], path)) list.add(configs[i]);
			}
			configs = list.toArray(new ILaunchConfiguration[list.size()]);
		}
		catch(CoreException e) {
			configs = new ILaunchConfiguration[0];
			HibernateConsolePlugin.getDefault().logErrorMessage( ERROR_MESS, e );
		}
		
		return configs;
	}
	
	/**
	 * @param changes - List of Change objects
	 * @return
	 */
	public static Change createChangesFromList(List<Change> changes, String name) {
		if (changes.size() == 0) {
			return null;
		} else if (changes.size() == 1) {
			return (Change) changes.get(0);
		} else {
			return new CompositeChange(name, (Change[])changes.toArray(new Change[changes.size()]));
		}
	}
}
