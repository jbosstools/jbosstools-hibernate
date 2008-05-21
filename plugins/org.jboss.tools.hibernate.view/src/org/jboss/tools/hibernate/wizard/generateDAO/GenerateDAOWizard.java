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
package org.jboss.tools.hibernate.wizard.generateDAO;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import org.eclipse.core.internal.resources.WorkspaceRoot;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.ContextFileWriter;
import org.jboss.tools.hibernate.internal.core.PersistentClass;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.view.ViewPlugin;



/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 16, 2005
 */
public class GenerateDAOWizard extends Wizard {
	public static final String BUNDLE_NAME = "generateDAO"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(GenerateDAOWizard.class.getPackage().getName() + "." + BUNDLE_NAME);
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;	
	private IMapping mod;
	private IWizardPage thispage;
	private GenerateDAOPage1 page1;
	private GenerateDAOPage2 page2;
	private GenerateDAOPage3 page3;	
	private IPersistentClass[] classes;
	 /**
	 * @param mod - IMapping for take persistent classes 
	 */
	public GenerateDAOWizard(IMapping mod)
	   {
	  	super();
	  	setNeedsProgressMonitor(true);
	  	this.mod=mod;
		this.setWindowTitle(BUNDLE.getString("GenerateDAOWizard.title"));
		classes=new PersistentClass[0];
		}
	  
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
				doFinish(monitor);
				monitor.done();
				}};
				
        //TODO (tau-tau) for Exception				
		try {
		getContainer().run(true, false, op);
	} catch (InvocationTargetException e) {
		ExceptionHandler.logThrowableError(e.getTargetException(), null);			
	} catch (InterruptedException e) {
		ExceptionHandler.logThrowableError(e,"Interrupt accured");
	}catch (Throwable e) { 
		ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
	}
   	return true;
   	}

	public IWizardPage getNextPage(IWizardPage page) {
		
		IWizardPage nextPage = super.getNextPage(page);			
	
//	if (isfinish)
//	{
//		thispage=null;
//		isfinish=false;
//	}
//	else
//	{
		//thispage=nextPage; del tau 20.01.2006
//		isfinish=false;		
//	}
		if (nextPage != null) {
			thispage=nextPage;
		}
	
		return nextPage;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	public void addPages() {
	
		page1 = new GenerateDAOPage1(mod);		
		addPage(page1);

		page2 = new GenerateDAOPage2(mod);
		addPage(page2);
		page2.setPageComplete(false);		
		
		page3 = new GenerateDAOPage3(mod); 
		addPage(page3);
		// tau 20.01.2006
//		page3.setPageComplete(false);

		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);
	}

		public boolean canFinish() {
 			if (thispage instanceof GenerateDAOPage2)
 			{
				classes=new PersistentClass[page1.getItemCount()];
 				for(int i=0;i<page1.getItemCount();i++)
 				{
 					classes[i]=mod.findClass(page1.getItem(i));
 				}
 			}
 			
 			// del tau 20.01.2006
 			 			
 			if ((page2.canFlipToNextPage())&&(page1.canFlipToNextPage()))
 			{
 				return super.canFinish();
 			} 			

 			if (thispage instanceof GenerateDAOPage3)
 			{
 				return super.canFinish();
 			}	else return false;
 			

 		/*	if (thispage instanceof GenerateDAOPage2)
 			{
 				return super.canFinish();
 			}	else return false;*/

 		}

 		private void doFinish(IProgressMonitor monitor) {
 		        OrmProgressMonitor.setMonitor(monitor);
                monitor.beginTask("Creating ", 100);
 
 				monitor.setTaskName(BUNDLE.getString("GenerateDAOWizard.taskname"));
				try {
//	 				mod.getConfiguration().save();
					
					//add tau 19.04.2006 -> ESORM-582: NullPointerException - orm2.core.CodeRendererService.importTypeName
					// move to PersistentClass.getPersistentClassMapping()
//					for (int i = 0; i < classes.length; i++) {
//						IPersistentClass persistentClass = classes[i];
//						persistentClass.getFields();
//						
//					}
					
	 				mod.addDAOClasses(classes, page2.isCheckInterfaces(), page2.isCheckLogging(), page2.isCheckTest(), page2.getNamePackage());
	 				if(page3.generateSpring()) {
	 					IFile	file = ((WorkspaceRoot)ResourcesPlugin.getWorkspace().getRoot()).getFile(page3.getStrPath());
	 					(new ContextFileWriter((HibernateConfiguration)mod.getConfiguration(), file, classes, page2.getNamePackage(), page2.isCheckInterfaces())).write();
	 				}
				} catch ( final IOException e) {
					getShell().getDisplay().asyncExec(new Runnable() {
	 					public void run() {
	 						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, e.getMessage());
	 					}
	 				});
				} catch ( final CoreException e) {
					getShell().getDisplay().asyncExec(new Runnable() {
	 					public void run() {
	 						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, e.getMessage());
	 					}
	 				});
				}

// 				getShell().getDisplay().asyncExec(new Runnable() {
// 					public void run() {
// 						IWorkbenchPage page =
// 							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
// 					}
// 				});
                OrmProgressMonitor.setMonitor(null);
        }
		
}
