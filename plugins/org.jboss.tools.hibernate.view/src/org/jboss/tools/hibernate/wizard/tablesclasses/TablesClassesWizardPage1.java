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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.dialog.ModelCheckedTreeSelectionDialog;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.jboss.tools.hibernate.wizard.hibernateconnection.HibernateConnectionWizardPage2;
import org.jboss.tools.hibernate.wizard.treemodel.TablesLabelProvider;
import org.jboss.tools.hibernate.wizard.treemodel.TablesTreeModel;
import org.jboss.tools.hibernate.wizard.treemodel.TreeModelContentProvider;






/**
 * @author sushko and gavrs
 *
 */
public class TablesClassesWizardPage1 extends WizardPage {
	public static String[] TABLE = {"TABLE"};
	public static String[] VIEW = {"VIEW"};
    
    // added by Nick 19.09.2005
    public static String[] TABLE_VIEW = {"TABLE","VIEW"};
    // by Nick
    
	public static String NAME_TABLE = "TABLE_NAME";
	public static final String BUNDLE_NAME = "tablesclasses"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(TablesClassesWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping ormmodel;
    private Button TablesClassesButton,removeButton;
    private List list;
    String prevCatalog;
    private boolean isSchemaNull=false;
    private Object[] selectedTables;
    private TablesTreeModel allTables;
	HibernateConnectionWizardPage2 page0;

    
	/**
	 * createControl() of the wizard
	 * @param parent
	 */
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));
		container.setLayout(layout);
		
	    GridData labelData =   new GridData();
	    labelData.horizontalAlignment = GridData.FILL;
	    labelData.horizontalSpan = 2;
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("TablesClassesWizardPage1.label"));
	    label.setLayoutData(labelData);
		
	    GridData lData =   new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
	    
		list = new List(container,SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
		list.setBackground(new Color(null,255,255,255));
		list.setLayoutData(lData);
		list.addSelectionListener(new SelectionListener()
				{
			
			public void widgetSelected(SelectionEvent e) {
				removeButton.setEnabled(true);
				
			}

			public void widgetDefaultSelected(SelectionEvent e) {

				
			}
	}
);
		TablesClassesButton= new Button(container, SWT.PUSH);
		TablesClassesButton.setText(BUNDLE.getString("TablesClassesWizardPage1.TablesClassesButton"));
		GridData d=setButtonLayoutData( TablesClassesButton);
		TablesClassesButton.setLayoutData(d);
		TablesClassesButton.setLayoutData(d);
		 removeButton=new Button(container, SWT.PUSH);
	   // gd=new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END);
		removeButton.setText(BUNDLE.getString("TablesClassesWizardPage1.RemoveButton"));
		removeButton.setEnabled(false);
		d.verticalAlignment = GridData.BEGINNING;
		removeButton.setLayoutData(d);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				RemoveTable();
			}
		});
		
		this.setPageComplete(false);
		TablesClassesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DoBrowse();
				DoCheck();
			}
		});
		
		setControl(container);		
	}
	private void RemoveTable(){
		list.setRedraw(false);
		list.remove(list.getSelectionIndices());
		list.setRedraw(true);		
		removeButton.setEnabled(false);
		DoCheck();
		//getWizard().getContainer().updateButtons();
		
	}

	/**
	 * DoCheck() of the TablesClassesWizardPage1
	 */
	public void DoCheck() {
		if (list.getItemCount() == 0) 
		this.setPageComplete(false);
		else
		this.setPageComplete(true);
			
	}
	
	/**
	 * constructor of the TablesClassesWizardPage1
	 * @param ormmodel
	 */
	public TablesClassesWizardPage1(IMapping ormmodel){
		super("wizardPage");
		setTitle(BUNDLE.getString("TablesClassesWizardPage1.Title"));
		setDescription(BUNDLE.getString("TablesClassesWizardPage1.Description"));
		this.ormmodel = ormmodel;
		//support.setFinished(false);
	}
	
	
	/**
	 * canFlipToNextPage of the TablesClassesWizardPage1
	 */
	public boolean canFlipToNextPage() {
		return this.isPageComplete();
		
	}
	
	/**
	 * isPageComplete()of the TablesClassesWizardPage1
	 */
	public boolean isPageComplete() {
		return super.isPageComplete();
	}
	
	/**
	 * DoBrowse()of the TablesClassesWizardPage1
	 */
	public void DoBrowse() {
		String currentCatalog = null;
		allTables = new TablesTreeModel("root",null,false);
		if (ormmodel != null) {

			HibernateConnectionWizardPage2 p = (HibernateConnectionWizardPage2) getWizard()
					.getPreviousPage(this);
			Connection con = p.testConnection(false);// p.getLocationpath(),p.getLocationnames(),

			// Connection con=p.getConnection();
			if (con != null) {
				try {

					currentCatalog = con.getCatalog();

				} catch (SQLException e) {
	            	//TODO (tau-tau) for Exception					
					ExceptionHandler.logThrowableError(e, e.getMessage());
				}
				if (prevCatalog != null && currentCatalog != null)
					if (!prevCatalog.equals(currentCatalog))
						list.removeAll();
					getAllTables(con);

					ModelCheckedTreeSelectionDialog dlg = new ModelCheckedTreeSelectionDialog(
						getShell(), new TablesLabelProvider(),
						new TreeModelContentProvider()
						);
				dlg.setTitle(BUNDLE.getString("TablesClassesWizardPage1.dialogtitle"));
				dlg.setMessage(BUNDLE.getString("TablesClassesWizardPage1.dialogtext"));
				dlg.setInput(allTables);
				dlg.open();
				if (dlg.getReturnCode() == 0) {

					selectedTables = dlg.getResult();
					for (int z = 0; z < selectedTables.length; z++)
						if (selectedTables[z] instanceof TablesTreeModel)
						{
							TablesTreeModel elem = (TablesTreeModel) selectedTables[z];
							if (elem.getChildren().length==0)
							{
								if (isSchemaNull)
								{
									if (list.indexOf(elem.getName()) < 0)
										list.add(elem.getName());
								}
								else if (list.indexOf(elem.getParent().getName()+"."+elem.getName()) < 0)
										list.add(elem.getParent().getName()+"."+elem.getName());

									
							}
								
						}
					//akuzmin 03.08.2005
					list.setRedraw(false);
					sortList();
					list.setRedraw(true);
				}
			}
			prevCatalog = currentCatalog;
		}
		getWizard().getContainer().updateButtons();
	}
	

	//akuzmin 03.08.2005
	private void sortList() {
	String [] values=list.getItems();
	Arrays.sort(values);
	list.removeAll();
	list.setItems(values);
	}

	
	/**
	 * DoBrowse()of the TablesClassesWizardPage1
	 * @param con
	 */
	private void getAllTables(Connection con){
		String catalogName=null;
		ResultSet rs=null;
		String nameTable=null,nametableInModel=null;
		String schemaName=null;
		DatabaseMetaData metadata=null;
		
		try{
			 metadata=con.getMetaData();
			 rs = metadata.getTables(null,null, null, TABLE_VIEW); // changed by Nick 19.09.2005
			 while (rs.next()) {
				// try{ catalogName=rs.getString("TABLE_CAT"); } catch(SQLException sqlex){ExceptionHandler.log(sqlex, sqlex.getMessage().toString());}
				 
           	//TODO (tau-tau) for Exception				 
				 try{ schemaName=rs.getString("TABLE_SCHEM"); } catch(SQLException sqlex){ExceptionHandler.logThrowableError(sqlex, sqlex.getMessage().toString());}
				 catalogName="";
				 nameTable=rs.getString(NAME_TABLE);
				 nameTable=StringUtils.hibernateEscapeName( nameTable);				 
				 nametableInModel=Table.toFullyQualifiedName(catalogName,schemaName,nameTable);
				 if(( ormmodel.findTable( nametableInModel)==null)
					 && (list.indexOf(nametableInModel)<0))
				 {
					 if (schemaName==null)
					 {
						 schemaName=con.getCatalog();
						 if (schemaName==null)
							 schemaName=BUNDLE.getString("TablesClassesWizardPage1.defaultdbname"); 
						 isSchemaNull=true;
					 }
					 else isSchemaNull=false;
					 
					 if (allTables.getNode(schemaName)==null)
					 	allTables.addNode(schemaName,false);
					 TablesTreeModel shemanode = (TablesTreeModel) allTables.getNode(schemaName);
					 //akuzmin 20.09.2005
					 	shemanode.addNode(nameTable,VIEW[0].equals(rs.getString("TABLE_TYPE")));
				 }
			 }
		}catch(SQLException ex){
        	//TODO (tau-tau) for Exception			
			ExceptionHandler.logThrowableError(ex, ex.getMessage());
		}
//		akuzmin 20.09.2005
		finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
            	//TODO (tau-tau) for Exception				
				ExceptionHandler.logThrowableError(ex, ex.getMessage().toString());
			}
		}
		 //end akuzmin 20.09.2005		
	}

	public String getItem(int i) {
		return list.getItem(i);
	}

	public int getItemCount() {
		return list.getItemCount();
	}
	
}
