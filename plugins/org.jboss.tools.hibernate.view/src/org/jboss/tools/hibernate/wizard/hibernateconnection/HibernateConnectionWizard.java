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
package org.jboss.tools.hibernate.wizard.hibernateconnection;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;


/**
 * @author sushko
 * Hibernate Connection Wizard main class
 */

public class HibernateConnectionWizard extends ReadOnlyWizard implements IWizard {

	public static final String BUNDLE_NAME ="hibernateconnection";
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConnectionWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;	
	private HibernateConnectionWizardPage0 page0; 
	private HibernateConnectionWizardPage1 page1; 
	private HibernateConnectionWizardPage2 page2; 
	private HibernateConnectionWizardPage3 page3; 
	private HibernateConnectionWizardPage4 page4; 
	private HibernateConnectionWizardPage5 page5; 
	private IWizardPage currentpage;
	private IMapping mapping;
	private boolean  isExistCfgFile=false;
	private IJavaProject fCurrJProject;
	private IFile file;
	
	/**
	 * constructor of the wizard
	 */
	public HibernateConnectionWizard (IMapping mapping, TreeViewer viewer) {
		super(mapping, viewer);
		setNeedsProgressMonitor(true);
		this.mapping = mapping;
		
		//TODO EXP 5d 27.02.2006
		((HibernateConfiguration)mapping.getConfiguration()).verifyHibernateMapping();
		((HibernateConfiguration)mapping.getConfiguration()).setDirty(false);
		boolean f = ((HibernateConfiguration)mapping.getConfiguration()).isDirty();		
		
		this.setWindowTitle(BUNDLE.getString("HibernateConnectionWizard.Title"));
		if(mapping.getConfiguration().getResource()!=null)
			isExistCfgFile=mapping.getConfiguration().getResource().exists();
	}
	
	
	/**
	 * performFinish() of the wizard. Reaction on clicking finish button
	 */
	public HibernateConnectionWizardPage2 getPage2(){
		return page2;
	}
	public boolean performFinish()  {
		String  filename=null;
    	//TODO (tau-tau) for Exception		
		try 
		{	
			if(currentpage==page1){
				page5.setNewProperty();
				page4.setProvider();
			}
			if ( currentpage!=page0)
			{
				if (page2.getConnection()!=null){
				try {
					page2.getConnection().close();
				} catch (SQLException e1) {
	            	//TODO (tau-tau) for Exception					
					ExceptionHandler.logThrowableError(e1, e1.getMessage());
				}
				}
				page2.saveConfiguration();
			} 
			if (currentpage==page0){
				if(((HibernateConfiguration)mapping.getConfiguration()).getProperty("hibernate.connection.url")==""){
				page2.setDialect();
				page2.saveConfiguration();
				}
			}

		    	
			if(page4.isPageComplete() ){
				page4.saveConfiguration();
				//mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage4.property_hibernate_connection_provider_class"));
				//mapping.save();
			}
				
			if(mapping.getConfiguration().getProperty("hibernate.dialect")!="" || page2.isPageComplete() )
				//mapping.getConfiguration().save(); del tau 14.02.2006
				mapping.getConfiguration().save(false); // add tau 14.02.2006				
						
			IPath ipath=page0.getPath();
			fCurrJProject =  JavaCore.create(mapping.getProject().getProject());
			if (ipath!=null){
				if(!createNewFolders())
					return false;

					// TODO (tau->tau 14.02.2006 - true or false for flagSaveMappingStorages in write(boolean flagSaveMappingStorages)
					 mapping.getProject().moveMapping(mapping, ipath);
					 filename =ipath.removeFirstSegments(1).toString();
					 file = mapping.getProject().getProject().getFile(filename);
					 if(file != null){
					 
						 IWorkbenchPage page = ViewPlugin.getPage();
						 IDE.openEditor(page,file);
					 }
						 
					 // #changed# by Konstantin Mishin on 19.12.2005 fixed for ESORM-437
					 //mapping.getProject().refresh(false);
					 
					 //TODO EXP 4d
					 //mapping.getProject().refresh(true);
					 mapping.setFlagDirty(true);
					 // #changed#

			}
			
	        // TODO (tau->tau) del? 27.01.2006
			// move tau 27.01.2006 -> ActionExplorerVisitor.hibernateConnectionWizardAction
			//mapping.getProject().fireProjectChanged(this, false);
			
		}
			catch (IOException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage().toString());
			}
			catch (CoreException e) {
				ExceptionHandler.handle(e, ViewPlugin.getActiveWorkbenchShell(), null,null);
			} catch (Throwable e) { 
				ExceptionHandler.logThrowableError(e,"Hibernate Connection  Wizard:Finish ->"+e.toString());
			}
		return true;
	}

	
	private boolean createNewFolders(){
		IPath ipath=page0.getPath();
		IPath pathTmp=null;
		IFolder folder=null;
		int tmp;
		try {
			 pathTmp=ipath.removeLastSegments(1);
			 pathTmp=pathTmp.removeFirstSegments(1);
				int countFoldrs =pathTmp.segmentCount();
				for(int i=0;i<=countFoldrs;i++){
					tmp=countFoldrs-i;
					if(tmp!=countFoldrs){
						folder=mapping.getProject().getProject().getFolder(pathTmp.removeLastSegments(tmp));
						if(!folder.exists() && !fCurrJProject.getProject().getFile(pathTmp.removeLastSegments(tmp)).exists())//{//&& folder.getResourceAttributes()==null)
								folder.create(IResource.NONE, true, null);
						else if(!folder.exists() && fCurrJProject.getProject().getFile(pathTmp.removeLastSegments(tmp)).exists()){
							 MessageDialog.openError(getShell(), null,BUNDLE.getString("HibernateConnectionWizard.resourceExist"));
							 return false;
						}
					}
				}

		 	}catch (CoreException e1) {
            	//TODO (tau-tau) for Exception		 		
				ExceptionHandler.handle(e1, ViewPlugin.getActiveWorkbenchShell(), null,null);
				return false;
		 	}
		return true;
	}
	
	
	/**
	 * Add pages to the wizard
	 */
	public void addPages() {

		page0 = new HibernateConnectionWizardPage0(mapping);
		addPage(page0);
		
		
		page1 = new HibernateConnectionWizardPage1(mapping);
		addPage(page1);

		page2 = new HibernateConnectionWizardPage2(mapping);
		addPage(page2);
		
		page3 = new HibernateConnectionWizardPage3(mapping);
		addPage(page3);
		
		page4 = new HibernateConnectionWizardPage4(mapping);
		addPage(page4);
		page4.setPageComplete(false);

		page5 = new HibernateConnectionWizardPage5(mapping);
		addPage(page5);
		page5.setPageComplete(true);
		
		//Set Image of the wizard
		ImageDescriptor descriptor = null;
		
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);
	}
	

	/**
	 * implementation CanFinish. Here is placed the view logic of the wizard
	 */
	public boolean canFinish() {

		boolean result = true;
		if (((HibernateConfiguration) mapping.getConfiguration()).getProperty("hibernate.dialect") == "") {
			result = false;			
		}

		currentpage = this.getContainer().getCurrentPage();
		if (currentpage instanceof HibernateConnectionWizardPage0) {
			if (!page2.isSupport())
				//return result = false;
				result = false;				
			if (page0.getWrongNameFile())
				//return result = false;
				result = false;				
		}

		// add tau 13.02.2006
		// for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?		
		if (this.readOnly) result = false;
		
		return result;		

	}


	
	/**
	 * implementation getNextPage. Here is placed the view logic of the wizard
	 * @param page
	 * @return
	 * 
	**/

public IWizardPage getNextPage(IWizardPage page) {
	currentpage=this.getContainer().getCurrentPage();
	IWizardPage nextPage=page;
	if (page instanceof HibernateConnectionWizardPage0){
		if(!page2.isSupport()){
			MessageDialog.openError(getShell(), BUNDLE_IMAGE.getString("HibernateConnectionWizard.ErrorDialogTitle"), BUNDLE_IMAGE.getString("HibernateConnectionWizard.ErrorDialogMessage"));
			return page;
		}else if(page0.isNewConfig())
				page2.setDialect();
		// #deleted# by Konstantin Mishin on 13.01.2006 fixed for ESORM-478
//		try {
//			mapping.getConfiguration().save();
//		} catch (IOException e) {
//			ExceptionHandler.logThrowableError(e, e.getMessage().toString());
//		} catch (CoreException e) {
//			ExceptionHandler.handle(e, ViewPlugin.getActiveWorkbenchShell(), null,null);
//		}
		// #deleted#
		if(page0.getGeneralHibConf().isDirty()){
			if(page2.setDialect()!=-1){
				//MessageDialog.openError(getShell(), null, "This version does not support");
				//return null;
			
			//if(mapping.getConfiguration().getProperty(BUNDLE.getString("HibernateConnectionWizardPage2.property_hibernate.dialect"))=="")
			//		MessageDialog.openError(getShell(), null, "Hibernate dialect property must be set");
			//else
				nextPage =super.getNextPage(page0);		
			}
		}else nextPage =super.getNextPage(page0);
			
	}
			
		if (page instanceof HibernateConnectionWizardPage2){
			if (page.isPageComplete()){
				if(page2.getIsDialectChange())
					page2.saveConfiguration();
					
				nextPage = super.getNextPage(page4);
			}
			
		}
		
		if (page instanceof HibernateConnectionWizardPage3){
				nextPage =	super.getNextPage(page);
		}
		
		if (page instanceof HibernateConnectionWizardPage4){
			if (page.isPageComplete())
				nextPage = super.getNextPage(page);
		}
		
		if (page instanceof HibernateConnectionWizardPage1){
			page4.setProvider();
			page5.setNewProperty();
			if(page2.getIsDialectChange()){
				page2.saveConfiguration();
				page0.formList();
				page2.setIsDialectChange(false);
			}
			if(page1.JDBCButton.getSelection())
				nextPage=super.getNextPage(page1);
			
			if(page1.DataSourceButton.getSelection())
				nextPage = super.getNextPage(page2);
			
			if(page1.CustomConnButton.getSelection())
				nextPage = super.getNextPage(page3);
			
		}
		return nextPage;
	}
	
	
	/**
	 * implementation performCancel. Reaction on clicking cancel button
	 */
	public boolean performCancel() {
		IProgressMonitor monitor = new NullProgressMonitor();
		if (page2.getConnection() != null) {
			try {
				page2.getConnection().close();
			} catch (SQLException e1) {
				// TODO (tau-tau) for Exception
				ExceptionHandler.logThrowableError(e1, e1.getMessage());
			}
		}
		
    	//TODO (tau-tau) for Exception		
		try {
			
			if(isExistCfgFile){
				
				if (((HibernateConfiguration)mapping.getConfiguration()).isDirty()) {
					mapping.getConfiguration().reload();
					mapping.refresh(false, false); // edit tau 17.11.2005					
				}

				//added by alex on 06/07/05
				
				//mapping.refresh(false, true); // edit tau 17.11.2005
				//TODO EXP 5d				
				//mapping.refresh(false, false); // edit tau 17.11.2005
				
				
//				if(page1.isDeleteHPFile())
//					if(mapping.getProperties().getResource()!=null)
//						mapping.getProperties().getResource().delete(IResource.NONE,monitor);
			}
			else{ 
				mapping.getConfiguration().getResource().delete(IResource.NONE,monitor);
//				if(page1.isDeleteHPFile())
//					mapping.getProperties().getResource().delete(IResource.NONE,monitor);
				mapping.getProject().refresh( true); // edit tau 17.11.2005
			}
			 
		} catch (IOException e) {
			ExceptionHandler.logThrowableError(e, e.getMessage().toString());
		} catch (CoreException e) {
			ExceptionHandler.handle(e, ViewPlugin.getActiveWorkbenchShell(), e.getMessage(),e.getMessage());
		}
		return super.performCancel();
	}
	

	
	/**
	 * addPathToClassPath. Add path to the classpath of the project
	 * @param path
	 */
	
	public void addPathToClassPath(String path) throws JavaModelException{
	    IJavaProject javaProject =  JavaCore.create(mapping.getProject().getProject());
	    IPath pathLib = new Path(path);
	    if (pathLib.isAbsolute()){
			//Add paths to the classpath	
			IProgressMonitor progressMonitor = new NullProgressMonitor();		    
			IClasspathEntry classpathEntryLib = JavaCore.newLibraryEntry( pathLib, null, null, false);
	    
			IClasspathEntry[] oldClasspath = javaProject.readRawClasspath();
			if ( checkClassPath(oldClasspath,path)== false){
				IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length + 1];
				System.arraycopy(oldClasspath, 0, newClasspath, 0, oldClasspath.length);
				newClasspath[oldClasspath.length]= classpathEntryLib;
				javaProject.setRawClasspath(newClasspath, progressMonitor);
			}
		}
	}
	
	/**
	 * checkClassPath check path string if it was in classpath.
	 * @param oldClasspath
	 * @param path
	 */
	public boolean checkClassPath(IClasspathEntry[] oldClasspath,String path){
		boolean result=false;
		
		IPath p = new Path(path);
		for (int i=0; i < oldClasspath.length;i++)	
		{  
			if (oldClasspath[i].getPath().toString().equals(p.toString().trim())){
			result=true;
			break;
			}
		}
		return result;
		}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
	 * akuzmin 15.08.2005
	 */
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page instanceof HibernateConnectionWizardPage0) 
			page0.formList();
		return super.getPreviousPage(page);
	}
	
	
}
