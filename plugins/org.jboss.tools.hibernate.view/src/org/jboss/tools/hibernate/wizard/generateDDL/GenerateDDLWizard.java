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
package org.jboss.tools.hibernate.wizard.generateDDL;

import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.view.ViewPlugin;


public class GenerateDDLWizard extends Wizard{
	public static final String BUNDLE_NAME = "generate"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(GenerateDDLPage1.class.getPackage().getName() + "." + BUNDLE_NAME);
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;	
	private GenerateDDLPage1 page1;
	private IWizardPage thispage;
	private IMapping mod;
	
	private IWizardPage currentpage;
	public GenerateDDLWizard(IMapping mod)
	 {
	 super();
	 setNeedsProgressMonitor(true);
	 this.mod=mod;
	 this.setWindowTitle(BUNDLE.getString("GenerateDDLWizard.Title"));  	
	 }  
	  
	  
	  
	
	public void addPages() {
		page1 = new GenerateDDLPage1(mod);		
		addPage(page1);
		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);
	
		
	}

	   	public boolean performFinish()
	   	{
			
			IRunnableWithProgress op = new IRunnableWithProgress() {
   				public void run(IProgressMonitor monitor) {
					try {
							
						doFinish(monitor);
					} catch (Exception e) {
						//ExceptionHandler.logThrowableError(e,e.getMessage());
						ExceptionHandler.handleAsyncExec(new InvocationTargetException(e), getShell(), null, null, IStatus.ERROR);						
					}
					
  				monitor.done();
   				}};
   				
   		try {
			getContainer().run(true, false, op);
		} catch (InvocationTargetException e) {
			//ExceptionHandler.logThrowableError(e.getTargetException(), null);
			ExceptionHandler.handle(e, getShell(), null,	null);			
		} catch (InterruptedException e) {
			//ExceptionHandler.logThrowableError(e,"Interrupt accured");
			ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,	null);			
		} catch (Throwable e) { 
			ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,	null);			
			//ExceptionHandler.logThrowableError(e,"Generate DDL Wizard:Finish ->"+e.getMessage());
		}
	   	return true;
	   	}
	   	


		public IWizardPage getNextPage(IWizardPage page)
		{
		IWizardPage nextPage = super.getNextPage(page);			
		//thispage=page;		
		return nextPage;
		}

 		private void doFinish(IProgressMonitor monitor) throws Exception{
				monitor.beginTask(BUNDLE.getString("GenerateDDLWizard.TaskName"),5);
				monitor.worked(1);
				 page1.execute();	
				 getShell().getDisplay().asyncExec(new Runnable() {
	 					public void run() {
	 						IWorkbenchPage page =
	 							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	 					}
	 				});
 				monitor.worked(1);
 			}
 		
 		public boolean canFinish() {
			boolean result =false;
			currentpage=this.getContainer().getCurrentPage();
 			if (currentpage instanceof GenerateDDLPage1 && currentpage.isPageComplete())
 			{
				result = true;
				
 			}
			return result;
 		}
 		
		
		
}
