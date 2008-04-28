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
package org.jboss.tools.hibernate.view;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingConfiguration;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.dialog.ModelCheckedTreeSelectionDialog;
import org.jboss.tools.hibernate.internal.core.hibernate.validation.HibernateValidationProblem;
import org.jboss.tools.hibernate.internal.core.util.RuleUtils;
import org.jboss.tools.hibernate.wizard.treemodel.HibernateJarsLabelProvider;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModel;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModelContentProvider;
import org.osgi.framework.Bundle;



/**
 * @author Tau from Minsk
 *
 */
public class RemoveOrmNatureAction {
	private IProject project;
	
	//add tau 07.04.2006
	private HashSet<String> filesForRemoveHibernateSet;
	private Map<String,IResource> filesForSelectMap;	
	
	//add tau 12.04.2006
	private boolean removeJars = false;
	private boolean removeMappings = false;
	//add 14.04.2006
	private boolean flagResolvedClasspath = false;	
	
	private ResourceBundle BUNDLE = ResourceBundle.getBundle(RemoveOrmNatureAction.class.getPackage().getName() + ".orm");

	public RemoveOrmNatureAction(IProject project) {
		this.project = project;
	}
	
	public void runAction() {
//		// $changed$ by Konstantin Mishin on 2005/08/09 fixed fo ORMIISTUD-613
//		// #changed# by Konstantin Mishin on 2005/08/18 fixed for ORMIISTUD-644
//		//String dialogButtonLabels[] = {"OK", "Cancel"};
//		//MessageDialog messageDialog = new MessageDialog(ViewPlugin.getActiveWorkbenchShell(), "Confirm Remove Hibernate Capabilities", null,
//				//"Do you want to remove the Hibernate Capabilities from \""+project.getName()+"\"?",
//				//MessageDialog.QUESTION,dialogButtonLabels, 0);
//		
//		RemoveConfirmCheckDialog messageDialog = new RemoveConfirmCheckDialog(ViewPlugin.getActiveWorkbenchShell(),
//				BUNDLE.getString("RemoveOrmNatureAction.DialogTitle"),
//				MessageFormat.format(BUNDLE.getString("RemoveOrmNatureAction.DialogQuestion"),new Object[]{project.getName()}),
//				new String[]{BUNDLE.getString("RemoveOrmNatureAction.Variant1"),BUNDLE.getString("RemoveOrmNatureAction.Variant2")});
//		messageDialog.setSelection(true, false);
		
		filesForSelectMap = getRemoveHibernateFiles();
		Set<String> filesForSelectSetKey = filesForSelectMap.keySet();
		String [] filesForSelect = new String[filesForSelectSetKey.size()];
		filesForSelectSetKey.toArray(filesForSelect);
		boolean removeFiles = selectRemoveFiles(filesForSelect, new String []{});
		
//		if (messageDialog.open()!=0) return;
		if (!removeFiles) return;		
		
		// add 05.04.2006
		//if (!(selectRemoveHibernateFiles())) return;
		
		try {
			// TODO (!tau - tau) ???
			// Resource is out of sync with the file system
			//org.eclipse.core.internal.resources.ResourceException
			deleteNatureFromProject(OrmCore.ORM2NATURE_ID);
		} catch (CoreException e) {
			ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null,null);
		}
	}
	
	// #changed# by Konstantin Mishin on 2005/08/18 fixed for ORMIISTUD-644
	//private void deleteNatureFromProject(String natureId) throws CoreException {
	private void deleteNatureFromProject(String natureId) throws CoreException {
	// #changed# by Konstantin Mishin on 2005/08/18 fixed for ORMIISTUD-644
		if (project == null) return; 
		if (!project.hasNature(natureId)) return;
		// #added# by Konstantin Mishin on 2005/08/25 fixed for ORMIISTUD-644
	    if(removeMappings) 
	    	this.removeComfAndMappFiles();	    
		// #added#
		IProjectDescription description = project.getDescription();
		String[] ids = description.getNatureIds();
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].equals(natureId)){
				String [] newIds = new String [ids.length -1];
				System.arraycopy(ids, 0, newIds, 0, i);
				System.arraycopy(ids, i + 1, newIds, i, ids.length - i - 1);
				
				/* Delete tau -> Other error-natures goto error status.
				IStatus status = ResourcesPlugin.getWorkspace().validateNatureSet(ids);

				// check the status and decide what to do
				if (status.getCode() == IStatus.OK) {
					description.setNatureIds(newIds);
					project.setDescription(description, null);
				} else {
					// 	TO DO (tau->tau) raise a user error

				}
				*/
				description.setNatureIds(newIds);
				project.setDescription(description, null);
				
				// 29.04.2005 tau
				OrmCore.getDefault().remove(project);
				
				// add 22.06.2005 tau
				HibernateValidationProblem.deleteMarkers(project);
								
				if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("Nature deleted for " + project.getName() );				
				
			}
		}

	    if(removeJars) {
			// del tau 12.04.2006
	    	//IJavaProject javaProject =  JavaCore.create(project);	    
	    	//this.removeFileLibJar(javaProject);
		
	    	removeHibernateFiles();	    	
	    }

	}
	
	
	private void removeHibernateFiles() throws CoreException {
		IJavaProject javaProject = JavaCore.create(project);		
		IClasspathEntry[] rawClasspath = javaProject.getRawClasspath();
		List<IClasspathEntry> newClasspath = new ArrayList<IClasspathEntry>();
		final List<IResource> filesForDelete = new ArrayList<IResource>();		
		
		for (Iterator iter = filesForRemoveHibernateSet.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			IResource fileInFolder = (IResource) filesForSelectMap.get(element);
			if (fileInFolder != null && fileInFolder.isAccessible()){
				ResourceAttributes attributesResource = fileInFolder.getResourceAttributes();
				if ((attributesResource != null) && !attributesResource.isReadOnly()) {
					IPath fullPath = fileInFolder.getFullPath();						
					for (int i = 0; i < rawClasspath.length; i++) {
						IClasspathEntry entry = rawClasspath[i];
						if (entry != null) {
							IPath pat = entry.getPath();
							if (pat.equals(fullPath)){
								rawClasspath[i] = null;
								break;
							}
						}
					}
					//fileInFolder.delete(IResource.NONE, null);
					filesForDelete.add(fileInFolder);					
				}
			}
		}
		
		if (!flagResolvedClasspath) {
			for (int i = 0; i < rawClasspath.length; i++) {
				IClasspathEntry entry = rawClasspath[i];
				if (entry != null){
					newClasspath.add(entry);
				}
			}
			
			IClasspathEntry[] entry = new IClasspathEntry[newClasspath.size()];
			newClasspath.toArray(entry);
			javaProject.setRawClasspath(entry, new NullProgressMonitor());
		}
		
		
		IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
			public void run(IProgressMonitor monitor) throws CoreException {
				for (Iterator iter = filesForDelete.iterator(); iter.hasNext();) {
					IResource resource = (IResource) iter.next();
					resource.delete(IResource.NONE, null);					
				}
			}
		};
		
		project.getWorkspace().run(runnable, project, IResource.NONE, new NullProgressMonitor());
		
		//add tau 05.05.2006
		QualifiedName addQualifiedName = new QualifiedName(ViewPlugin.PLUGIN_ID,"addhibernatejars");
		javaProject.getResource().setPersistentProperty(addQualifiedName, null);		
		
		project.refreshLocal(IResource.DEPTH_INFINITE,new NullProgressMonitor());
	}

	// add tau 12.04.2006
	private Map<String,IResource> getRemoveHibernateFiles() {	
		IJavaProject javaProject = JavaCore.create(project);		
		
		//boolean flagResolvedClasspath = false; // add tau 11.10.2005
		
		String[] fileLibJars = getPersistentPropertyHibernateFiles();
		
		if (fileLibJars.length == 0) {
		    Bundle bundle =	Platform.getBundle("org.jboss.tools.hibernate.hblibs");
		    Path path = new Path("/lib");
		    URL fileURL = FileLocator.find(bundle, path, null);
			URL fileResolveURL = null;
			try {
				fileResolveURL = FileLocator.resolve(fileURL);
			} catch (Exception e) {
				ExceptionHandler.logThrowableError(e, null);
				return new HashMap<String,IResource>();				
			}
			if (fileResolveURL != null) {
				fileLibJars = (new File(fileResolveURL.getFile())).list();
			}
		}
		
		//Select only exist in project
		if (fileLibJars.length > 0) {
			Arrays.sort(fileLibJars);
			IClasspathEntry classpathEntry[];
			try {
				//TODO (tau->tau) 14.04.2006 ? javaProject.getResolvedClasspath(true); 
				classpathEntry = javaProject.getRawClasspath();
			} catch (JavaModelException e) {
				ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null,null);
				return new HashMap<String,IResource>();
			}
			Set parentHibernateFilesInClasspath = getParentAddHibernateFilesInClasspath( fileLibJars, classpathEntry, javaProject);
			
			if (parentHibernateFilesInClasspath.size() == 0){
				try {
					classpathEntry = javaProject.getResolvedClasspath(true);
					parentHibernateFilesInClasspath = getParentAddHibernateFilesInClasspath( fileLibJars, classpathEntry, javaProject);
				} catch (JavaModelException e) {
					ExceptionHandler.logThrowableError(e, null);
					return new HashMap<String,IResource>();
				}
				flagResolvedClasspath = true;				
			}
			
			Map<String,IResource> filesForSelect = new HashMap<String,IResource>();			
			for (Iterator iter = parentHibernateFilesInClasspath.iterator(); iter.hasNext();) {
				File element = (File) iter.next();
//				Path ppp = new Path(element.getPath());
//				IFolder folder = project.getParent().getFolder((new Path(element.getPath())).removeFirstSegments(1));
				IFolder folder = project.getParent().getFolder((new Path(element.getPath())));				
				
				if (folder.exists()){
					for (int i = 0; i < fileLibJars.length; i++) {
						String nameFile = fileLibJars[i];
						IResource fileInFolder = folder.findMember(nameFile);
						if (fileInFolder != null && fileInFolder.exists()){
							filesForSelect.put(nameFile, fileInFolder);
						}					
					}					
				}
			}
			return filesForSelect;
		} else {
			return new HashMap<String,IResource>();
		}

	}
	
	// add tau 11.10.2005
	private HashSet getParentAddHibernateFilesInClasspath(String[] fileLibJars,IClasspathEntry[] classpathEntry,IJavaProject javaProject) {
		HashSet<File> filesInClasspath = new HashSet<File>();
		for (int i = 0; i < classpathEntry.length; i++) {
			if (classpathEntry[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY	&&
				classpathEntry[i].getContentKind() == IPackageFragmentRoot.K_BINARY) {
				
				IPath libPath = classpathEntry[i].getPath();
				File fileLib = javaProject.getProject().getFile(libPath.removeFirstSegments(1)).getLocation().toFile();
				String[] fileLibStrings = fileLib.list();
				if (fileLibStrings != null) {
					for (int j = 0; j < fileLibStrings.length; j++) {
						String fileLibString = fileLibStrings[j];
						String filePath = fileLib.toString() + File.separator+ fileLibString;
						if (Arrays.binarySearch(fileLibJars, fileLibString) >= 0) {
							File file = new File(filePath);
							filesInClasspath.add(file.getParentFile());
						}
					}
				}
			}

			if (Arrays.binarySearch(fileLibJars, classpathEntry[i].getPath().toFile().getName()) >= 0) {
//				File file = new File(project.getLocation().toString().substring(0,project.getLocation().toString().lastIndexOf(project.getName()) - 1)
//						+ classpathEntry[i].getPath());
				File file = new File(classpathEntry[i].getPath().toString());
				filesInClasspath.add(file.getParentFile());
			}
		}
		
		return filesInClasspath;
	}
	
//	// add tau 11.10.2005
//	private HashSet filesInClasspath(String[] fileLibJars, IClasspathEntry[] classpathEntry, Vector newClasspath, IJavaProject javaProject){	
//		HashSet hs = new HashSet();
//		for (int i = 0; i < classpathEntry.length; i++) {
//			
//			// add tau 11.10.2005
//			if (classpathEntry[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY &&
//				classpathEntry[i].getContentKind() == IPackageFragmentRoot.K_BINARY	)  {
//				
//				IPath libPath = classpathEntry[i].getPath();
//				File fileLib = javaProject.getProject().getFile(libPath.removeFirstSegments(1)).getLocation().toFile();				
//				String[] fileLibStrings = fileLib.list();
//				if (fileLibStrings != null){
//					for (int j = 0; j < fileLibStrings.length; j++) {
//						String fileLibString = fileLibStrings[j];
//						String filePath = fileLib.toString()+File.separator+fileLibString;
//						
//						if (Arrays.binarySearch(fileLibJars, fileLibString) >= 0) {
//							File file = new File(filePath);
//							hs.add(file.getParentFile());
//							file.delete();
//						}
//						
//					}
//				}
//			}
//			
//			if (Arrays.binarySearch(fileLibJars, classpathEntry[i].getPath().toFile().getName()) >= 0) {
//				File file = new File(project.getLocation().toString().substring(0,project.getLocation().toString().lastIndexOf(project.getName()) - 1)
//						+ classpathEntry[i].getPath());
//				hs.add(file.getParentFile());
//				file.delete();
//			} else {
//				// #added# by Konstantin Mishin on 27.08.2005 fixed for
//				// ORMIISTUD-644				
//				newClasspath.add(classpathEntry[i]);
//			}
//		}
//		return hs;
//	}	
	
	// add tau 11.10.2005
	private HashSet<File> getAddHibernateFilesInClasspath(String[] fileLibJars,IClasspathEntry[] classpathEntry,IJavaProject javaProject) {
		HashSet<File> returnfiles = new HashSet<File>();
		for (int i = 0; i < classpathEntry.length; i++) {
			if (classpathEntry[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY	&& classpathEntry[i].getContentKind() == IPackageFragmentRoot.K_BINARY) {
				IPath libPath = classpathEntry[i].getPath();
				File fileLib = javaProject.getProject().getFile(libPath.removeFirstSegments(1)).getLocation().toFile();
				String[] fileLibStrings = fileLib.list();
				if (fileLibStrings != null) {
					for (int j = 0; j < fileLibStrings.length; j++) {
						String fileLibString = fileLibStrings[j];
						String filePath = fileLib.toString() + File.separator+ fileLibString;

						if (Arrays.binarySearch(fileLibJars, fileLibString) >= 0) {
							File file = new File(filePath);
							returnfiles.add(file);
						}

					}
				}
			}

			if (Arrays.binarySearch(fileLibJars, classpathEntry[i].getPath().toFile().getName()) >= 0) {
				File file = new File(project.getLocation().toString().substring(0,project.getLocation().toString().lastIndexOf(project.getName()) - 1)
						+ classpathEntry[i].getPath());
				returnfiles.add(file);
			}
		}
		return returnfiles;
	}	

//	// add tau 11.10.2005
//	private HashSet getFilesInClasspath(String[] fileLibJars,IClasspathEntry[] classpathEntry,IJavaProject javaProject) {
//		HashSet filesInClasspath = new HashSet();
//		for (int i = 0; i < classpathEntry.length; i++) {
//			if (classpathEntry[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY	&& classpathEntry[i].getContentKind() == IPackageFragmentRoot.K_BINARY) {
//
//				IPath libPath = classpathEntry[i].getPath();
//				File fileLib = javaProject.getProject().getFile(
//						libPath.removeFirstSegments(1)).getLocation().toFile();
//				String[] fileLibStrings = fileLib.list();
//				if (fileLibStrings != null) {
//					for (int j = 0; j < fileLibStrings.length; j++) {
//						String fileLibString = fileLibStrings[j];
//						String filePath = fileLib.toString() + File.separator
//								+ fileLibString;
//
//						if (Arrays.binarySearch(fileLibJars, fileLibString) >= 0) {
//							File file = new File(filePath);
//							filesInClasspath.add(file.getParentFile());
//							file.delete();
//						}
//
//					}
//				}
//			}
//
//			if (Arrays.binarySearch(fileLibJars, classpathEntry[i].getPath()
//					.toFile().getName()) >= 0) {
//				File file = new File(project.getLocation().toString()
//						.substring(
//								0,
//								project.getLocation().toString().lastIndexOf(
//										project.getName()) - 1)
//						+ classpathEntry[i].getPath());
//				filesInClasspath.add(file.getParentFile());
//				file.delete();
//			} else {
//				// #added# by Konstantin Mishin on 27.08.2005 fixed for
//				// ORMIISTUD-644
//				newClasspath.add(classpathEntry[i]);
//			}
//		}
//		return filesInClasspath;
//	}
	
	
	// #added#
	// #added# by Konstantin Mishin on 27.08.2005 fixed for ORMIISTUD-644
	private void removeComfAndMappFiles(){
		IOrmProject ormProject = null;
		try {
			ormProject = OrmCore.getDefault().create(project);
		} catch (Exception e) {
			ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, null);			
		}
		if (ormProject != null) {
			final IMapping mappings[] = ormProject.getMappings();				
			IWorkspaceRunnable runnable = new IWorkspaceRunnable(){
				public void run(IProgressMonitor monitor) throws CoreException {
					IRunnableWithProgress operation = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							for (int j = 0; j < mappings.length; j++) {
								IMapping mapping = mappings[j];
								IMappingStorage[] storages=mapping.getMappingStorages();
								monitor.beginTask(
										MessageFormat.format(BUNDLE.getString("RemoveOrmNatureAction.ProgressMonitor"), new Object[]{mapping.getName()}),storages.length);
								OrmProgressMonitor.setMonitor(monitor);
								try {
									
									IMappingConfiguration conf=mapping.getConfiguration();
									IResource res=null;																	
									for(int i=0; i<storages.length; i++) {
										
										monitor.subTask(MessageFormat.format(BUNDLE.getString("RemoveOrmNatureAction.ProgressMonitor"), new Object[]{storages[i].getName()}));
										
										res=storages[i].getResource();
										if (res!=null) res.delete(true,monitor);
										monitor.worked(i+1);
										
									}
									res=conf.getResource();
									monitor.subTask(MessageFormat.format(BUNDLE.getString("RemoveOrmNatureAction.ProgressMonitor"), new Object[]{res.getName()}));
									
									res.delete(true,monitor);								
								} catch(CoreException ex) {	
									ExceptionHandler.handle(ex,ViewPlugin.getActiveWorkbenchShell(),null, "Error in remove hibernate configuration action.");
								} finally {
									monitor.done();
									OrmProgressMonitor.setMonitor(null);
								}
							}
						}
					};				
					ProgressMonitorDialog progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
					try {
						progress.run(false, true, operation);
					} catch (InvocationTargetException e) {
						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in remove hibernate configuration action.");
					} catch (InterruptedException e1) {
						ExceptionHandler.handle(e1,ViewPlugin.getActiveWorkbenchShell(),null, null);						
					}
				}
			};
			try {
				// edit tau 16.03.2006 -> add ISchedulingRule for IOrmProject
				//project.getProject().getWorkspace().run(runnable,new NullProgressMonitor());				
				project.getWorkspace().run(runnable, RuleUtils.getOrmProjectRule(OrmCore.getDefault().create(project)), IResource.NONE, new NullProgressMonitor());
				
			} catch (CoreException e) {
				ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in remove hibernate configuration action.");
			}
		}
	}
	// #added#
	
	// add tau 07.04.2006	
	private String[] getPersistentPropertyHibernateFiles() {
		String [] returnArray = {};
		QualifiedName addQualifiedName = new QualifiedName(ViewPlugin.PLUGIN_ID,"addhibernatejars");		
		try {
			String persistentProperty = project.getPersistentProperty(addQualifiedName);
			if (persistentProperty != null) {
				returnArray = persistentProperty.split(";");				
			}
		} catch (CoreException e) {
			ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, null);
		}
		return returnArray;		

	}	
	
	// add tau 05.04.2006
	private boolean selectRemoveFiles(String[] hibernateFiles, String[] mappingFiles) {
		
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW) ExceptionHandler.logInfo("RemoveOrmNatureAction.selectRemoveFiles(), hibernateFiles= " + hibernateFiles.length);		
		
		TreeModel deletelFiles = new TreeModel("root",null);
		TreeModel classnode = null;
		
		//For mappingFiles
		String selectMappings = BUNDLE.getString("RemoveOrmNatureAction.RemoveMapping");
	 	deletelFiles.addNode(selectMappings);
		classnode = (TreeModel) deletelFiles.getNode(selectMappings);		
		
		
		//For hibernateFiles
		String selectHibernateJars = null;
		if (hibernateFiles.length > 0) {
			selectHibernateJars = BUNDLE.getString("RemoveOrmNatureAction.RemoveHibernateJars");
		 	deletelFiles.addNode(selectHibernateJars);
			classnode = (TreeModel) deletelFiles.getNode(selectHibernateJars);
			for (int i = 0; i < hibernateFiles.length; i++) {
				//test foe exit in project?
				classnode.addNode(hibernateFiles[i]);			
			}			
		}
//		else {
//			selectHibernateJars = BUNDLE.getString("AddOrmNatureAction.NoRemoveHibernateJars");
//		}

		ModelCheckedTreeSelectionDialog dlg = new ModelCheckedTreeSelectionDialog(
				ViewPlugin.getActiveWorkbenchShell(), new HibernateJarsLabelProvider(),
				new TreeModelContentProvider(), true) {
			public void create() {
				super.create();
				getButton(IDialogConstants.OK_ID).setText(BUNDLE.getString("AddOrmNatureAction.SelectHibernateJars.Yes"));				
				getButton(IDialogConstants.CANCEL_ID).setText(BUNDLE.getString("AddOrmNatureAction.SelectHibernateJars.No"));				
			}

		};		
		
		dlg.setTitle(BUNDLE.getString("RemoveOrmNatureAction.DialogTitle"));
		dlg.setMessage(MessageFormat.format(BUNDLE.getString("RemoveOrmNatureAction.DialogQuestion"),new Object[]{project.getName()}));		
		dlg.setInput(deletelFiles);
		dlg.open();

		if (dlg.getReturnCode() == 0) {
			Object[] selectedFiles = dlg.getResult();
			
			if (selectedFiles.length == 0) {
				removeJars = false;
				removeMappings = false;
				return true;
			}
			
			filesForRemoveHibernateSet = new HashSet<String>(selectedFiles.length);			
			for (int z = 0; z < selectedFiles.length; z++) {
				if (selectedFiles[z] instanceof TreeModel) {
					TreeModel elem = (TreeModel) selectedFiles[z];
					String elemName = elem.getName();
					if (elemName.equals(selectHibernateJars)){
						removeJars = true;
						continue;
					} else if (elem.getChildren().length==0 && elem.getParent().getName().equals(selectHibernateJars)) {
						filesForRemoveHibernateSet.add(elemName);
					} else if (elemName.equals(selectMappings)){
						removeMappings = true;
						continue;
					}
				}
			}
			return true;
		} else {
			return false;
		}
		
//		if (dlg.getReturnCode() == 0) {
//			Object[] selectedFiles = dlg.getResult();
//			for (int z = 0; z < selectedFiles.length; z++)
//				if (selectedFiles[z] instanceof TreeModel)
//				{
//					TreeModel elem = (TreeModel) selectedFiles[z];
//					if (elem.getChildren().length==0) {
//						String nameElem = elem.getName();
//						for (int i = 0; i < hibernateJars.length; i++) {
//							File fileHibernate = (File)hibernateJars[i];	
//							if (fileHibernate.isFile()){
//								if (fileHibernate.getName().equals(nameElem)){
//									addHibernateJarsSet.add(hibernateJars[i]);
//									break;
//								}
//							}
//						}
//					}
//				}
//			return true;			
//		} else {
//			addHibernateJarsSet.clear();			
//			return false;
//		}
		
	}
	
}