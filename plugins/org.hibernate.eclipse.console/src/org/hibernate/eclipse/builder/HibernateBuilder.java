/**
 * $Id$
 *
 * Public Domain code
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

	public static final String BUILDER_ID = HibernateConsolePlugin.ID + ".hibernateBuilder";
	
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
