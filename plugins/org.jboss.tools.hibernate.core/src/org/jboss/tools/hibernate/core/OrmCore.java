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
package org.jboss.tools.hibernate.core;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ILock;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.OrmModel;
import org.jboss.tools.hibernate.internal.core.OrmProject;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class. The class provides connection with Eclipse resources 
 * such as workspace and projects. 
 */
public class OrmCore extends Plugin {
	/**
	 * The plug-in identifier of the Orm core support
	 * (value <code>"org.jboss.tools.hibernate.core"</code>).
	 */
	public static final String PLUGIN_ID = "org.jboss.tools.hibernate.core" ;
	
	// add tau 18.02.2005
	public static final String ORM2NATURE_ID = "org.jboss.tools.hibernate.core.OrmHibernateNature"; 
	
	//The shared instance.
	private static OrmCore plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private IOrmModel ormModel;
	private IResourceChangeListener listener = null;

	private boolean flagLockResourceChangeListener = false;
	
	// add Tau 27.04.2005 for trace
	public static boolean TRACE = false;
	public static boolean TRACE_INT_CORE = false;
	public static boolean TRACE_VALIDATION = false; // add tau 19.06.2005 for Nikola
	
	// add Tau 23.11.2005	
	public static ILock lock = Platform.getJobManager().newLock();
	
	/**
	 * The constructor.
	 */
	public OrmCore() {
		super();
		plugin=this;
		
		if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("Start OrmCore()");
		
		try {
			resourceBundle = ResourceBundle.getBundle(PLUGIN_ID + ".OrmCoreResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		// TODO EXP del		
		//ormModel = new OrmModel();
		
		// TODO EXP del
		//initProjects();
	
		// add tau 03.05.2005
		
		// del tau 23.03.2006
		//updateListener();
	}
	
	// TODO EXP
	/*
	private void initProjects() {	
			IProject [] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects(); 
			for (int i = 0; i < projects.length; i++) {
				IProject project = projects[i];

				// edit Tau 28.04.2005
				if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("try initProject " + project.getName());
				
				if (project.isAccessible()) {
					try {
						if (project.hasNature(OrmCore.ORM2NATURE_ID)) {
							if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("project " + project.getName() + "is open and hasNature " + OrmCore.ORM2NATURE_ID);							
							create(project);								
						}
					} catch (CoreException e) {
						ExceptionHandler.logThrowableError(e, "init projects");
					}
				}
			}
	}
	*/
	
	
	public IOrmModel getOrmModel(){
		// TODO EXP del		
		//return ormModel;
		
		if (ormModel == null) {
			ormModel = new OrmModel();
		}
		return ormModel;		
	}
	
	public synchronized void remove(final IProject project){
		getOrmModel().removeOrmProject(project);

		if (TRACE || TRACE_INT_CORE ) ExceptionHandler.logInfo("remove for " + project.getName() + "ormModel.size()= " + getOrmModel().size());
		
		// TODO (tau->tau) ? removeListener
		// del 28.04.2005
		
		//if(ormModel.size()==0)removeListener();
	}
	
	/**
	 * Returns the ORM project corresponding to the given project
	 * @author troyas
	 * date 04.02.2005
	 * */	
	public synchronized IOrmProject create(final IProject project) throws CoreException {
		//XXX: Instantiate OrmProject only if there is no model for such project!
		
		if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("TRY CREATE OrmProject: " + project.getName());

		IOrmProject ormProject = getOrmModel().getOrmProject(project);		
		
		if(ormProject == null){

			if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("Create new OrmProject: " + project.getName());			
				
			ormProject = new OrmProject(project);
				
				/* delete tau 03.05.2005
				//listener created when ormModel contains no elements
				if(ormModel.size()==0){
					updateListener();
				}
				*/
			getOrmModel().addOrmProject(ormProject);
		}
		
		return ormProject;
	}
	
	/**
	 * @author troyas
	 * date 08.02.2005
	 * edit tau 21.11.2005 - public synchronized
	 */
	public synchronized  void removeListener(){
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null) return;
		if(listener!=null) {
			workspace.removeResourceChangeListener(listener);
			// add tau 21.11.2005
			if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("ORMCORE.removeListener() " + listener);			
			listener = null;
		}
	}
	
	/**
	 * @author troyas
	 * date 08.02.2005
	 * edit tau 21.11.2005 - public synchronized
	 */
	public synchronized  void updateListener() {
		if(listener != null) return;
		listener = new Listener();
		
		//edit Tau 27.04.2005 
		//ResourcesPlugin.getWorkspace().addResourceChangeListener(listener,IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
		//ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
		// edit 27.06.2005
		if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("ORMCORE.updateListener() " + listener);		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.PRE_BUILD);
	}
	
	/**
	 * @author troyas
	 * date 08.02.2005
	 * edit Tau 28.04.2005
	 */
	 class Listener implements IResourceChangeListener {
		public void resourceChanged(IResourceChangeEvent event) {

			// TODO (tau->tau) ????
			// add 25.01.2006
			if (isLockResourceChangeListener()){
				if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("ORMCORE.resourceChanged - return by "+ isLockResourceChangeListener());				
				return;
			} else {
				if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("ORMCORE.resourceChanged - DONE!!! "+ isLockResourceChangeListener());				
			}
			
			
			/*
			if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("ORMCORE.resourceChanged(...):" + 
					"Resource=" + event.getResource()+ 
					",BuildKind= " + event.getBuildKind()+
					",Source=" + event.getSource());
			*/			
		
			if (event.getType() == IResourceChangeEvent.PRE_CLOSE || event.getType() == IResourceChangeEvent.PRE_DELETE ){
				
				if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("ORMCORE.resourceChanged(PRE_CLOSE or PRE_DELETE):" + event.getResource());
				
				getOrmModel().removeOrmProject((IProject)event.getResource());
				
				//if(ormModel.size()==0)removeListener(); del Tau -> Requires Listener for Open Project ???
				
			//} else if (event.getType() == IResourceChangeEvent.POST_CHANGE ){
			// edit tau 27.06.2005	
			} else if (event.getType() == IResourceChangeEvent.PRE_BUILD ){				

				IResourceDelta delta = event.getDelta();
				if (TRACE || TRACE_INT_CORE )	ExceptionHandler.logInfo("ORMCORE.resourceChanged(PRE_BUILD):" + delta.getResource());				
				
				/* del tau 21.11.2005
				IResourceDelta[] affectedChildren = delta.getAffectedChildren(IResourceDelta.ADDED |IResourceDelta.CHANGED);
				for (int i = 0; i < affectedChildren.length; i++) {
					IResourceDelta deltaChildren = affectedChildren[i];
					IResource resource = deltaChildren.getResource();
					if (resource != null && resource.isAccessible()) {
						IProject project = resource.getProject();
						if (project != null) {
							try {
								if (project.hasNature(OrmCore.ORM2NATURE_ID)) {
									IOrmProject ormProject = ormModel.getOrmProject(project);
									if (ormProject == null){
										if (TRACE || TRACE_INT_CORE )ExceptionHandler.logInfo("ORMCORE.resourceChanged(PRE_BUILD)-> create(project):" + project.getName() + " end return.");										
										create(project);
										return;
									}
								}
							} catch (CoreException e) {
								ExceptionHandler.log(e, null);
							}
						}
					}
				}
				*/
				getOrmModel().resourcesChanged(delta);

			}
		}
	}
	 
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		getOrmModel().removeOrmProjects();
		removeListener();
	}

	/**
	 * Returns the shared instance.
	 */
	public synchronized static OrmCore getDefault() {
		//if(plugin==null) plugin=new OrmCore();
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = OrmCore.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	/**
	 * Add a log entry
	 * * @deprecated 
	 * 
	 * use ExceptionHandler.log(Throwable e, String message);
	 */
	public static IStatus log(Throwable e, String message) {
		IStatus status= new Status(
			IStatus.ERROR, 
			OrmCore.PLUGIN_ID, 
			IStatus.ERROR, 
			message, 
			e); 
		log(status); // edit tau 15.09.2005
        // added by Nick 10.08.2005
        //ProblemReportingHelper.reportProblem(status); del tau 15.09.2005 duplicate
        // by Nick
		return status; // add tau 31.03.2005
	}

	// add tau 05.04.2005
	public static void log(Throwable e, int severity) {
		IStatus status = null;		
		if (severity == IStatus.ERROR || severity == IStatus.WARNING || severity == IStatus.INFO) { 
			status = new Status(severity, OrmCore.PLUGIN_ID, severity, getResourceString("OrmCore.internal_error"), e);
		} else {
			status = new Status(IStatus.ERROR, OrmCore.PLUGIN_ID, IStatus.ERROR, getResourceString("OrmCore.internal_error"), e);			
		}
        log(status);
	}	
	
	/**
	 * Add a log entry
	 * * @deprecated 
	 * 
	 * use ExceptionHandler.log(String message);	 * 
	 */
	public static IStatus log(String message) {
		IStatus status= new Status(
			IStatus.INFO, 
			OrmCore.PLUGIN_ID, 
			IStatus.INFO, 
			message, 
			null); 
		log(status); // edit tau 15.09.2005
		return status; // add tau 31.03.2005		
	}	
	
	// add tau 05.04.2005
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
		
        // added by Nick 10.08.2005
		if (status != null)
        {
		    if (status.getSeverity() == IStatus.ERROR && status.getException() != null)
            {
                ProblemReportingHelper.reportProblem(status);
            }
        }
        // by Nick
    }
	
	// add Tau 27.04.2005 for trace
	static {
		
		String value = Platform.getDebugOption(PLUGIN_ID + "/debug");
		if (value != null && value.equalsIgnoreCase("true")) TRACE = true;
		
		value = Platform.getDebugOption(PLUGIN_ID + "/debug/ormcore");
		if (value != null && value.equalsIgnoreCase("true")) TRACE_INT_CORE = true;
		
		// add tau 19.06.2005 for Nikola
		value = Platform.getDebugOption(PLUGIN_ID + "/debug/validation");
		if (value != null && value.equalsIgnoreCase("true")) TRACE_VALIDATION = true;		
		
	}

	public synchronized boolean isLockResourceChangeListener() {
		return flagLockResourceChangeListener;
	}

	public synchronized void setLockResourceChangeListener(boolean flagLockResourceChangeListener) {
		if (TRACE || TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmCore.setLockResourceChangeListener("+isLockResourceChangeListener()+")");		
		this.flagLockResourceChangeListener = flagLockResourceChangeListener;
	}

}
