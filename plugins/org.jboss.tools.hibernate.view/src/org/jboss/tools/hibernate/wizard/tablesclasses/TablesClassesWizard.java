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
package org.jboss.tools.hibernate.wizard.tablesclasses;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ReversStatistic;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;
import org.jboss.tools.hibernate.wizard.hibernateconnection.HibernateConnectionWizardPage2;

/**
 * @author sushko and gavrs
 *
 */
public class TablesClassesWizard extends ReadOnlyWizard {
	public static final String BUNDLE_NAME = "tablesclasses";
	public static final ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;//ResourceBundle.getBundle(ViewPlugin.class.getPackage().getName() + ".image");
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(TablesClassesWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);
	private IWizardPage thispage;
	private ArrayList<String> tables;
	protected HibernateConnectionWizardPage2 page0;
	private TablesClassesWizardPage1 page1;
	private TablesClassesWizardPage2 page2;
	private boolean  generateMapping,useHeuristicAlgorithms;
	//private IWizardPage currentpage;
	private IMapping mod;
	private Connection  connection;
	private String[] namesOllTables;
	private ReversStatistic results;
	String pack;

/**
 * performFinish() of the wizard
 */


/**
 * constructor of the wizard
 * @param viewer 
 * @param ormmodel
 */
	
	public TablesClassesWizard (IMapping mod,ReversStatistic results, TreeViewer viewer) {
		super(mod, viewer);
		setNeedsProgressMonitor(true);
		this.mod = mod;
		this.setWindowTitle(BUNDLE.getString("TablesClassesWizard.Title"));
		tables = new ArrayList<String>();
		this.results = results;
	}
	
	public IWizardPage getNextPage(IWizardPage page)
	{
	IWizardPage nextPage;
		nextPage = page;
		page=this.getContainer().getCurrentPage();
	
	if (page instanceof HibernateConnectionWizardPage2){
		//	((HibernateConnectionWizardPage2)page).setDialect();
			nextPage=super.getNextPage(page0);
	}
	if (page instanceof TablesClassesWizardPage1){
		tables.clear();
		for(int i=0;i<page1.getItemCount();i++)
		{
			tables.add(page1.getItem(i));
		}
		nextPage=super.getNextPage(page1);
	}
		
	//thispage=page;
if (page instanceof TablesClassesWizardPage2)
		nextPage=super.getNextPage(page2);

		thispage=nextPage;
	return nextPage;
	}
/**
	 * init of the wizard
	 * @param 
	 */
	
	 public IWizardPage getStartingPage() {
		IWizardPage tmpPage= super.getStartingPage();
		if (tmpPage instanceof HibernateConnectionWizardPage2){
				//((HibernateConnectionWizardPage2)tmpPage).setDialect();
				((HibernateConnectionWizardPage2)tmpPage).setTitle(BUNDLE.getString("TablesClassesWizard.PageTitle"));
			
		}
	        return super.getStartingPage();
	    }
	
	/**
	 * init of the wizard
	 * @param 
	 */
	public void init(IWorkbench workbench, IMapping ormmodel) {
		this.setWindowTitle(BUNDLE.getString("TablesClassesWizard.Title"));
	}
	public Connection getConnection(){
		return this.connection;
		
	}
	
	/**
	 * AddPages of the wizard
	 * @param ormmodel
	 */
	public void addPages() {
		page0 = new HibernateConnectionWizardPage2(mod);
		addPage(page0);
		
		page1 = new TablesClassesWizardPage1(mod);
		addPage(page1);

		page2 = new TablesClassesWizardPage2(mod);
		addPage(page2);
		//page3.setPageComplete(false);
		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));//ViewPlugin.getImageDescriptor("exdOrm16x16.gif");
		setDefaultPageImageDescriptor(descriptor);
		
	}
	public boolean performFinish()
   	{ 	
   		pack=page2.getNamePackage();
   		IStatus status=org.eclipse.jdt.core.JavaConventions.validatePackageName(pack);
   		if(!status.isOK()){
   			MessageDialog.openError(getShell(), BUNDLE.getString("TablesClassesWizard.MessageDialogTitle"), status.getMessage());
   		return false;}
   			
		IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
		     try {
				doFinish( monitor);
			} catch (Exception e) {
            	//TODO (tau-tau) for Exception			
				e.printStackTrace();
			}
				monitor.done();
				}};
		try {
		getContainer().run(true, false, op);
	} catch (InvocationTargetException e) {
    	//TODO (tau-tau) for Exception		
		e.printStackTrace();
	} catch (InterruptedException e) {
    	//TODO (tau-tau) for Exception		
		e.printStackTrace();
	}
   	return true;
   	}
	private void doFinish(IProgressMonitor monitor)throws Exception {
		
		
        
        // changed by Nick 03.06.2005
		OrmProgressMonitor.setMonitor(monitor);
        monitor.beginTask("Creating tables ", 100);
        //monitor.beginTask("Creating tables ", tables.size()+2);
        //monitor.worked(1);
        // by Nick

        namesOllTables=new String[tables.size()];
			
		for (int i=0; i<tables.size(); i++) {
			namesOllTables[i]=tables.get(i).toString();
			}
		generateMapping=page2.getCheckGenerate();
		useHeuristicAlgorithms=page2.getCheckHeuristic();
		Connection con=page0.getConnection();
        try{		
		mod.getProject().getOrmConfiguration().setProperty("hibernate.default_package",pack);
		mod.getProject().getOrmConfiguration().save();
		mod.addDatabasesTables( con,namesOllTables, generateMapping,useHeuristicAlgorithms,results);		
        }
        catch (Throwable e) { 
			ExceptionHandler.logThrowableError(e,"Reverse Engineer Database Wizard:Finish ->"+e.getMessage());
		}        
        // changed by Nick 03.06.2005
        //monitor.worked(tables.size()); 				
		// by Nick
        if (con!=null){
			try {
				con.close();
			} catch (SQLException e1) {
            	//TODO (tau-tau) for Exception				
				ExceptionHandler.logThrowableError(e1, e1.getMessage());
			}}
        
        getShell().getDisplay().asyncExec(new Runnable() {	
				public void run() {
						IWorkbenchPage page =
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						// added by Nick 11.07.2005
						//commented by akuzmin 29.07.2005
//						IResource rsrc = mod.getProject().getProject().findMember(IAutoMappingService.REVERSING_REPORT_FILENAME);
//                        if (rsrc != null && rsrc.exists() && rsrc.getType() == IResource.FILE)
//                        {
//                            try {
//                            	//akuzmin 28.07.2005
//                        		IWorkbenchBrowserSupport support= PlatformUI.getWorkbench().getBrowserSupport();
//                        		support.isInternalWebBrowserAvailable();
//                                IFile file = (IFile) rsrc;
//                                IDE.openEditor(page,file);
//                            } catch (PartInitException e) {
//                                ExceptionHandler.log(e,e.getMessage());
//                            } }
                        // by Nick
                }
															});
		// changed by Nick 03.06.2005
        OrmProgressMonitor.setMonitor(null);
        //monitor.worked(1);
		// by Nick
        
            
    }
	
	// edit tau 13.02.2006
	public boolean canFinish() {
		
		boolean result = true;		
		
		thispage=this.getContainer().getCurrentPage();//super.getStartingPage();
		if (thispage instanceof TablesClassesWizardPage2) {
			if (((TablesClassesWizardPage2)thispage).isPageComplete())
				//return true;
				result = true;				
			else result = false; // return false;
		} else result = false;
		
		// add tau 13.02.2006
		// for ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?		
		if (this.readOnly) result = false;
	
		return result;
		
		
		// edit not tau
//			if (thispage instanceof TablesClassesWizardPage1)
//			{
//				tables.clear();
//				for(int i=0;i<page1.getItemCount();i++)
//				{
//					tables.add(page1.getItem(i));
//				}
//				return false;
//			}
//		if (thispage instanceof HibernateConnectionWizardPage2){
//			((HibernateConnectionWizardPage2)thispage).setDialect();
//				return false;
//		}
		// end edit not tau
		
		//} else return false;
		
	}
	
	public ArrayList getTablesName(){
		return tables;
	}

}
