/*
 * Created on 15-Oct-2004
 *
 *
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * @author max
 *
 */
public class ClassLoaderHelper {

	static public URLClassLoader getProjectClassLoader (IJavaProject project)
	{
		List pathElements = getProjectClassPathURLs(project);
		URL urlPaths[] = (URL[]) pathElements.toArray(new URL[pathElements.size()]);
		
//		for (int i = 0; i < urlPaths.length; i++)
//		{
//			System.out.println("[aop-core-plugin] class-loader-path: " + urlPaths[i]);
//		}
		
		return new URLClassLoader(urlPaths, Thread.currentThread().getContextClassLoader() );
	}
	
	static public List getProjectClassPathURLs (IJavaProject project)
	{
		ArrayList pathElements = new ArrayList();
		
		try {
			IClasspathEntry paths[] = project.getResolvedClasspath(true);
			
			if (paths != null)
			{
				for (int i = 0; i < paths.length; i++)
				{
					IClasspathEntry path = paths[i];					
					if (path.getEntryKind() == IClasspathEntry.CPE_LIBRARY)
					{
						System.out.println("Path: " + path);
						IPath simplePath = path.getPath();
						URL url = getRawLocationURL(simplePath);
						
						pathElements.add(url);
					}
				}
			}
			
			/*IPath aopPaths[] = AopClasspathContainer.getAopJarPaths();
			for (int i = 0; i < aopPaths.length; i++)
			{
				URL url = aopPaths[i].toFile().toURL();
				pathElements.add(url);
			}*/
			
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
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(simplePath);
		File file = null;
		if(resource!=null) {
			file = ResourcesPlugin.getWorkspace().getRoot().findMember(simplePath).getRawLocation().toFile();	
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
}
