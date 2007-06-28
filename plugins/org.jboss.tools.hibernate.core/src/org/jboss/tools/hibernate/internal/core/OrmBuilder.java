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
package org.jboss.tools.hibernate.internal.core;

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IValidationService;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateValidationProblem;


/**
 * @author Tau
 */
public class OrmBuilder extends IncrementalProjectBuilder {

	/**
	 * 
	 */
	public OrmBuilder() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {

        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("??? Build(...) -> Project = " + this.getProject().getName());
        }
		
		if (!getProject().hasNature(OrmCore.ORM2NATURE_ID)) return null;
		
		//add tau 21.03.2006
		if (OrmCore.getDefault().getOrmModel().isEmptyListeners()) return null;
		
		// add tau 11.11.2005
		IMarker[] javaModelMarkers = this.getProject().findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_INFINITE);
		for (int i = 0; i < javaModelMarkers.length; i++) {
			IMarker marker = javaModelMarkers[i];
			int severity = marker.getAttribute(IMarker.SEVERITY,IMarker.SEVERITY_INFO);
			if (severity == IMarker.SEVERITY_ERROR) {
		        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
		        	OrmCore.getPluginLog().logInfo("initBuild(...) -> Project = " + this.getProject().getName() +
		        		" have JAVA_MODEL_PROBLEM_MARKER, severity=ERROR" );
		        }
				return null;
			}
		}
		
		// add tau 22.09.2005 
		IOrmProject ormProject = OrmCore.getDefault().create(getProject());
		
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("!!! Build(...) -> Project = " + this.getProject().getName());
        }
        
        // edit tau 21.03.2006
        IResourceDelta delta = getDelta(getProject());        
		try {
			OrmCore.getDefault().removeListener();
			OrmCore.getDefault().setLockResourceChangeListener(true);
			if (kind == IncrementalProjectBuilder.FULL_BUILD || delta == null){
				fullBuild(ormProject, null, this);
				ormProject.fireProjectChanged(this, true);				
			} else {
				incrementalBuild(delta, monitor);
				ormProject.fireProjectChanged(this, false);				
			}
		} catch (Exception e){
			OrmCore.getPluginLog().logError(e.getMessage(),e);				
		} finally {
			OrmCore.getDefault().updateListener();
			OrmCore.getDefault().setLockResourceChangeListener(false);
		}
       
		
//		if (kind == IncrementalProjectBuilder.FULL_BUILD || getDelta(getProject()) == null) {
//            //fullBuild(ormProject, monitor); tau 22.09.2005
//			// edit tau 21.11.2005
//			try {
//				// edit tau 27.01.2006
//				OrmCore.getDefault().removeListener();
//				OrmCore.getDefault().setLockResourceChangeListener(true);
//				
//				fullBuild(ormProject, null, this);
//			} catch (Exception e){
//                ExceptionHandler.logThrowableWarning(e,e.getMessage());				
//			} finally {
//				
//				// edit tau 27.01.2006				
//				OrmCore.getDefault().updateListener();
//				OrmCore.getDefault().setLockResourceChangeListener(false);
//				
//				// add 27.01.2006
//				ormProject.fireProjectChanged(this, false);				
//			}
//         } else {
//            IResourceDelta delta = getDelta(getProject());
//			// changed by Nick 29.09.2005 - wrapped into try...catch()
//			// edit tau 21.11.2005 - add ...Listener()            
//			try {
//				OrmCore.getDefault().removeListener();
//				incrementalBuild(delta, monitor);
//			} catch (Exception e) {
//				ExceptionHandler.logThrowableWarning(e, e.getMessage());
//			} finally {
//				// add tau 27.01.2006			
//				OrmCore.getDefault().setLockResourceChangeListener(false);
//				OrmCore.getDefault().updateListener();
//				
//				// TODO (tau-> tau) 
//		        // add tau 27.01.2006 true or false ???
//		        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("build -> after incrementalBuild -> ormProject.fireProjectChanged(ormProject, true) )");            
//		        ormProject.fireProjectChanged(ormProject, true); // add tau 27.01.2006        
//			}
//
//         }
		
		return null;
	}
	
    // added by Nick 21.09.2005
    static public void deleteValidationMarkers(IOrmProject ormProject)
    {
        if (ormProject == null)
            return;
        
        // 27.05.2005 tau move this metod from HibernateValidationService.run()
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("deleteValidationMarkers on "+ormProject.getName());        
        }
        HibernateValidationProblem.deleteMarkers(ormProject.getProject());
    
        //ormProject.refresh();
        
    //       added by Nick 12.09.2005
        IProject[] refProjects = ormProject.getReferencedProjects();
        for (int i = 0; i < refProjects.length; i++) {
            IProject project = refProjects[i];
            if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
            	OrmCore.getPluginLog().logInfo("deleteValidationMarkers of refProjects on "+project.getName());            
            }
            HibernateValidationProblem.deleteMarkers(project);
        }
        // by Nick
    }
    // by Nick
    
    // add tau 25.01.2006
    class DeltaVisitor implements IResourceDeltaVisitor {
    	private IProgressMonitor monitor;
    	private IOrmProject ormProject;
    	
		public DeltaVisitor(IOrmProject ormProject, IProgressMonitor monitor) {
			this.monitor = monitor;
			this.ormProject = ormProject;			
		}
		
        public boolean visit(IResourceDelta delta) {
        	
            IResource resource = delta.getResource();
            if (resource.getType() != IResource.FILE || !resource.exists()) {
                return true;                    	
            }

            validation( ormProject, resource, monitor);
            
            return true; // visit children too
        }    		
    	
    }    
    
    // edit tau 25.01.2006
	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		
        // added by Nick 21.09.2005
        IOrmProject ormProject = OrmCore.getDefault().create(this.getProject());
        if (ormProject == null) return;
        
        // add tau 25.01.2066
        monitor.subTask("->Invoking Hibernate Validation Builder on " +  ormProject.getName());
        monitor.worked(1);

        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("!!! incrementalBuild on "+delta+","+delta.getResource().getName());        
        }
        
        delta.accept(new DeltaVisitor(ormProject, monitor));
        
        
        // tau 27.01.2006 - move high ^ in build(
        // add tau 24.01.2006
        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("incrementalBuild pre ormProject.fireProjectChanged(ormProject, true) )");            
    	//ormProject.fireProjectChanged(ormProject); // add tau 05.12.2005
        //ormProject.fireProjectChanged(ormProject, true); // add tau 27.01.2006        
        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("incrementalBuild past ormProject.fireProjectChanged(ormProject, true)");
        
        //ormProject.fireProjectChanged(this); del tau 05.12.2005        
         
     }
	
	private void validation(IOrmProject ormProject, IResource resource, IProgressMonitor monitor){
    	if(monitor.isCanceled()) throw new OperationCanceledException();

        IMapping  maps[]= ormProject.getMappings();
        
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE) {
        	OrmCore.getPluginLog().logInfo("OrmBuilder->visit->validation(...),maps[]= " + maps.length + ", " + resource.getFullPath());
        }
        
        for(int i=0;i<maps.length;++i){
        	
            IValidationService validationService = maps[i].getValidationService();
            if(validationService != null){
                try {
                    
                    if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
                    	OrmCore.getPluginLog().logInfo("OrmBuilder->validation->validationService.incrementalRun, Service(" + maps[i].getName()+") for " + resource.getFullPath());
                    }
                    
                    validationService.incrementalRun(resource);
                    
                } catch (Exception e) {
                	OrmCore.getPluginLog().logError(e);
                }
    
            }
        }    	
	}
	

	static public void fullBuild(IOrmProject ormProject, OrmProgressMonitor monitor, Object source) throws CoreException {
        
		//IOrmProject ormProject = OrmCore.getDefault().create(this.getProject()); // del tau 22.09.2005
        
		if (ormProject == null) return;
		
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("!!! fullBuild -> Project = " + ormProject.getName());		
        }
		
		// delete tau 14.09.2005
		//* open tau 19.09.2005 for Nikola

		//if (OrmCore.TRACE || OrmCore.TRACE_VALIDATION) { del 22.09.2005
			
		    // changed by Nick 21.09.2005 - code moved to deleteValidationMarkers()
		    deleteValidationMarkers(ormProject);
            // by Nick
            
			IMapping  maps[]= ormProject.getMappings();
			
			if (monitor != null) {
		        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
		        	OrmCore.getPluginLog().logInfo("fullBuild for Refresh F5");				
		        }
				monitor.setTaskParameters(1, maps.length);
			}
			
			for(int i=0;i<maps.length;++i){
				IValidationService validationService = maps[i].getValidationService();
				if(validationService != null){
					try {
						
						if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
							OrmCore.getPluginLog().logInfo("fullBuild, validationService.run for " + maps[i].getName());
						}
						
						validationService.run();
						
						// add tau 22.09.2005
						if (monitor != null) {
							monitor.worked();
						}
						
					} catch (Exception e) {
						OrmCore.getPluginLog().logError(e);
					}
		
				}
			}
		
		// move in refreshOrmProjectAction
		// del tau 02.12.2005 - from refreshMappingAction & refreshOrmProjectAction
		//ormProject.fireProjectBeforeChange(source);

		// TODO (tau-> tau) 27.01.2006 ???? TEST
		//ormProject.fireProjectChanged(ormProject, true); // add tau 05.12.2005 27.01.2006
			
			
		//ormProject.fireProjectChanged(source); // del tau 05.12.2005
		//ormProject.fireProjectChanged(); del tau. move to changeProjectJob in class OrmCore
     }

}
