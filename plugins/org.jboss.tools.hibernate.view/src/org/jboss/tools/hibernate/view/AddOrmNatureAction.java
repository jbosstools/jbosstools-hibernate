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
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.dialog.ModelCheckedTreeSelectionDialog;
import org.jboss.tools.hibernate.dialog.SelectAdditionPersClasses;
import org.jboss.tools.hibernate.dialog.StatisticDialog;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ReversStatistic;
import org.jboss.tools.hibernate.view.views.ExplorerBase;
import org.jboss.tools.hibernate.view.views.ExplorerClass;
import org.jboss.tools.hibernate.wizard.hibernateconnection.HibernateConnectionWizard;
import org.jboss.tools.hibernate.wizard.persistentclasses.PersistentClassesWizard;
import org.jboss.tools.hibernate.wizard.tablesclasses.TablesClassesWizard;
import org.jboss.tools.hibernate.wizard.treemodel.HibernateJarsLabelProvider;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModel;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModelContentProvider;
import org.osgi.framework.Bundle;

/**
 * @author Tau from Minsk
 * Created on 20.04.2006  
 */
public class AddOrmNatureAction {

	private Shell fCurrentShell;
	private IMapping ormMapping;
	private ResourceBundle BUNDLE = ResourceBundle.getBundle(AddOrmNatureAction.class.getPackage().getName() + ".orm");
	private Set<File> addHibernateJarsSet = new HashSet<File>();
	private IProject project;
	
	//add tau 25.04.2006
	private IFolder folderLib;
	private boolean flagLib = false;		

	public AddOrmNatureAction(IProject project, Shell fCurrentShell) {
		this.project = project;
		this.fCurrentShell = fCurrentShell;
	}


	public void runAction() {
		if  (project == null) return;
		
		//add tau 04.04.2006
		if (!selectAddHibernateFiles()) return;		
		
		// edit tau 21.11.2005
		try {
			
			// edit tau 27.01.2006
			OrmCore.getDefault().removeListener();
			OrmCore.getDefault().setLockResourceChangeListener(true);
			
			//edit tau 17.03.2006 add getWorkspace().run			
			class NatureAddWorkspaceRunnable implements IWorkspaceRunnable {
				boolean natureAdded = false;
				public void run(IProgressMonitor monitor) throws CoreException {
					natureAdded = addNatureToProject(project, OrmCore.ORM2NATURE_ID, !addHibernateJarsSet.isEmpty());					
				}
				public boolean getnatureAdded() {
					return natureAdded;
				}
			};
			
			NatureAddWorkspaceRunnable workspaceRunnable = new NatureAddWorkspaceRunnable();
			
			project.getWorkspace().run( workspaceRunnable, null, IWorkspace.AVOID_UPDATE, new NullProgressMonitor());			

			// add 04.07.2005 tau			
			if (workspaceRunnable.getnatureAdded()) {
				setClasspath(folderLib, flagLib);				

				IWorkbench workbench = PlatformUI.getWorkbench();
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				
				// add 04.08.2005
				ExplorerBase explorerBaseView = (ExplorerBase) window
						.getActivePage()
						.showView(ExplorerBase.getExplorerId());
				
				ExplorerClass explorerClassView = (ExplorerClass) window
						.getActivePage()
						.showView(ExplorerClass.getExplorerId());

				explorerClassView.setFocus(OrmCore.getDefault().create(
						project), 1);
				Dialog dialog = new SelectAdditionPersClasses(fCurrentShell);
				int rezult = dialog.open();
				if (rezult == SelectAdditionPersClasses.PERSISTENT_CLASSES_WIZARD){
					new WizardDialog(fCurrentShell,	new PersistentClassesWizard(ormMapping, null)).open(); // edit tau 13.02.2006
				} else if (rezult == SelectAdditionPersClasses.TABLES_CLASSES_WIZARD){
					//akuzmin 06.09.2005
					ReversStatistic results=new ReversStatistic();
					
					if(new WizardDialog(fCurrentShell,	new TablesClassesWizard(ormMapping, results, null)).open()==WizardDialog.OK); {
					String[] statisticlabel={BUNDLE.getString("Explorer.TablesClassesWizardInfoCreate"),
											BUNDLE.getString("Explorer.TablesClassesWizardInfonoPK"),
											BUNDLE.getString("Explorer.TablesClassesWizardInfoLink")};
					String[] statisticstatus={"INFO","WARNING","WARNING"};
					new StatisticDialog(fCurrentShell,
										results.getAllResults(),
										statisticlabel,statisticstatus,BUNDLE.getString("Explorer.TablesClassesWizardInfoTitle")).open();
					}

				}
				if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW) ExceptionHandler.logInfo("Ok addNatureToProject for " + project.getName());

				//add tau 04.08.2005
				if (ormMapping != null &&
						ormMapping.getConfiguration() != null &&
						ormMapping.getConfiguration().getResource() instanceof IFile){
					IWorkbenchPage page = ViewPlugin.getPage();
					IDE.openEditor(page,(IFile) ormMapping.getConfiguration().getResource());
					//added gavrs 9/20/2005 edit tau 09.12.2005
					if(!chekJRESystemLibraryEntry()) {
						MessageDialog.openWarning(ViewPlugin.getActiveWorkbenchShell(),
								BUNDLE.getString("AddOrmNatureAction.TitleDialog"),
								BUNDLE.getString("AddOrmNatureAction.AddJRE"));					
						//added gavrs 9/20/2005
					}
				}
			} else {
				if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)ExceptionHandler.logInfo("Cancel addNatureToProject for " + project.getName());
				OrmCore.getDefault().getOrmModel().removeOrmProject(project);
			}  
		} catch (CoreException e) {
			ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, "Error in Add Hibernate capabilities!");
		} finally {
			// edit tau 27.01.2006				
			OrmCore.getDefault().updateListener();
			OrmCore.getDefault().setLockResourceChangeListener(false);			
		}

	}

	private boolean addNatureToProject(final IProject project, final String natureId, final boolean addFiles) throws CoreException {
		if (project == null) return false; 
		if (project.hasNature(natureId)) return true;
		
		IRunnableWithProgress operation = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask(BUNDLE.getString("AddOrmNatureAction.ProgressMonitor"),100);
				OrmProgressMonitor.setMonitor(monitor);				
				try {
					IOrmProject ormProject;
					try {
						ormProject = OrmCore.getDefault().create(project);
						if (ormProject != null){					
							IMapping ormMapping=ormProject.getInitialMapping(natureId);
							setOrmMapping(ormMapping);
							
							IOrmConfiguration ormConfiguration = ormProject.getOrmConfiguration();
							if (ormConfiguration.getResource() == null) {
								Properties properties = ormConfiguration.getProperties();
								ViewPlugin.loadPreferenceStoreProperties(properties, ViewPlugin.autoMappingSettingPrefPage);
								if (!properties.isEmpty()) {
										ormConfiguration.save();
								}
							}
						}						
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} catch (IOException e) {
						throw new InvocationTargetException(e);						
					}
				} finally {
					monitor.done();						
					OrmProgressMonitor.setMonitor(null);
				}

			}
			
		};

		ProgressMonitorDialog progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
		try {
			progress.run(false, true, operation);
		} catch (InvocationTargetException e1) {
			ExceptionHandler.handle(e1,ViewPlugin.getActiveWorkbenchShell(),null, "PersistentClasses was not created!");
		} catch (InterruptedException e1) {
        	//TODO (tau-tau) for Exception			
			// Cancelled.
		}
	
		if ( ormMapping == null) return false;
		Wizard wizard = new HibernateConnectionWizard(ormMapping, null); // edit tau 13.02.2006
		Dialog dialog = new WizardDialog(fCurrentShell,wizard);
		if (dialog.open() == WizardDialog.CANCEL) return false;

		// else -> add Nature To Project
		// add tau 30.09.2005
		
		IRunnableWithProgress operationAddNature = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask(BUNDLE.getString("AddOrmNatureAction.ProgressMonitor"), 100);
				OrmProgressMonitor.setMonitor(monitor);
				
				monitor.worked(1);
				
				try {
					//TODO (tau -> tau) RULE - GO UP for project.setDescription(description, null); !!!
					IProjectDescription description;
					try {
						description = project.getDescription();
						String[] prevNatures = description.getNatureIds();
						String[] newNatures = new String[prevNatures.length + 1];
						System.arraycopy(prevNatures, 0, newNatures, 0,	prevNatures.length);
						monitor.worked(1);
						newNatures[prevNatures.length] = natureId;
						description.setNatureIds(newNatures);
						project.setDescription(description, null);
						monitor.worked(1);
						// Setting the Java Classpath for javaProject
						// tau 24.02.2005
						// #added# by Konstantin Mishin on 30.08.2005 fixed for
						// ORMIISTUD-661
						IJavaProject javaProject = JavaCore.create(project);						
						if (addFiles) {
								addFileLibJar(javaProject);
						}
						monitor.worked(1);						
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} catch (IOException e) {
						throw new InvocationTargetException(e);						
					}

					if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo("addNatureToProject: for "	+ project.getName());

				} finally {
					monitor.done();
					OrmProgressMonitor.setMonitor(null);
				}
			}
		};
		
		progress = new ProgressMonitorDialog(ViewPlugin.getActiveWorkbenchShell());
		try {
			progress.run(false, true, operationAddNature);
		} catch (InvocationTargetException e1) {
			ExceptionHandler.handle(e1,ViewPlugin.getActiveWorkbenchShell(),null, "Error in order to add Orm Nature");
		} catch (InterruptedException e1) {
        	//TODO (tau-tau) for Exception			
			// Cancelled.
		}
				
		return true;

	}
	
	/**
	 * add fileLibJars into /lib javaProject
	 * add fileLibJars into Classpath javaProject
	 * 
	 * tau 19.05.2005
	 *  
	 * @param javaProject
	 * @param fileLibJars
	 * @throws CoreException 
	 * @throws IOException 
	 */
	private void addFileLibJar(IJavaProject javaProject) throws CoreException, IOException {
		
		 // add 08.05.2005 tau
		IPath folderLibPath = null;
		//IFolder folderLib = null;		
		IFolder webInfLib = javaProject.getProject().getFolder(new String("/web-inf/lib"));
		IFolder lib = javaProject.getProject().getFolder(new String("/lib"));		
		String webInfLibStringLower = webInfLib.getProjectRelativePath().toString().toLowerCase();
		String libStringLower = lib.getProjectRelativePath().toString().toLowerCase();
		
		IProgressMonitor monitor = OrmProgressMonitor.getMonitor();
		monitor.worked(1);
		
		// add 04.08.2005
		IFolder[] libs = {
				javaProject.getProject().getFolder(new String("/web-inf/lib")),
				javaProject.getProject().getFolder(new String("/WEB-INF/lib")),
				javaProject.getProject().getFolder(new String("/WEB-INF/LIB")),
				javaProject.getProject().getFolder(new String("/web-inf/LIB")),
				javaProject.getProject().getFolder(new String("/lib")),
				javaProject.getProject().getFolder(new String("/LIB"))};
		
		// edit 12.07.2005 tau -> for deploy hibernate to server 
		IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);		
		for (int i = 0; i < classpathEntries.length; i++) {
			IClasspathEntry classpathEntry = classpathEntries[i];
			if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY){
				IPath classpathEntryPath = classpathEntry.getPath();
				IPath classpathInProject = classpathEntryPath.removeFirstSegments(1);
				if (javaProject.getProject().exists(classpathInProject)) {
					String classpathEntryPathStringLover = classpathEntryPath.toString().toLowerCase();
					if (classpathEntryPathStringLover.indexOf(webInfLibStringLower) > 0){
						folderLibPath = classpathEntryPath;
						
						// add tau 05.05.2006 for ESORM-600
						if (classpathEntry.getEntryKind() != IClasspathEntry.CPE_CONTAINER){
							flagLib = true;								
						}						
						
						if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo("folderLibPath= " + folderLibPath);						
						break;
					}					
				}
			}
		}
		
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo("folderLibPath= " + folderLibPath + ",flagLib= " + flagLib);		
		
		if (folderLibPath == null){
			for (int i = 0; i < classpathEntries.length; i++) {
				IClasspathEntry classpathEntry = classpathEntries[i];
				if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY){
					IPath classpathEntryPath = classpathEntry.getPath();
					IPath classpathInProject = classpathEntryPath.removeFirstSegments(1);
					if (javaProject.getProject().exists(classpathInProject)) {
						String classpathEntryPathStringLover = classpathEntryPath.toString().toLowerCase();
						if (classpathEntryPathStringLover.indexOf(libStringLower) > 0){
							folderLibPath = classpathEntryPath;
							
							// add tau 05.05.2006 for ESORM-600
							if (classpathEntry.getEntryKind() != IClasspathEntry.CPE_CONTAINER){
								flagLib = true;								
							}
							
							break;
						}					
					}
				}
			}
		}
		
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo("2 folderLibPath= " + folderLibPath + ",flagLib= " + flagLib);		
		
		if (folderLibPath != null) {
			if (folderLibPath.segmentCount() == 1){
				folderLib = javaProject.getProject().getFolder(folderLibPath);				
			} else if ((folderLibPath.segmentCount() == 2)){
				folderLib = javaProject.getProject().getFolder(folderLibPath.removeFirstSegments(1));				
			} else {
				folderLib = javaProject.getProject().getFolder(folderLibPath.removeFirstSegments(1).removeLastSegments(1));				
			}
			
			folderLibPath = folderLib.getLocation();
		}
		
		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo("3 folderLibPath= " + folderLibPath + ",flagLib= " + flagLib);
		
		if (folderLibPath == null) {
			for (int i = 0; i < libs.length; i++) {
				if (libs[i].isAccessible()){
					folderLibPath = libs[i].getLocation();
					folderLib = libs[i];
					flagLib = true;
					break;
				}
			}
		}

		if (folderLibPath == null){
			lib.create(false, true, null);
			folderLibPath = lib.getLocation();
			folderLib = lib;
			flagLib = true;			
		}
		
		File fileSource = getFileFromBundle("org.jboss.tools.hibernate.hblibs", new Path("/lib") );		
	
		File fileDest = folderLibPath.toFile();
		
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) return true;
				for (Iterator iter = addHibernateJarsSet.iterator(); iter.hasNext();) {
					File fileSource = (File) iter.next();
					if (pathname.getName().equals(fileSource.getName()) ) return true; 
				}				
				return false;
			}
		};

		FileUtil.copyDir(fileSource, fileDest, false, false, false, fileFilter);

		//add tau 05.04.2006 for /ESORM-554 Show a list of Jar files when adding Hibernate capability
		char c = ';';
		StringBuffer bufer = new StringBuffer(addHibernateJarsSet.size());
		for (Iterator iter = addHibernateJarsSet.iterator(); iter.hasNext();) {
			File element = (File) iter.next();
			bufer.append(element.getName());
			bufer.append(c);
		}
		
		QualifiedName addQualifiedName = new QualifiedName(ViewPlugin.PLUGIN_ID,"addhibernatejars");
		javaProject.getResource().setPersistentProperty(addQualifiedName, null);		
		javaProject.getResource().setPersistentProperty(addQualifiedName, bufer.toString());
		
		monitor.worked(1);
		if (folderLib != null){
			if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW)	ExceptionHandler.logInfo("ref->" + folderLib);			
			folderLib.refreshLocal(IResource.DEPTH_ONE,new NullProgressMonitor());			
		}
		monitor.worked(1);		
	}

	private File getFileFromBundle(String stringID, Path path) throws IOException {
		File fileReturn = null;		
	    Bundle bundle = Platform.getBundle(stringID);
	    URL fileURL = FileLocator.find(bundle, path, null);
		URL fileResolveURL = FileLocator.resolve(fileURL);
		if (fileResolveURL != null) {
			fileReturn = new File(fileResolveURL.getFile());
		}
		return fileReturn;
	}

	protected void setOrmMapping(IMapping ormMapping) {
		this.ormMapping = ormMapping;
		
	}

	private boolean chekJRESystemLibraryEntry() {
		boolean result = false;
		IJavaProject fCurrJProject = JavaCore.create(ormMapping.getProject()
				.getProject());
		IClasspathEntry[] oldClasspath = fCurrJProject.readRawClasspath();
		for (int i = 0; i < oldClasspath.length; i++) {
			if (oldClasspath[i].getPath().toString().indexOf(
					"org.eclipse.jdt.launching.JRE_CONTAINER") > -1) {// added
																		// 10/10/2005
																		// gavrs
				result = true;
				break;
			}
		}
		return result;
	}

	// add tau 04.04.2006
	private boolean selectAddHibernateFiles() {
		TreeModel allFiles = new TreeModel("root",null);
		File[] hibernateJars = {};
		
		File fileSource = null;
		try {
			fileSource = getFileFromBundle("org.jboss.tools.hibernate.hblibs", new Path("/lib") );
		} catch (IOException e) {
			ExceptionHandler.handle(e, ViewPlugin.getActiveWorkbenchShell(), null, null);
			return false;
		}		
		
		if (fileSource == null) return false;		
		
		hibernateJars = fileSource.listFiles();

		String selectHibernateJars = BUNDLE.getString("AddOrmNatureAction.SelectHibernateJars");
	 	allFiles.addNode(selectHibernateJars);
		TreeModel classnode = (TreeModel) allFiles.getNode(selectHibernateJars);	 	

		for (int i = 0; i < hibernateJars.length; i++) {
			File file = hibernateJars[i];
			classnode.addNode(file.getName());			
		}

		ModelCheckedTreeSelectionDialog dlg = new ModelCheckedTreeSelectionDialog(
				ViewPlugin.getActiveWorkbenchShell(), new HibernateJarsLabelProvider(),
				new TreeModelContentProvider(), true) {
			public void create() {
				super.create();
				getTreeViewer().expandAll();				
				setAllSelections();
				getButton(IDialogConstants.OK_ID).setText(BUNDLE.getString("AddOrmNatureAction.SelectHibernateJars.Yes"));				
				getButton(IDialogConstants.CANCEL_ID).setText(BUNDLE.getString("AddOrmNatureAction.SelectHibernateJars.No"));				
			}

		};		
		
		dlg.setTitle(BUNDLE.getString("RemoveConfirmRadioDialog.Title"));
		dlg.setMessage(MessageFormat.format(BUNDLE.getString("RemoveConfirmRadioDialog.Question"),
				new Object[]{project.getName()}));		
		dlg.setInput(allFiles);
		dlg.open();
		
		if (dlg.getReturnCode() == 0) {
			Object[] selectedFiles = dlg.getResult();
			for (int z = 0; z < selectedFiles.length; z++)
				if (selectedFiles[z] instanceof TreeModel)
				{
					TreeModel elem = (TreeModel) selectedFiles[z];
					if (elem.getChildren().length==0) {
						String nameElem = elem.getName();
						for (int i = 0; i < hibernateJars.length; i++) {
							File fileHibernate = (File)hibernateJars[i];	
							if (fileHibernate.isFile()){
								if (fileHibernate.getName().equals(nameElem)){
									addHibernateJarsSet.add(hibernateJars[i]);
									break;
								}
							}
						}
					}
				}
			return true;			
		} else {
			addHibernateJarsSet.clear();			
			return false;			
		}

	}	
	
	//
	// add tau 04.04.2006
	private void setClasspath(IFolder folderLib, boolean flagLib) throws CoreException {

		if (folderLib == null) return;
		
		IResource[] members = folderLib.members();
		int plus = members.length;
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);

		for (int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			if (resource.exists() && resource.getFileExtension() != null
					&& resource.getFileExtension().equalsIgnoreCase("jar")) {
				if (flagLib) {
					for (int j = 0; j < classpathEntries.length; j++) {
						IClasspathEntry classpathEntry = classpathEntries[j];
						if (classpathEntry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
							IPath classpathEntryPath = classpathEntry.getPath();
							if (classpathEntryPath.equals(resource
									.getFullPath())) {
								members[i] = null;
								plus--;
								break;
							}
						}
					}
				} else if (javaProject.isOnClasspath(resource)) {
					members[i] = null;
					plus--;
				}
			} else {
				members[i] = null;
				plus--;
			}
		}

		IClasspathEntry[] oldClasspath = javaProject.readRawClasspath();
		IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length + plus];
		System.arraycopy(oldClasspath, 0, newClasspath, 0, oldClasspath.length);

		int k = 0;
		for (int i = 0; i < members.length; i++) {
			IResource resource = members[i];
			if (resource != null) {
				IClasspathEntry classpathEntry = JavaCore.newLibraryEntry(
						resource.getFullPath(), null, // no source
						null, // no source
						false); // not exported
				newClasspath[oldClasspath.length + k] = classpathEntry;
				k++;
			}
		}

		javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());

	}	

}
