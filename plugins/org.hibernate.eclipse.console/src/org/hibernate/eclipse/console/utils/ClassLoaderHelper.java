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
package org.hibernate.eclipse.console.utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author max
 *
 */
public class ClassLoaderHelper {

	static public URLClassLoader getProjectClassLoader (IJavaProject project)
	{
		List<URL> pathElements = getProjectClassPathURLs(project);
		URL urlPaths[] = pathElements.toArray(new URL[pathElements.size()]);

		return new URLClassLoader(urlPaths, Thread.currentThread().getContextClassLoader() );
	}

	static public List<URL> getProjectClassPathURLs (IJavaProject project)
	{
		List<URL> pathElements = new ArrayList<URL>();

		try {
			IClasspathEntry paths[] = project.getResolvedClasspath(true);

			if (paths != null)
			{
				for (int i = 0; i < paths.length; i++)
				{
					IClasspathEntry path = paths[i];
					if (path.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
					{
						IPath simplePath = path.getPath();
						URL url = getRawLocationURL(simplePath);

						pathElements.add(url);
					}
				}
			}

			IPath location = getProjectLocation(project.getProject() );
			IPath outputPath = location.append(
				project.getOutputLocation().removeFirstSegments(1) );

			pathElements.add(outputPath.toFile().toURL() );
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return pathElements;
	}

	private static URL getRawLocationURL(IPath simplePath) throws MalformedURLException {
		File file = getRawLocationFile(simplePath);
		return file.toURL();
	}

	private static File getRawLocationFile(IPath simplePath) {
		IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(simplePath);
		File file = null;
		if(member!=null) {
			IPath rawLocation = member.getRawLocation();
			if(rawLocation==null) {
				rawLocation = member.getLocation();
				if(rawLocation==null) {
					throw new HibernateConsoleRuntimeException(HibernateConsoleMessages.ClassLoaderHelper_could_not_determine_physical_location_for + simplePath);
				}
			}
			file = rawLocation.toFile();
		} else {
			file = simplePath.toFile();
		}
		return file;
	}

	static public IPath getProjectLocation (IProject project)
	{
		if (project.getRawLocation() == null)
		{
			return project.getLocation();
		}
		else return project.getRawLocation();
	}

	/**
	 * Convenience method to get access to the java model.
	 */
	public static IJavaModel getJavaModel() {
		return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot() );
	}

	public static URL[] getRawLocationsURLForResources(IPath[] classpaths) throws MalformedURLException {
		URL[] l = new URL[classpaths.length];
		for (int i = 0; i < classpaths.length; i++) {
			l[i] = getRawLocationURL(classpaths[i]);
		}
		return l;
	}

	static public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
		IRuntimeClasspathEntry[] entries = JavaRuntime
		.computeUnresolvedRuntimeClasspath(configuration);
		entries = JavaRuntime.resolveRuntimeClasspath(entries, configuration);
		List<String> userEntries = new ArrayList<String>(entries.length);
		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry runtimeClasspathEntry = entries[i];
			if (runtimeClasspathEntry.getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				String location = runtimeClasspathEntry.getLocation();
				if (location != null) {
					userEntries.add(location);
				} else {
					//System.out.println("No location: " + runtimeClasspathEntry.getMemento());
				}
			} else {
				//System.out.println("Ignored " + runtimeClasspathEntry.getMemento());
			}
		}
		return userEntries.toArray(new String[userEntries.size()]);
	}

}
