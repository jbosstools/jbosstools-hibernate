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
package org.jboss.tools.hibernate.wizard.refreshmappingwizard;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ReversStatistic;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ReadOnlyWizard;
import org.jboss.tools.hibernate.wizard.hibernateconnection.HibernateConnectionWizardPage1;
import org.jboss.tools.hibernate.wizard.hibernateconnection.HibernateConnectionWizardPage2;



public class RefreshMappingWizard  extends ReadOnlyWizard {
	
	public static String[] TABLE = {"TABLE"};
	public static String[] TABLE_VIEW = {"TABLE","VIEW"};
	public static String NAME_TABLE = "TABLE_NAME";
	public static final String BUNDLE_NAME = "refresh"; 
	private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(RefreshMappingWizard.class.getPackage().getName() + "." + BUNDLE_NAME);
	private HibernateConnectionWizardPage2 page1;
	HibernateConnectionWizardPage1 page2;
	private IMapping mod;
	private Connection con;
	private Object type;
	// private IDatabaseTable[] tables; del tau 03.08.2005

	boolean result;
	private String schName,tableName="";
	Object[] namesOllTablesStr=null;
	
	public RefreshMappingWizard(Object type, IMapping mod, TreeViewer viewer) {
		// edit tau 16.02.2006
		super((IOrmElement) type, viewer);			
		setNeedsProgressMonitor(true);
		this.mod = mod;
		this.type = type;

		this.setWindowTitle(BUNDLE.getString("RefreshMappingWizard.Title"));

	}
	
	/**
	 * Add pages to the wizard
	 */
	public void addPages() {
		page1 = new HibernateConnectionWizardPage2(mod);
		page1.setMessage(BUNDLE.getString("RefreshMappingWizard.Description"));
		addPage(page1);
		ImageDescriptor descriptor = null;
		descriptor = ViewPlugin.getImageDescriptor(BUNDLE_IMAGE.getString("Wizard.Title"));
		setDefaultPageImageDescriptor(descriptor);
		
		
	}
	/**
	 * performFinish() of the wizard. Reaction on clicking finish button
	 */
	public boolean performFinish(){	
	
		IRunnableWithProgress op = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {	
					result=doFinish(monitor);
					monitor.done();
				}};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(e, "Interrupt occured");
			return false;
		} catch (Throwable e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(e, "Refresh Mapping Wizard:Finish");
			return false;
		} finally {
			if (con != null){
				try {
					con.close();
				} catch (SQLException e) {
	            	//TODO (tau-tau) for Exception					
					ExceptionHandler.logThrowableError(e, "Refresh Mapping Wizard:Finish");					
				}
			}
		}
		
		return result;

   	}
	
	/**
	 * implementation CanFinish. Here is placed the view logic of the wizard
	 */
	public boolean canFinish() {
		boolean result;
		if(page1.isPageComplete() ){	
			result=true;
		}else result=false;
		
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
	public IWizardPage getNextPage(IWizardPage page)
	{		
	IWizardPage nextPage = super.getNextPage(page);					
	return nextPage;
	}
	
	 public IWizardPage getStartingPage() {
			IWizardPage tmpPage= super.getStartingPage();
			if (tmpPage instanceof HibernateConnectionWizardPage2){
				((HibernateConnectionWizardPage2)tmpPage).setTitle("Synchronize Schema Mapping");		
			}
		        return super.getStartingPage();
	}
	
	private boolean doFinish(IProgressMonitor monitor){
		OrmProgressMonitor.setMonitor(monitor);
		monitor.beginTask("Waiting for server response...",100);
		try {

			if (type instanceof IDatabaseSchema || type instanceof IDatabaseTable || type instanceof IMapping) {
				if (!refreshSchemaToObject())
					return false;
			} else
				refreshObjectToSchema();
		} catch (Throwable e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(e, "Refresh Mapping Wizard:Finish");
		}

		/* del tau 24.11.2005
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
			}
		});
		*/
		OrmProgressMonitor.setMonitor(null);
		return true;
		}
	
	
	/**
	 * implementation refreshSchemaToObject(). 
	 * 
	 * @return boolean
	 * 
	**/
	
	
	private boolean refreshSchemaToObject(){
		boolean result;
		con=page1.testConnection(false);
		if(con==null)
			return false;

        if (type instanceof IDatabaseSchema) {
        	
        	IDatabaseSchema databaseSchema = (IDatabaseSchema) type;
        	
        	// add tau 12.05.2006 for ESORM-606        	
        	IMappingStorage[] mappingStorages = databaseSchema.getProjectMapping().getMappingStorages();
        	for (int i = 0; i < mappingStorages.length; i++) {
				IMappingStorage storage = mappingStorages[i];
				storage.setDirty(true);
				
			}
        	
        	result=dorefreshObjectToSchema((IDatabaseSchema) type, null );
        	
        	return result;
        } else if (type instanceof IDatabaseTable) {///  call On the name of table 
        	IDatabaseTable databaseTable = (IDatabaseTable) type;
        	
        	// add tau 12.05.2006 for ESORM-606 
        	IPersistentClassMapping[] persistentClassMapping = databaseTable.getPersistentClassMappings();
        	for (int i = 0; i < persistentClassMapping.length; i++) {
				IPersistentClassMapping mapping = persistentClassMapping[i];
				mapping.getStorage().setDirty(true);
			}
        	
            IDatabaseSchema schema = databaseTable.getSchema();        	
        	result=dorefreshObjectToSchema(schema, databaseTable );
        	
        	return result;
        } else if ((type instanceof IMapping)){
        	IMapping mapping = (IMapping) type;
        	IDatabaseSchema[] databaseSchemas = mapping.getDatabaseSchemas();
        	for (int i = 0; i < databaseSchemas.length; i++) {
				IDatabaseSchema schema = databaseSchemas[i];
	        	dorefreshObjectToSchema(schema, null );	
			}
    		return true;        	
        } else return false;

	}

    // add tau 29.07.2005
    private boolean  dorefreshObjectToSchema(IDatabaseSchema schema, IDatabaseTable table ){
		DatabaseMetaData metadata=null;
		ResultSet rs=null;
		String catalogName=null;
		String nameTable=null;
//		String schemaName=null;
		String[] namesOllTablesStr;
		ArrayList<String> refreshingTables = new ArrayList<String>();
		tableName = null;
		final StringBuffer nameTableBuffer = new StringBuffer();
		ArrayList<String> noRefreshingTables = new ArrayList<String>();		
        
        schName= schema.getShortName();
        String catName = schema.getCatalog();
//        if (table != null) {
//        	tableName = table.getShortName();
//        	tableName = StringUtils.hibernateUnEscapeName(tableName); // add 03.08.2005 tau
//        }
       
        // add tau 03.08.2005
        if (table != null) {
			noRefreshingTables.add(table.getShortName());
		} else {

			IDatabaseTable[] d = mod.getDatabaseTables();
			for (int j = 0; j < d.length; j++)
				if(d[j].getSchemaName().equals(schName))// add gavrs 10/8/2005
					noRefreshingTables.add(d[j].getShortName());
		}
			
		try {
			metadata = con.getMetaData();
			if(schName.length() == 0) schName = null; // need "null" instead of empty string.
			
			for (int z = 0; z < noRefreshingTables.size(); z++)
			{
				tableName=(String) noRefreshingTables.get(z);
				tableName = StringUtils.hibernateUnEscapeName(tableName);				
				rs = metadata.getTables(catName, schName, tableName, TABLE_VIEW);//TABLE);				
				while (rs.next()){
					nameTable=Table.toFullyQualifiedName(catalogName,schName,(String) noRefreshingTables.get(z));
				// 7/29
				if (mod.findTable(nameTable) != null)
						refreshingTables.add(nameTable);						
				}
			}
		} catch (SQLException e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(e, e.getMessage().toString());
		}
		//akuzmin 20.09.2005
		finally {
			try{ 
				if(rs!=null) rs.close();
			}catch(SQLException ex) {
            	//TODO (tau-tau) for Exception				
				ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
			}
		}		

		// add tau 03.08.2005
		for (Iterator iter = noRefreshingTables.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			if (schName!=null)
				element =schName+'.'+element;
			if (!refreshingTables.contains(element)) {
				nameTableBuffer.delete(0,nameTableBuffer.length());
				nameTableBuffer.append(element);			
				getShell().getDisplay().syncExec(new Runnable(){
       				public void run(){
       					MessageDialog.openError(getShell(),
       							BUNDLE.getString("RefreshMappingWizard.TitleError"),
       							MessageFormat.format(BUNDLE.getString("RefreshMappingWizard.NorefreshingTables"),new Object[]{nameTableBuffer}));
       				}
       			});
			}
		}
		
		//add tau 03.08.2005
		if (refreshingTables.size() == 0) return false;
		
		namesOllTablesStr = new String[refreshingTables.size()];		
		
		try {
			for (int i = 0; i < refreshingTables.size(); i++) {
				// changed by Nick 26.07.2005
				// TABLES store associated CMs and column values! Do not remove
				// them from the model!!!!
				// mod.removeDatabaseTable(refreshingTables.get(i).toString());
				// by Nick
				namesOllTablesStr[i] = refreshingTables.get(i).toString();
			}
			ReversStatistic results = new ReversStatistic();
			mod.addDatabasesTables(con, namesOllTablesStr, true, false,	results);
		} catch (IOException e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(e, e.getMessage().toString());
		} catch (CoreException e) {
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(e, e.getMessage().toString());
		}
		return true;
	}
	
    // nikogda ne vizivaetsya / add tau 03.08.2005
	private void refreshObjectToSchema(){
		String[] namesOllClassesStr;
		ArrayList<String> tableListInModel = new ArrayList<String>();		
		ArrayList<String> persClasses = new ArrayList<String>();
		tableListInModel.clear();
		for(int i=0;i<mod.getDatabaseTables().length;i++){
			tableListInModel.add(mod.getDatabaseTables()[i].getName());
		}
			IPersistentClass[] PertsistentClassesobj=mod.getPertsistentClasses();
		 	for (int j = 0; j < PertsistentClassesobj.length; j++)
		 	{
		 		persClasses.add(PertsistentClassesobj[j].getName());	
		 	}	
		 	
		 	
		 	namesOllClassesStr=new String[persClasses.size()];
		 	 for(int i=0;i<persClasses.size();i++){
			
		 		namesOllClassesStr[i]=persClasses.get(i).toString();
			 }
		 	try {
				mod.addPersistentClasses(namesOllClassesStr, true,false);
			} catch (IOException e) {
            	//TODO (tau-tau) for Exception				
				ExceptionHandler.logThrowableError(e, e.getMessage().toString());
			} catch (CoreException e) {
            	//TODO (tau-tau) for Exception				
				ExceptionHandler.logThrowableError(e, e.getMessage().toString());
			}
		
	}
	public void init(){
		page1.testConnection(true);
	}
}
