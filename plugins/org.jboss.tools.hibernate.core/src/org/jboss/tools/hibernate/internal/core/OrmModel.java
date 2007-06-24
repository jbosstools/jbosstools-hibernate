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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.SequencedHashMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IOrmModel;
import org.jboss.tools.hibernate.core.IOrmModelListener;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmModelEvent;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;



/**
 * @author Tau from Minsk
 *
 */
public class OrmModel implements IOrmModel {

	private SequencedHashMap root = new SequencedHashMap();
	private static final IOrmProject[] ROOT={};
	private SequencedHashMap listeners=new SequencedHashMap();
	
	// edit tau 21.11.2005
	//private String [] EXT_NO_CHANGE = {"class","jar","zip","ear","gif","war","rar","jpg","png","exe","jsp","css","html","js"};
	private String [] EXT_CHANGE = {"java","xml","properties"};
	// TODO EXP
	private boolean flagInitProjects = false;
	
	public OrmModel() {
	}
	
	
	public void addOrmProject(IOrmProject project) {
		if (root.get(project.getProject()) == null) {
			root.put(project.getProject(), project);
			fireOrmModelEvent(new OrmModelEvent(this, this, project, OrmModelEvent.AddProject));			
		}
	}
	
	public void removeOrmProject(IOrmProject ormProject) {
		removeOrmProject(ormProject.getProject());
	}	

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#getOrmProject(org.eclipse.core.resources.IProject)
	 */
	public IOrmProject getOrmProject(IProject project) {
		IOrmProject ormProject=(IOrmProject)root.get(project);
		return ormProject;
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#removeOrmProject(org.eclipse.core.resources.IProject)
	 */
	public void removeOrmProject(IProject project) {
		// edit tau 30.01.2006 ESORM-500
		//if(root.remove(project)!=null)	fireOrmModelEvent(new OrmModelEvent(this, this, project, OrmModelEvent.RemoveProject));			
		root.remove(project);
    	if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.removeOrmProject-> " + project.getName());		
		fireOrmModelEvent(new OrmModelEvent(this, this, project, OrmModelEvent.RemoveProject));		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#removeOrmProjects()
	 */
	public void removeOrmProjects() {
		if(root.size()!=0) {
			root.clear();
			fireOrmModelEvent(new OrmModelEvent(this, this, null, OrmModelEvent.RemoveALLProjects));
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#getOrmModels()
	 */
	public IOrmProject[] getOrmProjects() {
		// TODO EXP
		if (!flagInitProjects){
			initProjects();
			flagInitProjects = true;			
		}
		
		IOrmProject[] projects = (IOrmProject[])root.values().toArray(ROOT);
		return projects;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#size()
	 */
	public int size() {
		return root.size();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#addListener(org.jboss.tools.hibernate.core.IOrmModelListener)
	 */
	public void addListener(IOrmModelListener listener) {
        // tau 05.07.2005
		//listeners.put(listener,listener);
		Map synchro_map = Collections.synchronizedMap(listeners);
		synchro_map.put(listener,listener);
		
		//add tau 23.032006
		OrmCore.getDefault().updateListener(); // add IResourceChangeListener if no exit
	
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#removeListener(org.jboss.tools.hibernate.core.IOrmModelListener)
	 */
	public void removeListener(IOrmModelListener listener) {
        // tau 05.07.2005        
		//listeners.remove(listener);
		Map synchro_map = Collections.synchronizedMap(listeners);
		synchro_map.remove(listener);
		
		//add tau 23.032006
		if (synchro_map.isEmpty()){
			OrmCore.getDefault().removeListener(); // remove IResourceChangeListener
			removeOrmProjects();
			flagInitProjects = false;			
		}
		
	}
	
	public void fireOrmModelChanged(){
		fireOrmModelEvent(new OrmModelEvent(this,this, null, OrmModelEvent.ChangeModel));
	}

	protected void fireOrmModelEvent(final OrmModelEvent event){
		if(listeners.size() == 0 )return;
		
		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.fireOrmModelEvent()->Source="+event.getSource().toString());
		
		// add tau 05.07.2005
		final Map synchro_map = Collections.synchronizedMap(listeners);
		final Collection cl = synchro_map.values();
		
		synchronized(synchro_map){
			Iterator it=cl.iterator();
			while(it.hasNext()){
				IOrmModelListener listener = (IOrmModelListener)it.next();
				listener.modelChanged(event);
			}
		}
		
		// DEL TAU 22.07.2005 
		/*
		Job notifyProjectJob = new Job("Job for notify listeners of IProject") {
			protected IStatus run(IProgressMonitor monitor) {
				synchronized(synchro_map){
					// edit tau 05.07.2005
					//Iterator it=listeners.values().iterator();
					Iterator it=cl.iterator();
					
					while(it.hasNext()){
						IOrmModelListener listener = (IOrmModelListener)it.next();
						listener.modelChanged(event);
					}
				}
				return Status.OK_STATUS;
			}
		};
		notifyProjectJob.schedule();
		*/
	}

	// del tau 23.03.2006
//    public static int counter = 0;
//    
//    // added by Nick 17.06.2005
//    class ChangeProjectJob extends Job {
//        
//        private class WorkspaceProjects
//        {
//            IWorkspace workspace;
//            List projects = new ArrayList();
//        }
//    
//        public ChangeProjectJob(String name) {
//            super(name);
//        }
//
//        private List workspaces = new ArrayList();
//        
//        private List projects = new ArrayList();
//
//        protected IStatus run(IProgressMonitor monitor) {
//
//            counter++;
//            
//            IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
//                public void run(IProgressMonitor monitor) throws CoreException {
// 
//                    int size;
//                    
//                    synchronized (this) {
//                        size = projects.size();
//                    }
//                    
//                    while (size != 0)
//                    {
//                        IOrmProject project = null;
//                        synchronized (this) {
//                            project = (IOrmProject) projects.get(0);
//                            projects.remove(0);
//                        }
//                        
//                        if(project != null) 
//                        {
//                			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("project.resourcesChanged(), size=" + size);                        	
//                            project.resourcesChanged();
//                			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("project.resourcesChanged() END");                            
//                        }
//                    
//                        synchronized (this) {
//                            size = projects.size();
//                        }
//                    }
//                }
//            };
//            
//            try {
//                int size;
//                
//                synchronized (this) {
//                    size = workspaces.size();
//                }
//                
//                while (size != 0)
//                {
//                    WorkspaceProjects wp = null;
//                    synchronized (this) {
//                        wp = (WorkspaceProjects) workspaces.get(0);
//                        projects = wp.projects;
//                    }
//                    
//                    if (wp != null) {
//            			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("wp.workspace.run(...), size=" + size);                    	
//                        wp.workspace.run(runnable, new NullProgressMonitor());
//            			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("wp.workspace.run(...) END");                        
//                    }
//                    
//                    synchronized (this) {
//                        if (projects.size() == 0)
//                    	{
//                        	workspaces.remove(0);
//                            size = workspaces.size();
//                    	}
//                    }
//                }
//            } catch (CoreException e) {
//                ExceptionHandler.logThrowableError(e, null);
//            }
//            return Status.OK_STATUS;                            
//        }
//
//        public void addProject(IOrmProject project, IWorkspace workspace) {
//            synchronized (this) {
//                
//                // find workspace
//                int i = 0;
//                while (i < workspaces.size())
//                {
//                    if (workspaces.get(i) == workspace)
//                        break;
//                    else
//                        i++;
//                }
//                
//                WorkspaceProjects wp;
//                
//                if (i == workspaces.size())
//                {
//                    // this workspace was never added
//                    wp = new WorkspaceProjects();
//                    wp.workspace = workspace;
//                    wp.projects.add(project);
//                    workspaces.add(wp);
//                }
//                else
//                {
//                    // already added
//                    wp = (WorkspaceProjects) workspaces.get(i);
//                    if (!wp.projects.contains(project))
//                        wp.projects.add(project);
//                }
//            }
//        }
//
//    };
//
//    final ChangeProjectJob changeProjectJob = new ChangeProjectJob("Refreshing ORM Explorer");
    // by Nick

    
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmModel#resourcesChanged(org.eclipse.core.resources.IResourceDelta[])
	 */
    
    // edit tau 17.11.2005 - add synchronized 
    public synchronized void resourcesChanged(final IResourceDelta delta) {
    	
        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("...DELTA START - OrmModel.resourcesChanged(...)");
        
    	final Map<IOrmElement,IOrmProject> ResourceChanged = new HashMap<IOrmElement,IOrmProject>(); 
    	try {
    		// add 23.11.2005 tau
            if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("... DELTA lock(=" + OrmCore.lock.toString() + ").acquire(), Depth=" + OrmCore.lock.getDepth() + "- OrmModel.resourcesChanged(...)");    		
	        OrmCore.lock.acquire();
				delta.accept(new IResourceDeltaVisitor() {
				    public boolean visit(IResourceDelta delta) {
				    	
				        IResource resource = delta.getResource();
				        
				        if (resource.getType() == IResource.PROJECT) {

							IProject project = resource.getProject();
							
							/*
							if ((delta.getFlags() & IResourceDelta.CONTENT) == 0) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT - NO CHANGED on ");
								// return;
							}
							if ((delta.getFlags() & IResourceDelta.CONTENT) != 0) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT - CHANGED on ");
								// return;
							}
							if ((delta.getFlags() & IResourceDelta.OPEN) == 0) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT - NO OPEN ");
								// return;
							}
							*/
							
							if (((delta.getFlags() & IResourceDelta.OPEN) != 0)	&& project.isOpen()) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT - OPEN ");
								try {
									if (project.hasNature(OrmCore.ORM2NATURE_ID)) {
										OrmCore.getDefault().create(project);
									}
								} catch (CoreException e) {
									ExceptionHandler.logThrowableError(e, null);
								}
								return false; // NO visit children
							}
	
							if (((delta.getFlags() & IResourceDelta.OPEN) != 0) && !project.isOpen()) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT - CLOSE ");
								return false; // NO visit children
							}
	
							if (delta.getKind() == IResourceDelta.CHANGED) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT && IResourceDelta.CHANGED on "+ resource.getFullPath());
							} else if (delta.getKind() == IResourceDelta.ADDED) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT && IResourceDelta.ADDED on "+ resource.getFullPath());
								return false; // NO visit children // tau 01.12.2005
							} else if (delta.getKind() == IResourceDelta.OPEN) {
								if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE)
									ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),IResource.PROJECT && IResourceDelta.OPEN on "+ resource.getFullPath());
								return false; // NO visit children // tau 01.12.2005							
							}
							return true;
				        }				        
				        
				        //	only interested IResource.FILE resources (not Folder and ...)			        
				        if (resource.getType() != IResource.FILE || !resource.exists()) {
				            return true;                    	
				        }
				        
				        IFile file = (IFile) resource;
				        
				        // edit tau 21.11.2005
			        	boolean flagNo = true;				        
				        for (int i = 0; i < EXT_CHANGE.length && flagNo; i++) {
					        if (file.getFileExtension() != null && file.getFileExtension().toLowerCase().equals(EXT_CHANGE[i])) {
					        	flagNo = false;
					        }							
						}
				        if (flagNo) {
					        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...),->EXIT FOR Extension "+file.getFullPath());					        	
				        	return false; // NO visit children				        	
				        	
				        }
				        
				        
				        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...) on "+file.getFullPath() + " " + file.getLocalTimeStamp() + "/"+ file.getModificationStamp());
				        
				        //	only interested in changed resources (not added or removed)
			               if (delta.getKind() == IResourceDelta.CHANGED) {
						        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...), IResourceDelta.CHANGED on "+file.getFullPath());
			               } else if (delta.getKind() == IResourceDelta.ADDED) {
						        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...), IResourceDelta.ADDED on "+file.getFullPath());
						        return false;
			               } else if (delta.getKind() == IResourceDelta.OPEN) {
						        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...), IResourceDelta.OPEN on "+file.getFullPath());
						        return false;	
			               }
 
 
			               
			            //only interested in content changes
			               if ((delta.getFlags() & IResourceDelta.CONTENT) == 0) {
			            	   if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...), CONTENT NO CHANGED(->EXIT) on "+file.getFullPath());
			            	   return false;
			               }


						IOrmProject[] ormProjects=(IOrmProject[])root.values().toArray(ROOT);
						IOrmProject ormProjectForDelta = null;
						boolean flagResourceChanged = false;						
						
				        // find ormProject for resource						
						for (int i = 0; i < ormProjects.length; i++) {
							IOrmProject ormProject = ormProjects[i];
							IProject project = ormProject.getProject();
							if (project.equals(resource.getProject())) {
								ormProjectForDelta = ormProject;
								break;
							}
						}

						//TODO (!tau->tau) testing
						if (ormProjectForDelta == null) {
							// find ormProject for resource what is in ReferencedProjects 						
							for (int i = 0; i < ormProjects.length && ormProjectForDelta == null; i++) {
								IOrmProject ormProject = ormProjects[i];
				                IProject[] refProjects = ormProject.getReferencedProjects();
				                for (int j = 0; j < refProjects.length; j++) {
				                    IProject project = refProjects[j];
				                    IResourceDelta refProjectDelta = delta.findMember(project.getProjectRelativePath());				                    
				                    if(refProjectDelta != null){
										ormProjectForDelta = ormProject;
								        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...)-> refProject= " + project.getName());										
										break;
				                    }
				                }
							}
						}
				        
						if (ormProjectForDelta != null) {
						        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...) Poisk for: " + file.getFullPath());
						        
						        IMapping[] mappings = ormProjectForDelta.getMappings();

						        // find resource in mapping						        
						        for (int i = 0; i < mappings.length && !flagResourceChanged; i++) {
									IMapping mapping = mappings[i];
									if (mapping.getConfiguration().getResource().equals(resource)){
										// edit tau 18.11.2005
										//mapping.resourcesChanged();
										ResourceChanged.put(mapping, ormProjectForDelta);
										flagResourceChanged = true;
										if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...), ResourceChanged.put <- " + mapping.getName() + ",ResourceChanged.size()="+ ResourceChanged.size());										
								        break;
									}
						        }

						        // find resource in mappingStorages
						        for (int i = 0; i < mappings.length && !flagResourceChanged; i++) {
									IMapping mapping = mappings[i];
									IMappingStorage[] mappingStorages = mapping.getMappingStorages();
									for (int j = 0; j < mappingStorages.length; j++) {
										IMappingStorage mappingStorage = mappingStorages[j];
										if (mappingStorage.getResource().equals(resource)){
											//flagResourceChanged = mappingStorage.isResourceChanged();
											ResourceChanged.put(mappingStorage,ormProjectForDelta);
											flagResourceChanged = true;
									        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...), ResourceChanged.put <- " + mappingStorage.getName() + ",ResourceChanged.size()="+ ResourceChanged.size());											
									        break;
										}
									}
						        }

						        // find resource in pertsistentClasses						        
						        for (int i = 0; i < mappings.length && !flagResourceChanged; i++) {
									IMapping mapping = mappings[i];
									IPersistentClass[] pertsistentClasses = mapping.getPertsistentClasses();
									for (int j = 0; j < pertsistentClasses.length; j++) {
										IPersistentClass clazz = pertsistentClasses[j];
										ICompilationUnit clazzSourceCode = clazz.getSourceCode();
										if (clazzSourceCode != null && clazzSourceCode.getResource().equals(resource)){
											//flagResourceChanged = clazz.isResourceChanged();
											ResourceChanged.put(clazz, ormProjectForDelta);
											flagResourceChanged = true;
											if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...), ResourceChanged.put <- " + clazz.getName() + ",ResourceChanged.size()="+ ResourceChanged.size());											
									        break;
										}
									}
						        }

						}

				        /*
				        IMapping  maps[]= ormProject.getMappings();

				        for(int i=0;i<maps.length;++i){
				            IValidationService validationService = maps[i].getValidationService();
				            if(validationService != null){
				                try {
				                    validationService.incrementalRun(file);
				                } catch (Exception e) {
				                    ExceptionHandler.log(e,null);
				                }
				    
				            }
				        }
				        */
				        
				        return false; // No visit children too - tau 21.11.2005
				    }
				});

			    if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("...DELTA - OrmModel.resourcesChanged(...), ResourceChanged.size()="+ ResourceChanged.size());
				if (ResourceChanged.size() != 0 ){
					Set setOrmProject = new HashSet();
					Set ResourceChangedSet = ResourceChanged.entrySet();
					//if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...).run(...), ResourceChanged.size()=" + ResourceChanged.size() + ",ResourceChangedSet.size()="+ResourceChangedSet.size());				
					for (Iterator iter = ResourceChangedSet.iterator(); iter.hasNext();) {
						Map.Entry element = (Map.Entry) iter.next();
						Object key = element.getKey();
						boolean flagChanged = false;					
						if (key instanceof IMappingStorage) {
					        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...)->MappingStorage= "+((IMappingStorage) key).getName());						
							flagChanged = ((IMappingStorage) key).isResourceChanged();
						} else if (key instanceof IPersistentClass) {
					        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...)->PersistentClass= "+((IPersistentClass) key).getName());						
							flagChanged = ((IPersistentClass) key).isResourceChanged();
						} else if (key instanceof IMapping) {
					        //if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...)->Mapping= "+((IMapping) key).getName());						
							((IMapping) key).resourcesChanged();
							flagChanged = true;						
						}
						
						if (flagChanged){
					        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("OrmModel.resourcesChanged(...)=TRUE -> "+((IOrmElement) key).getName());							
							setOrmProject.add(element.getValue());
						}
					}
					
					for (Iterator iter = setOrmProject.iterator(); iter.hasNext();) {
						IOrmProject ormProject = (IOrmProject)iter.next();
						//ormProject.fireProjectBeforeChange(this);
				        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo(
				        		"OrmModel.resourcesChanged(...) ->->-> ormProject.fireProjectChanged(this)");						
						ormProject.fireProjectChanged(this, false);
					}
				}
		} catch (CoreException e) {
               ExceptionHandler.logThrowableError(e, null);
		} finally {
    		// add 23.11.2005 tau
	        OrmCore.lock.release();
            if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("... DELTA lock(=" + OrmCore.lock.toString() + ").release(), Depth=" + OrmCore.lock.getDepth() + " - OrmModel.resourcesChanged(...)");	        
		}
	    
	    //WorkspaceJob job = new WorkspaceJob("Refreshing files for ORM Explorer") {
			//public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		
		// DEADLOCK?
		/*
	    IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
		*/
	        
		//return Status.OK_STATUS;
		if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("...DELTA END  - OrmModel.resourcesChanged(...)");				
		return;
		 /*		        
			}

		};
		*/
		
         // job.setRule(...);
         //job.schedule();
		/*
		try {
	        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("--- START RUN ---- Refreshing files for ORM Explorer,ResourceChanged.size()="+ResourceChanged.size());			
			ResourcesPlugin.getWorkspace().run(workspaceRunnable, null);
	        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("--- END RUN   ---- Refreshing files for ORM Explorer");			
		} catch (CoreException e) {
            ExceptionHandler.log(e, null);
		}
		
	    if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo(".......DELTA END");
		*/	    
    }
    
    /*
	public void resourcesChanged(IResourceDelta delta) {
		final IOrmProject[] projects=(IOrmProject[])root.values().toArray(ROOT);
		
		boolean changed = false;
		for(int i=0;i<projects.length;++i){
			boolean changedProject = false; // add tau 16.09.2005			
			IResourceDelta projectDelta = delta.findMember(projects[i].getProject().getFullPath());
			if(projectDelta!=null){
				changedProject = true;
			}

            // changed by Nick 07.09.2005
			if (!changedProject) // try to detect changes in referenced projects
            {
                IProject[] refProjects = projects[i].getReferencedProjects();
                for (int j = 0; j < refProjects.length && !changed; j++) {
                    IProject project = refProjects[j];
                    
                    IResourceDelta refProjectDelta = delta.findMember(project.getFullPath());
                    if(refProjectDelta != null){
                    	changedProject = true;
                    }
                }
            }
            
            if (!changedProject) {
                projects[i]=null;
				if (OrmCore.TRACE ||OrmCore.TRACE_INT_CORE )	ExceptionHandler.logInfo("NO ResourcesChanged for " + projects[i].getName());                
            } else {
				if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE )	ExceptionHandler.logInfo("Yes ResourcesChanged for " + projects[i].getName());            	
				changed = true; // add tau 16.09.2005
            }
            // by Nick
            
		}
		
		if(changed){

			// edit to IWorkspaceRunnable tau 11.06.2005			
			final IWorkspace workspace = delta.getResource().getWorkspace();
//*------------------------------------			
			Job changeProjectJob = new Job("Refreshing ORM Explorer") {
				protected IStatus run(IProgressMonitor monitor) {
					
					IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
						public void run(IProgressMonitor monitor) throws CoreException {
					
							for(int i=0;i<projects.length;++i){
								if(projects[i]!=null) projects[i].resourcesChanged();
							}
						}
					};
					
					try {
						workspace.run(runnable, new NullProgressMonitor());
					} catch (CoreException e) {
						ExceptionHandler.log(e, null);
					}
					return Status.OK_STATUS;							
				}
			};
//*------------------				
            for(int i=0;i<projects.length;++i){
                if(projects[i]!=null)
    				if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("ChangeProjectJob.addProject(" + projects[i].getName()+")");                	
                    changeProjectJob.addProject(projects[i],workspace);
            }

			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("changeProjectJob.run(...)");            
            changeProjectJob.run(new NullProgressMonitor());
			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo("changeProjectJob.run(...) END");            
//            if (changeProjectJob.getState() == Job.NONE)
//                changeProjectJob.schedule();

		}
	}
*/

    /*
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	*/
    
	private void initProjects() {	
		IProject [] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects(); 
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];

			// edit Tau 28.04.2005
			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE )	ExceptionHandler.logInfo("Try initProject " + project.getName());
			
			if (project.isAccessible()) {
				try {
					if (project.hasNature(OrmCore.ORM2NATURE_ID)) {
						if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE )	ExceptionHandler.logInfo("project " + project.getName() + "is open and hasNature " + OrmCore.ORM2NATURE_ID);							
						OrmCore.getDefault().create(project);								
					}
				} catch (CoreException e) {
					ExceptionHandler.logThrowableError(e, "init projects");
				}
			}
		}
	}
	
	// add tau 21.03.2006
	public boolean isEmptyListeners() {
		return listeners.isEmpty(); 
	}
		 
	
}
