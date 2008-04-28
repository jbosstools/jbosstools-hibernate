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
package org.jboss.tools.hibernate.wizard.persistentclasses;

/**
 * @author kaa
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;



public class PersistentClassesWizard extends ReadOnlyWizard{
	public static final String BUNDLE_NAME = "persistentclasses"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassesPage.class.getPackage().getName() + "." + BUNDLE_NAME);
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;	
	private PersistentClassesPage page1;
	private PersistentClassesPage2 page2;
	private PersistentClassesPage3 page3;
	private IWizardPage thispage;
	private ArrayList<String> classes;
	private String schema;
//	,catalog;
	private IMapping mod;

	public void addPages() {
		page1 = new PersistentClassesPage(mod);		
		addPage(page1);

		page2 = new PersistentClassesPage2(mod);
		addPage(page2);
		
//		page3 = new PersistentClassesPage3(mod);
//		addPage(page3);
		//page1.setPageComplete(false);
		//page2.setPageComplete(false);

		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);
	}

   	public boolean performFinish() {
   		//do something with progress bar
	   	schema=page2.GetSchema();
//	   	catalog=page2.GetCatalog();
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
	   	
  public PersistentClassesWizard(IMapping mod, TreeViewer viewer) {
  	super(mod, viewer); // edit tau 13.02.2006
  	setNeedsProgressMonitor(true);
  	classes = new ArrayList<String>();
  	this.mod=mod;
	this.setWindowTitle(BUNDLE.getString("PersistentClassesWizard.title"));  	
	   }  

		public IWizardPage getNextPage(IWizardPage page)
		{//operation on press NEXT button
		IWizardPage nextPage = super.getNextPage(page);			
		thispage=page;		
		return nextPage;
		}

 		private void doFinish(IProgressMonitor monitor) {
 			//do cicle to view progress bar
				
                // changed by Nick 03.06.2005
 		        OrmProgressMonitor.setMonitor(monitor);
                monitor.beginTask("Creating ", 100);
 		        //monitor.beginTask("Creating ", classes.size()+2);
                //monitor.worked(1);
                // by Nick

 				monitor.setTaskName(BUNDLE.getString("PersistentClassesWizard.taskname"));
 				String[] arr=new String[classes.size()];
 				//String[] arr=new String[1];
// 				mod.getProject().getConfiguration().setProperty("hibernate.default_catalog",catalog);
 				mod.getProject().getOrmConfiguration().setProperty("hibernate.default_schema",schema);
// 				mod.getConfiguration().setProperty("hibernate.default_catalog",catalog);
// 				mod.getConfiguration().setProperty("hibernate.default_schema",schema); 				
 				
				try {
					mod.getProject().getOrmConfiguration().save();
				} catch ( final IOException e) {
					getShell().getDisplay().asyncExec(new Runnable() {
	 					public void run() {
	 						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, e.getMessage());
	 					}
	 				});
					
					//e1.printStackTrace();
				} catch ( final CoreException e) {
					getShell().getDisplay().asyncExec(new Runnable() {
	 					public void run() {
	 						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, e.getMessage());
	 					}
	 				});
				}
 				for (int i=0; i<classes.size(); i++) {
 				arr[i]=classes.get(i).toString();
 				//arr[0]=classes.get(i).toString();
 				//System.out.println(classes.get(i).toString());
 				//monitor.worked(1);
 				}
 				try {
					mod.addPersistentClasses(arr, page2.Check,page2.getCheckHeuristic());
				} catch ( final IOException e) {
					getShell().getDisplay().asyncExec(new Runnable() {
	 					public void run() {
	 						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, e.getMessage());
	 					}
	 				});
					//ExceptionHandler.handle(new InvocationTargetException(e), ViewPlugin.getActiveWorkbenchShell(), null,
						//	e.getMessage());
				} catch (final CoreException e) {
					getShell().getDisplay().asyncExec(new Runnable() {
	 					public void run() {
	 						ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, e.getMessage());
	 					}
	 				});
					//ExceptionHandler.handle(e, getShell(), null, null);
//				} catch (final Throwable e) {
//					getShell().getDisplay().asyncExec(new Runnable() {
//	 					public void run() {
//	 						MessageDialog.openError(ViewPlugin.getActiveWorkbenchShell(), null, e.getMessage());
//	 						//ExceptionHandler.handle(e,ViewPlugin.getActiveWorkbenchShell(),null, " was not created!");
//	 					}
//	 				});
					//ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,
					//		null);
				}
                // changed by Nick 03.06.2005
                //monitor.worked(classes.size());                 
                // by Nick
				
				//del tau 05.05.2006
// 				getShell().getDisplay().asyncExec(new Runnable() {
// 					public void run() {
// 						IWorkbenchPage page =
// 							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
// 					}
// 				});
 				
 				// changed by Nick 03.06.2005
                //monitor.worked(1);
                OrmProgressMonitor.setMonitor(null);
                // by Nick
        }
 		
 	public boolean canFinish() {
		boolean result = true; 			
		if (thispage instanceof PersistentClassesPage) {
			classes.clear();
			for(int i = 0; i < page1.getItemCount(); i++) {
				classes.add(page1.getItem(i));
			}
			result = true;
		} else {
			result = false;
		} 			
		// add tau 13.02.2006
		// for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?		
		if (this.readOnly) result = false;
		return result; 			
	}
		
}