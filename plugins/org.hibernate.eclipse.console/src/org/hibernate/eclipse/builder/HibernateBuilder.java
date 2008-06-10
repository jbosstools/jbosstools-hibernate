/**
 * $Id$/*
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
package org.hibernate.eclipse.builder;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * @author juozas
 *
 */
public class HibernateBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = HibernateConsolePlugin.ID + ".hibernateBuilder"; //$NON-NLS-1$
	
    protected IProject[] build(int kind, Map args, final IProgressMonitor monitor)
            throws CoreException {
        
        
     /*   IJavaProject jproject = JavaCore.create(getProject() );
        IPath location = jproject.getOutputLocation();
        IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(location);
        if(res != null){
            res.accept( new IResourceVisitor(){
                
                public boolean visit(IResource resource) throws CoreException {
                    
                    if( resource instanceof IFile ){
                        IFile file = (IFile)resource;
                        process(file.getLocation().toFile() );
                        file.refreshLocal(IResource.DEPTH_ZERO, monitor);
                    }
                    return true;
                }
                
                
            });
        }*/
        
        return null;
    }

   private void process(final File file){
/*
        InstrumentTask task = new InstrumentTask(){
            
             protected Collection getFiles() {
               return Collections.singleton(file);       
            
             }  
           }; 
         
           task.execute();
           */
    }
    
}

/**
* $Log$
* Revision 1.4  2006/07/07 13:51:13  mandersen
* JBIDE-276 - lgpl headers
*
* Revision 1.3  2005/06/18 23:37:46  maxcsaucdk
* checkstyle nazi
*
* Revision 1.2  2005/05/30 20:28:46  maxcsaucdk
* enablement of HibernateNature
*
* Revision 1.1  2005/05/24 20:21:36  maxcsaucdk
* commit for jbosside integration build
*
*/
