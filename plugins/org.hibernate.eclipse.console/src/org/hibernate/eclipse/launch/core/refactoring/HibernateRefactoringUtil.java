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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.JavaMigrationDelegate;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateRefactoringUtil {
	
	private static final String ERROR_MESS = "Error during refactoring";
	
	private static String[] stringAttribs = new String[]{
		IConsoleConfigurationLaunchConstants.CFG_XML_FILE,
		IConsoleConfigurationLaunchConstants.PROPERTY_FILE,
		HibernateLaunchConstants.ATTR_TEMPLATE_DIR,
		HibernateLaunchConstants.ATTR_OUTPUT_DIR,
		HibernateLaunchConstants.ATTR_REVERSE_ENGINEER_SETTINGS,		
		};
	
	private static String[] strListAttribs = new String[]{
		IConsoleConfigurationLaunchConstants.FILE_MAPPINGS,	
	};
	
	public static boolean isConfigurationChanged(ILaunchConfiguration config, IPath oldPath) throws CoreException{
		String attrib = null;
		for (int i = 0; i < stringAttribs.length; i++) {
			attrib = config.getAttribute(stringAttribs[i], (String)null);
			if (isAttributeChanged(attrib, oldPath)) 
				return true;			
		}
		
		for (int i = 0; i < strListAttribs.length; i++) {
			List list = config.getAttribute(strListAttribs[i], Collections.EMPTY_LIST);
			List newMappings = new ArrayList();
			Iterator iter = list.iterator();
			while ( iter.hasNext() ) {
				attrib = (String) iter.next();
				if (isAttributeChanged(attrib, oldPath)){
					return true;
				}
				newMappings.add(attrib);
			}
		}
		
		//classpath
		IRuntimeClasspathEntry[] entries;
		try {
			entries = JavaRuntime.computeUnresolvedRuntimeClasspath(config);
			for (int i = 0; i < entries.length; i++) {
				IRuntimeClasspathEntry entry = entries[i];
				if(entry.getClasspathProperty()==IRuntimeClasspathEntry.USER_CLASSES) {
					attrib = entry.getPath() == null ? null
							: entry.getPath().toString();
					if(isAttributeChanged(attrib, oldPath)){
						return true;
					}
				}
			}
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log( e );
		}
		
		return false;
	}
	
	public static boolean isAttributeChanged(String attrib, IPath path){
		if (attrib == null || path == null) return false;
		return path.isPrefixOf(new Path(attrib));
	}
	
	public static ILaunchConfiguration updateLaunchConfig(ILaunchConfiguration config, IPath oldPath, IPath newPath) throws CoreException{
		final ILaunchConfigurationWorkingCopy wc = config.getWorkingCopy();
		
		String attrib = null;
		for (int i = 0; i < stringAttribs.length; i++) {
			attrib = wc.getAttribute(stringAttribs[i], (String)null);
			if (isAttributeChanged(attrib, oldPath)){
				attrib = getUpdatedPath(attrib, oldPath, newPath);
				wc.setAttribute(stringAttribs[i], attrib);				
			}
		}
		
		boolean isChanged = false;
		for (int i = 0; i < strListAttribs.length; i++) {
			List list = wc.getAttribute(strListAttribs[i], Collections.EMPTY_LIST);
			isChanged = false;
			List newMappings = new ArrayList();
			Iterator iter = list.iterator();
			while ( iter.hasNext() ) {
				attrib = (String) iter.next();
				if (isAttributeChanged(attrib, oldPath)){
					attrib = getUpdatedPath(attrib, oldPath, newPath);
					isChanged = true;
				}
				newMappings.add(attrib);
			}
			if (isChanged) wc.setAttribute(strListAttribs[i], newMappings);
		}
		
		//classpath
		isChanged = false;
		IRuntimeClasspathEntry[] entries;		
		try {
			entries = JavaRuntime.computeUnresolvedRuntimeClasspath(config);
			List mementos = new ArrayList(entries.length);
			for (int i = 0; i < entries.length; i++) {
				IRuntimeClasspathEntry entry = entries[i];
				if(entry.getClasspathProperty()==IRuntimeClasspathEntry.USER_CLASSES) {
					attrib = entry.getPath() == null ? null
							: entry.getPath().toString();
					if(isAttributeChanged(attrib, oldPath)){
						attrib = getUpdatedPath(attrib, oldPath, newPath);
						IPath p = new Path(attrib).makeAbsolute();
						
						switch (entry.getClasspathEntry().getEntryKind()) {
						case IClasspathEntry.CPE_PROJECT:
							entry = new RuntimeClasspathEntry( JavaCore.newProjectEntry(p));
							break;
						default:
							entry = JavaRuntime.newArchiveRuntimeClasspathEntry(p);
							break;
						} 		
						
						entries[i] = entry;
						isChanged = true;
					}
				}
				mementos.add(entries[i].getMemento());
			}
			if (isChanged) wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, mementos);
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
	
	private static String getUpdatedPath(String attrib, IPath oldPath, IPath newPath){
		IPath attribPath = new Path(attrib);
		IPath newAttribPath = Path.EMPTY;
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
		return newAttribPath.toString();
	}
	
	public static ILaunchConfiguration[] getChangedLaunchConfigurations(IPath path){
		ILaunchConfiguration[] configs = null;
		try {
			configs = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
			ArrayList list = new ArrayList();
			for(int i = 0; i < configs.length; i++) {
				//ILaunchConfigurationWorkingCopy wc = configs[i].getWorkingCopy();
				if (HibernateRefactoringUtil.isConfigurationChanged(configs[i], path)) list.add(configs[i]);
			}
			configs = (ILaunchConfiguration[])list.toArray(new ILaunchConfiguration[list.size()]);
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
	public static Change createChangesFromList(List changes, String name) {
		if (changes.size() == 0) {
			return null;
		} else if (changes.size() == 1) {
			return (Change) changes.get(0);
		} else {
			return new CompositeChange(name, (Change[])changes.toArray(new Change[changes.size()]));
		}
	}
}
