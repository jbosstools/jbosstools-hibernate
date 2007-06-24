/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;


/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 30.08.2005
 * 
 */
public class DefaultClassLoaderFactory {
    
    public static ClassLoader create(IProject project, ClassLoader parentLoader) throws Exception
    {
        // approx. EclipseResourceUtil#getClassLoader method copy
        // can't use created with that method classloader as a parent here
        // because Hibernate stores parent classloader as used for some PC
        // classloader and use that to load everything skipping referenced projects
        // Hib Libs URLs are in project CP too
        List paths = EclipseResourceUtil.getClassPath(project);
        ArrayList<URL> l = new ArrayList<URL>();
        for (int i = 0; i < paths.size(); i++) {
        	// edit tau 03.04.2006 for Exception
            l.add(new java.io.File(paths.get(i).toString()).toURL());        	
        }
        //end copy
        
        
        
        IJavaProject prj = JavaCore.create(project);
        IPackageFragmentRoot[] entries = prj.getAllPackageFragmentRoots();
        if (entries != null)
        {
            for (int i = 0; i < entries.length; i++) {
                IPackageFragmentRoot entry = entries[i];
                if (entry.getKind() == IPackageFragmentRoot.K_SOURCE)
                {
                    IPath output = entry.getRawClasspathEntry().getOutputLocation();

                    // added by Nick 30.08.2005
                    if (output == null)
                    {
                        IJavaProject jProject = entry.getJavaProject();
                        if (jProject != null)
                        {
                            output = jProject.getOutputLocation();
                        }
                    }
                    // by Nick
                    
                    if (output != null)
                    {
                        IPath fullPath = ScanProject.relativePathToAbsolute(output,project);
                        if (fullPath != null)
                        {
                            if (fullPath.toFile().isDirectory())
                            {
                                String osPath = "file:///"+fullPath.toOSString();
                                if (!fullPath.hasTrailingSeparator())
                                    osPath += "/";
                                
                                URL url = new URL(osPath);
                                // added by Nick 30.08.2005
                                if (!l.contains(url))
                                // by Nick
                                    l.add(url);
                            }
                        }
                    }
                }
            }
        }

        URL[] urls = (URL[]) l.toArray(new URL[0]);
        return new URLClassLoader(urls,parentLoader);
    }
}
