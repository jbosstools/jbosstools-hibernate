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



import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.jboss.tools.hibernate.core.IConfigurationResource;
import org.jboss.tools.hibernate.core.IJavaLoggingProperties;
import org.jboss.tools.hibernate.core.ILog4JProperties;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IOrmProjectBeforeChangeListener;
import org.jboss.tools.hibernate.core.IOrmProjectChangedListener;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.OrmProjectEvent;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.properties.PropertySourceWrapper;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;


/**
 * @author alex
 *
 */
public class OrmProject extends AbstractOrmElement implements IOrmProject{
	private static final long serialVersionUID = 1L;
	private IProject project;
	private IOrmConfiguration ormConfiguration;
	private Log4JProperties log4jProperties;
	private IJavaLoggingProperties javaLoggingProperties;
	//private SequencedHashMap listeners=new SequencedHashMap();
	private HashMap<IOrmProjectChangedListener,IOrmProjectChangedListener> projectChangedListeners=new HashMap<IOrmProjectChangedListener,IOrmProjectChangedListener>();	

	// add tau 09.11.2005
	private HashMap<IOrmProjectBeforeChangeListener,IOrmProjectBeforeChangeListener> projectBeforeChangeListeners=new HashMap<IOrmProjectBeforeChangeListener,IOrmProjectBeforeChangeListener>();	
	
	private boolean initialized;
	private Map<String,IMapping> mappings = new HashMap<String,IMapping>();
	private static final IMapping[] MAPPINGS={};
	
	//TODO EXP 3d
	private boolean flagLoadMappings = false;
	protected boolean flagLoadOrmConfiguration = false;
	
	//TODO EXP 9d	
	private IJavaElement[] packageAllFragments;
	private HashMap<String,IPackageFragment[]> mapPackageFragments = new HashMap<String,IPackageFragment[]>();
//	private boolean dirty;
	
	/**
	 * 
	 */
	public OrmProject(IProject project){
		super();
		this.project=project;
		ormConfiguration=new OrmConfiguration(project);
		//log4jProperties=new Log4JProperties(project);
		//javaLoggingProperties=new JavaLoggingProperties(project);///add Gavrs 4.03.05
	}

	public IMapping[] getMappings() {
		//TODO EXP 3d		
		//init();
		if (!flagLoadMappings){
			loadMappings();
			flagLoadMappings = true;			
		}
		return (IMapping[])mappings.values().toArray(MAPPINGS);
	}
	
	/**
	 * @return Returns the project.
	 */
	public IProject getProject() {
		return project;
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#getName()
	 */
	public String getName() {
		return project.getName();
	}
	private synchronized final void init(){
		if(!initialized){
			initialized=true;
			//TODO EXP 2d
			//refresh(true); // do doMappingsUpdate - add tau 23.11.2005
			refresh(false);			
		}
	}
	
	//TODO EXP 3d
	// create tau 23.02.2006
	// edit tau 16.03.2006
	private synchronized void loadMappings()  {
		
		try {
			synchronize();
			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE) {
				OrmCore.getPluginLog().logInfo(project.getName()+"->.loadMappins()");
			}
			flagLoadMappings = true;
			scanHibernateMappings();
		} catch (Exception e) {
			OrmCore.getPluginLog().logError(e);
		}		
		

		// try {
			//synchronize();			
			
			//TODO RULE ??? for Workspace
			// tau 16.03.2006 del getWorkspace().run
			//ResourcesPlugin.getWorkspace().run( new IWorkspaceRunnable() {
				//public void run(IProgressMonitor monitor) throws CoreException {
					
			//        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmProject.loadMappins()");					
			//        flagLoadMappings = true;

		        // changed by Nick 10.08.2005
		        // exception handlers decoupled to make mappings parser independent
		        // of ORM2.properties file - ORMIISTUD-624
			        
			    /*
		        try{
					flagLoadOrmConfiguration = true;		        	
					ormConfiguration.reload();
		        } catch(Exception ex){
		            ExceptionHandler.logThrowableError(ex,null);
		        }
		        */
		        
		      //  try{
		      //  	scanHibernateMappings();		        	
			//	} catch(Exception ex){
			//		ExceptionHandler.logThrowableError(ex,null);
			//	}
				
		//	}}, null);
		//} catch (CoreException e) {
        // ExceptionHandler.logThrowableError(e, null);
		//}		

	}
	
	//TODO EXP 3d
	// create tau 23.02.2006
	private void scanHibernateMappings() throws Exception{
		Map<String,IMapping> backupedMappings = new HashMap<String,IMapping>();
        backupedMappings.putAll(mappings);
        mappings.clear();
        
		//changed by Nick 5.04.2005
		//was: List configs=ScanProject.getResources(".cfg.xml", project);
		// Nick Project should be scanned for all possible hibernate configs: .cfg.xml
		List configs=ScanProject.getResources(".cfg.xml", project,ScanProject.SCOPE_SRC|ScanProject.SCOPE_ROOT);;
		//by Nick
		
		// add tau 17.03.2006
		//TODO (tau->tau) dodelat
		OrmProgressMonitor monitor = new OrmProgressMonitor(OrmProgressMonitor.getMonitor());
		monitor.setTaskParameters(5,configs.size());
		
		Iterator it=configs.iterator();
		while(it.hasNext()){
			try{
				IFile res=(IFile)it.next();
				HibernateMapping mapping = new HibernateMapping(this, res);
				// changed by Nick 05.07.2005
                boolean exisitingMapping = backupedMappings.containsKey(mapping.getName());

                if (exisitingMapping)
                {
                    mapping = (HibernateMapping) backupedMappings.get(mapping.getName());
                }
                
                mappings.put(mapping.getName(), mapping);

            } catch(Exception ex){
            	OrmCore.getPluginLog().logError("loading hibernate config",ex);
			}
            monitor.worked();
		}
	}
	
	// TODO EXP 4d del
	public synchronized void refresh(final boolean doMappingsUpdate)  {
		loadMappings();
	}
	/*
	public synchronized void refresh(final boolean doMappingsUpdate)  {
		// edit tau 21.11.2005
		try {
			
			synchronize(); // add 17.01.2006			
			
			ResourcesPlugin.getWorkspace().run( new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
			        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmProject.refresh("+ doMappingsUpdate + ").run(...) START");					
				//	create/refresh mappings, configurations etc
				initialized=true;

		        // changed by Nick 10.08.2005
		        // exception handlers decoupled to make mappings parser independent
		        // of ORM2.properties file - ORMIISTUD-624
		        try{
					ormConfiguration.reload();
		        } catch(Exception ex){
		            ExceptionHandler.logThrowableError(ex,null);
		        }
		        
		        try{
		            //log4jProperties.reload();
					//javaLoggingProperties.reload();
		        	
		        	// TODO EXP 2d del?
		        	//scanForHibernateMappings(true, doMappingsUpdate);
		        	// TODO EXP 2d del?		        	
		        	//scanForHibernateMappings(false, doMappingsUpdate);		        	
					
				} catch(Exception ex){
					ExceptionHandler.logThrowableError(ex,null);
				}
		        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmProject.refresh("+ doMappingsUpdate + ").run(...) END");				
			}}, null);
		} catch (CoreException e) {
            ExceptionHandler.logThrowableError(e, null);
		}		
		// by Nick
	}
	*/
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#removeMapping(org.jboss.tools.hibernate.core.IMapping)
	 */
	public void removeMapping(IMapping mapping) {
		mappings.remove(mapping.getName());
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getInitialMapping(java.lang.String)
	 */
	public IMapping getInitialMapping(String natureId) {
		//ignore natureId while we have only Hibernate nature
		try{
			// edit tau 06.03.2006
			//init();
			getMappings();			
			if(mappings.isEmpty()){
				IFile cfg=createFileResource("hibernate.cfg.xml");
				HibernateMapping mapping=new HibernateMapping(this,cfg);
				mappings.put(mapping.getName(), mapping);
				return mapping;
			} else {
				return (IMapping)mappings.values().iterator().next();
			}
		} catch(Exception ex){
			throw new NestableRuntimeException(ex);
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#createMapping(java.lang.String)
	 */
	public IMapping createMapping(String natureId) {
		//ignore natureId while we have only Hibernate nature
		try{
			//XXX Generate new file name like hibernateNN.cfg.xml NN=01...99
			IFile cfg=null;
			for(int i=1;i<1000;++i){
				cfg=createFileResource("hibernate"+ String.valueOf(i)+".cfg.xml");
				if(!cfg.isLocal(IResource.DEPTH_ZERO)) break;
			}
			HibernateMapping mapping=new HibernateMapping(this,cfg);
			mappings.put(mapping.getName(), mapping);
			return mapping;
		} catch(Exception ex){
			throw new NestableRuntimeException(ex);
		}
	}
	private IFile createFileResource(String fileName) throws CoreException, IOException {
		IPath path = ScanProject.getSourcePath(project);
		if(path==null) path=project.getProjectRelativePath();
		path = path.append(fileName); 
		IFile resource = project.getFile(path);
		return resource;
	}
	
	// TODO EXP 4d del	
	/*
	private void scanForHibernateMappings(boolean reloadAll, boolean doMappingsUpdate) throws Exception{
		Map backupedMappings = new HashMap();
        backupedMappings.putAll(mappings);
        mappings.clear();
		//changed by Nick 5.04.2005
		//was: List configs=ScanProject.getResources(".cfg.xml", project);
		// Nick Project should be scanned for all possible hibernate configs: .cfg.xml
		List configs=ScanProject.getResources(".cfg.xml", project,ScanProject.SCOPE_SRC|ScanProject.SCOPE_ROOT);;
		//by Nick
		Iterator it=configs.iterator();
		while(it.hasNext()){
			try{
				IFile res=(IFile)it.next();
				HibernateMapping mapping=new HibernateMapping(this,res);
				// changed by Nick 05.07.2005
                boolean exisitingMapping = backupedMappings.containsKey(mapping.getName());

                if (exisitingMapping)
                {
                    mapping = (HibernateMapping) backupedMappings.get(mapping.getName());
                }
                
                if (reloadAll || !exisitingMapping)
                {
        	        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmProject.scanForHibernateMappings(...)->mapping.reload("+doMappingsUpdate+"): " + mapping.getName());                	
                    mapping.reload(doMappingsUpdate);
                }
                
                mappings.put(mapping.getName(), mapping);
				// by Nick
            } catch(Exception ex){
				ExceptionHandler.logThrowableError(ex,"loading hibernate config");
			}
		}
	}
	*/
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#moveMapping(org.jboss.tools.hibernate.core.IMapping, IPath)
	 */
	public void moveMapping(IMapping mapping, IPath newPath)  throws CoreException {
		String oldId=mapping.getName();
		mappings.remove(oldId);
		mapping.moveTo(newPath);
		mappings.put(mapping.getName(), mapping);
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getConfiguration()
	 */
	public IOrmConfiguration getOrmConfiguration() {
		//TODO EXP 5d
		if (!flagLoadOrmConfiguration) {
			try{
				ormConfiguration.reload();
				((OrmConfiguration)ormConfiguration).setPropertyDescriptorsHolder(OrmPropertyDescriptorsHolder.getInstance(this));				
				flagLoadOrmConfiguration = true;
	        } catch(Exception e){
	        	OrmCore.getPluginLog().logError(e);
	        }
		}		
		
		//TODO EXP 3d		
		//init();
		/*
		if (!flagLoadOrmConfiguration) {
			try{
				ormConfiguration.reload();
				flagLoadOrmConfiguration = true;				
	        } catch(Exception e){
	            ExceptionHandler.logThrowableError(e, null);
	        }
		}
		*/
		
		return ormConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getLog4jProperties()
	 */
	public ILog4JProperties getLog4jProperties() {
		init(); 
		return log4jProperties;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getJavaLoggingProperties()
	 */
	public IJavaLoggingProperties getJavaLoggingProperties() {
		init(); 
		return javaLoggingProperties;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#addOrmModelEventListener(org.jboss.tools.hibernate.core.IOrmProjectListener)
	 */
	public void addChangedListener(IOrmProjectChangedListener listener) {
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("OrmProject.addChangedListener: " + listener);
        }
        
        // tau 05.07.2005
		//listeners.put(listener,listener);
		Map<IOrmProjectChangedListener,IOrmProjectChangedListener> synchro_map = Collections.synchronizedMap(projectChangedListeners);
		// add tau 02.08.2005
		synchronized(synchro_map)
		{
			synchro_map.put(listener,listener);			
		}		
	}
	
    // tau 09.11.2005
	public void addBeforeChangeListener(IOrmProjectBeforeChangeListener listener) {
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("OrmProject.addBeforeListener: " + listener);
        }
		Map<IOrmProjectBeforeChangeListener,IOrmProjectBeforeChangeListener> synchro_map = Collections.synchronizedMap(projectBeforeChangeListeners);
		synchronized(synchro_map)
		{
			synchro_map.put(listener,listener);			
		}		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#removeOrmModelEventListener(org.jboss.tools.hibernate.core.IOrmProjectListener)
	 */
	public void removeChangedListener(IOrmProjectChangedListener listener) {
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE )  {
        	OrmCore.getPluginLog().logInfo("OrmProject.removeChangedListener: " + listener);
        }
        // tau 05.07.2005        
		//listeners.remove(listener);
		Map synchro_map = Collections.synchronizedMap(projectChangedListeners);
		// add tau 02.08.2005		
		synchronized(synchro_map)
		{
			synchro_map.remove(listener);			
		}
	}
	
	public void removeBeforeChangeListener1(IOrmProjectBeforeChangeListener listener) {
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) { 
        	OrmCore.getPluginLog().logInfo("OrmProject.removeBeforeListener: " + listener);
        }
		Map synchro_map = Collections.synchronizedMap(projectBeforeChangeListeners);
		synchronized(synchro_map)
		{
			synchro_map.remove(listener);			
		}
		
	}
	
	// edit by tau 19.07.2005 add "Object source"
	public void fireProjectChanged(Object source, boolean flagUpdate) {
			fireOrmProjecChangedEvent(new OrmProjectEvent(this,source), flagUpdate);			
	}
	
	protected void fireOrmProjecChangedEvent(final OrmProjectEvent event, boolean flagUpdate){
		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
			OrmCore.getPluginLog().logInfo("fireOrmProjecChangedtEvent() START, Source="+event.getSource().toString());		
		}
		Map synchro_map = Collections.synchronizedMap(projectChangedListeners);
		Set cl = synchro_map.keySet();
		int i = 0;
		synchronized(synchro_map)
		{
			Iterator it=cl.iterator();
			while(it.hasNext()){
				IOrmProjectChangedListener listener = (IOrmProjectChangedListener)it.next(); // edit 04.08.2005
				i++;
				if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
					OrmCore.getPluginLog().logInfo("OrmProject.fireOrmProjecChangedtEvent()-> listener-" 
							+ i + " (" + listener.toString() + ")");
				}
				
				listener.projectChanged(event, flagUpdate);
				
											/* del tau 04.05.2008
											Platform.run(new ISafeRunnable() {
												public void handleException(Throwable exception) {
													ExceptionHandler.log(exception, "Exception occurred in listener of OrmProject events");
												}
												public void run() throws Exception {
													listener.projectChanged(event);
												}
											});
											*/
			}
		}		
	}
	
	protected void NOfireOrmProjecBeforeChangedtEvent(final OrmProjectEvent event){
		
		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
			OrmCore.getPluginLog().logInfo("OrmProject.fireOrmProjecBeforeChangedtEvent() START, " + "Source="+event.getSource().toString());		
		}
		
		Map<IOrmProjectBeforeChangeListener,IOrmProjectBeforeChangeListener> synchro_map = Collections.synchronizedMap(projectBeforeChangeListeners);
		Set cl = synchro_map.keySet();
		int i = 0;
		synchronized(synchro_map) {
			Iterator it=cl.iterator();
			while(it.hasNext()){
				IOrmProjectBeforeChangeListener listener = (IOrmProjectBeforeChangeListener)it.next(); // edit 04.08.2005
				
				i++;
				if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
					OrmCore.getPluginLog().logInfo("OrmProject.fireOrmProjecBeforeChangedtEvent()-> listener-"					+ i + " (" + listener.toString() + ")");
				}
				listener.projectBeforeChange(event);
			}
		}		
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitOrmProject(this,argument);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmProject#getMapping(java.lang.String)
	 */
	public IMapping getMapping(String id) {
		return (IMapping)mappings.get(id);
	}
	
	/*
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	*/

    //added By Nick 19.05.2005
    public void resourcesChanged()
    {
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
        	OrmCore.getPluginLog().logInfo("OrmProject.resourceChanged");
        }
		
        if (!initialized) return;
        
        try {
			synchronize(); // add 17.01.2006 for ESORM-484
			
			//TODO EXP 4d        	
            //scanForHibernateMappings(false, true); // do doMappingsUpdate - add tau 17.11.2005
			scanHibernateMappings();
			
        } catch (Exception e1) {
        	OrmCore.getPluginLog().logError(e1.getMessage(),e1);
        }
        IMapping[] mappings = this.getMappings();
        for (int i = 0; i < mappings.length; i++) {
            IMapping mapping = mappings[i];
            mapping.resourcesChanged();
        }
		
        IConfigurationResource[] configs = new IConfigurationResource[3];
        configs[0] = ormConfiguration;
        configs[1] = log4jProperties;
        configs[2] = javaLoggingProperties;
  
        for (int i = 0; i < configs.length; i++) {
            IConfigurationResource resource = configs[i];
            if (resource != null)
            {
                if (resource.getResource() != null && resource.getResource().isLocal(IResource.DEPTH_ZERO) && resource.resourceChanged())
                    try {
				        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
				        	OrmCore.getPluginLog().logInfo("Reloading resource: " + resource);
				        }
                        resource.reload();
                    } catch (Exception e) {
                    	OrmCore.getPluginLog().logError("Exception refreshing resources...",e);
                }
            }
        }
     

// add by yk 08.06.2005.
        // TODO (tau->tau) WHY?        
        updatePersistentClassMappings();
// add by yk 08.06.2005 stop.
        
        // TODO (tau->tau) del? 27.01.2006        
		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
			OrmCore.getPluginLog().logInfo("STOP? project.resourcesChanged()");        
		}
		fireProjectChanged(this, false);
    }
    
    
    // add by yk 08.06.2005.
    /**
     * Update fields of nested components, which user has changed.
     */
    private void updatePersistentClassMappings()
    {
    	long length = getMappings().length;
    	IMapping[] mappings = getMappings();
    	UpdateMappingVisitor updatevisitor = new UpdateMappingVisitor(null);
        // refresh(); or...
        for(int imap = 0; imap < length; imap++)
        {
        	updatevisitor.setProjectMapping(mappings[imap]);
        	updatevisitor.doMappingsUpdate(mappings[imap].getPersistentClassMappings());
        }
    }
    // add by yk 08.06.2005 stop.

	/**
	 * @param ormConfiguration The config to set.
	 * akuzmin 13.07.2005
	 */
	public IOrmConfiguration newConfigiration() {
		this.ormConfiguration = new OrmConfiguration(project);
		((OrmConfiguration)ormConfiguration).setPropertyDescriptorsHolder(OrmPropertyDescriptorsHolder.getInstance(this));
		return ormConfiguration;
	}

    // added by Nick 07.09.2005
    private void traverseReferencedProjects(IProject project, List<IProject> holder) {
        if (project != null) {
            try {
                IProject[] refProjects = project.getReferencedProjects();
                for (int i = 0; i < refProjects.length; i++) {
                    IProject refProject = refProjects[i];
                    
                    if (refProject.isAccessible() && !holder.contains(refProject)) {
                        holder.add(refProject);
                        traverseReferencedProjects(refProject,holder);
                    }
                }
            } catch (CoreException e) {
            	OrmCore.getPluginLog().logError(e.getMessage(),e);
            }
        }
    }
    
    /* 
     * Returns array of open and existing IProject instances referenced by this ORM Project.
     * Returns empty array if none.
     */
    public IProject[] getReferencedProjects() {
        ArrayList<IProject> list = new ArrayList<IProject>();
        if (this.project != null) {
            traverseReferencedProjects(this.project,list);
        }
        return list.toArray(new IProject[0]);
    }
    // by Nick
    
//  akuzmin 23.08.2005    
    public IPropertySource2 getSpringConfiguration() {
        return new PropertySourceWrapper(((OrmConfiguration)ormConfiguration), OrmPropertyDescriptorsHolder.getSpringPropertiesDescriptors());
    }
    
    
	// add tau 17.01.2006 for ESORM-389 -  No Tree in ORM Expl. after proj. import
    public void synchronize() throws CoreException {
    	boolean isSynchronized = project.isSynchronized(IResource.DEPTH_INFINITE);
    	//if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo(project.getName()+".synchronize() = " + isSynchronized);    	
		if (!isSynchronized) {
	        	if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
	        		OrmCore.getPluginLog().logInfo("OrmProject.synchronize() -> project.refreshLocal(...)");			
	        	}
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
    }
    
    //TODO EXP8 tau
    public synchronized void setDirty(boolean dirty){
    	if (dirty) {
        	packageAllFragments = null;
        	mapPackageFragments.clear();
    	}
//    	this.dirty = dirty;
    }

    //TODO EXP9d tau
	private IJavaElement[] getAllPackageFragments() throws JavaModelException {
		if (packageAllFragments == null) {
			IJavaProject javaProject = JavaCore.create(project);
			packageAllFragments = javaProject.getAllPackageFragmentRoots();
		}
		return packageAllFragments;
	}

    //TODO EXP9d tau	
	public IJavaElement[] getPackageFragments(String packageName, boolean findAll) throws JavaModelException {
		IPackageFragment [] packageFragments = (IPackageFragment[]) mapPackageFragments.get(packageName);
		if (packageFragments == null) {
			packageFragments = ScanProject.findPackageFragments(packageName, getAllPackageFragments(), true);
			mapPackageFragments.put(packageName, packageFragments);
		}
		
		if (findAll) {
			return (IJavaElement[]) packageFragments;
		} else if (packageFragments.length != 0){
			return  (IJavaElement[]) new IPackageFragment [] {packageFragments[0]};
		} else
			return (IJavaElement[]) new IPackageFragment [] {};
	}
    
}
