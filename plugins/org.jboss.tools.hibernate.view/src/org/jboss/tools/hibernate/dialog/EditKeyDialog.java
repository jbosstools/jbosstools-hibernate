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
package org.jboss.tools.hibernate.dialog;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.descriptors.KeyPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.jboss.tools.hibernate.view.ViewPlugin;




public class EditKeyDialog extends Dialog {
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(EditKeyDialog.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IDatabaseTable table;
	private int maxColumnnumber;
    private List ColumnList;
    private List KeyList;
//	private List ParamList;
	private String title;
	private String[] columns;
//	private String[] keyParams;
	private Button AddColumnButton;
	private Button RemoveColumnButton;
	private PropertySheetPage page;
	private SimpleValueMapping key;
//	private Button AddParamButton;
//	private Button RemoveParamButton;
//	private Combo ParamCombo;
//	private Combo ParamValueCombo;
	private Combo TableList;
//	private Text ParamValueText;
	private IMapping mod;
	
	/**
	 * @param parentShell
	 */
	public EditKeyDialog(Shell parentShell, IMapping mod, IDatabaseTable table,SimpleValueMapping key) {
		super(parentShell);
		this.setTitle(BUNDLE.getString("EditKeyDialog.title"));
		this.key=key;
		if (mod==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null project");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("EditKeyDialog.ExceptionTitle"), null);
			throw myException;
			
		}
		if (table==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null table");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("EditKeyDialog.ExceptionTitle"), null);
			throw myException;
		}
		
		this.table=table;
		this.mod=mod;
		maxColumnnumber=0;
	}

	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
 	        // #changed# by Konstantin Mishin on 08.02.2006 fixed for ESORM-483
//			root.setBounds(0,0,380,450);
 	        RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
 	        rowLayout.marginBottom = rowLayout.marginTop = rowLayout.marginRight = rowLayout.marginLeft= 10;
			root.setLayout(rowLayout);

 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText(BUNDLE.getString("EditKeyDialog.tablelist"));
//			label1.setBounds(20,20,210,15);			
			
			TableList = new Combo(root, SWT.NONE);
//			if (table==null)
//			{
				for(int i=0;i<mod.getDatabaseTables().length;i++)
					TableList.add(mod.getDatabaseTables()[i].getName());
//			}
//			else
//				{
//					TableList.add(table.getName());
//					TableList.setEnabled(false);
//				}
			
 			if (TableList.getItemCount()>0)
				TableList.setText(TableList.getItem(0));
//			TableList.setBounds(20,40,150,20);
//			TableList.addModifyListener(new ModifyListener()
// 					{
//
//						public void modifyText(ModifyEvent e) {
//							modifyTableList();
//							}}
// 			); 			
			
			
		    Composite composite1 = new Composite(root, SWT.NULL);
		    GridData gridData = new GridData(100,100);
		    composite1.setLayout(new GridLayout(3,false));
		    
 			Label label2 = new Label(composite1, SWT.NULL);
 			label2.setText(BUNDLE.getString("EditKeyDialog.columnname"));
//			label2.setBounds(20,70,150,15);

 			new Label(composite1, SWT.NULL);
 			
 			Label label3 = new Label(composite1, SWT.NULL);
 			label3.setText(BUNDLE.getString("EditKeyDialog.keyname"));
//			label3.setBounds(250,70,140,15);
			
			ColumnList = new List(composite1,SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
			ColumnList.setBackground(new Color(null,255,255,255));
//			ColumnList.setBounds(20,90,110,120);
			ColumnList.setLayoutData(gridData);			
			ColumnList.addSelectionListener(new SelectionListener()
					{
				
				public void widgetSelected(SelectionEvent e) {
					AddColumnButton.setEnabled(true);
					
				}
				public void widgetDefaultSelected(SelectionEvent e) {

					
				}				
					});
			
		    Composite composite2 = new Composite(composite1, SWT.NULL);
		    FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		    fillLayout.spacing = 10;
		    composite2.setLayout(fillLayout);

			KeyList = new List(composite1,SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
			KeyList.setBackground(new Color(null,255,255,255));
//			KeyList.setBounds(250,90,110,120);			
			KeyList.setLayoutData(gridData);			
			KeyList.addSelectionListener(new SelectionListener()
					{
				
				public void widgetSelected(SelectionEvent e) {
					RemoveColumnButton.setEnabled(true);
					
				}
				public void widgetDefaultSelected(SelectionEvent e) {

					
				}				
					});
			
 	        AddColumnButton= new Button(composite2, SWT.PUSH);
//			AddColumnButton.setBounds(140,110,100,23);
			AddColumnButton.setEnabled(false);			
			AddColumnButton.setText(BUNDLE.getString("EditKeyDialog.columnadd"));
			AddColumnButton.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					AddColumn();
 				}
 			});
 			
 	        RemoveColumnButton= new Button(composite2, SWT.PUSH);
//			RemoveColumnButton.setBounds(140,150,100,23);			
			RemoveColumnButton.setText(BUNDLE.getString("EditKeyDialog.columnremove"));
			RemoveColumnButton.setEnabled(false);
			RemoveColumnButton.addSelectionListener(new SelectionAdapter() {
 				public void widgetSelected(SelectionEvent e) {
 					RemoveColumn();
 				}
 			});

			// #changed# by Konstantin Mishin on 21.09.2005 fixed for ESORM-40
			Composite composite = new Composite(root, SWT.NONE);
			GridLayout layout = new GridLayout(1,false);
			composite.setLayout(layout);
			composite.setLayoutData(new RowData(350,170));
			page=new PropertySheetPageWithDescription();
 			page.createControl(composite);
 			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
			data.grabExcessHorizontalSpace=true;
			data.grabExcessVerticalSpace=true;
			page.getControl().setLayoutData(data);
			// #changed#
			
// 		    GridData data = new GridData(SWT.FILL, SWT.FILL, true,true, 2, 1);//new GridData(GridData.HORIZONTAL_ALIGN_FILL|GridData.VERTICAL_ALIGN_FILL);
// 			data.widthHint = 570;
// 			data.heightHint=300;		    
// 			page.getControl().setSize(600,300);
// 			page.getControl().setLayoutData(data);
//// 			page.getControl().getSize();
 			BeanPropertySourceBase bp = new BeanPropertySourceBase(key);
 			bp.setPropertyDescriptors(KeyPropertyDescriptorsHolder.getInstance());		
 			page.selectionChanged(null, new StructuredSelection(bp));
// 			composite.setBounds(20,220,350,170);			
// 			Label label4 = new Label(root, SWT.NULL);
// 			label4.setText(BUNDLE.getString("EditKeyDialog.paramname"));
//			label4.setBounds(20,220,120,15);			
//
// 			
// 			Label label5 = new Label(root, SWT.NULL);
// 			label5.setText(BUNDLE.getString("EditKeyDialog.paramvalue"));
//			label5.setBounds(170,220,120,15);			
//
//			ParamCombo = new Combo(root, SWT.BORDER | SWT.READ_ONLY);
//			ParamCombo.setBounds(20,240,120,20);
//			ParamCombo.setItems(OrmConfiguration.KEY_PARAMS_NAME);
//			ParamCombo.setText(ParamCombo.getItem(0));
//			ParamCombo.addModifyListener(new ModifyListener()
// 					{
//						
//
//						public void modifyText(ModifyEvent e) {
//							modifyParamCombo();
//						}
//						});
//			
// 			ParamValueText = new Text(root, SWT.BORDER | SWT.SINGLE);
//			ParamValueText.setBounds(170,240,120,20);				
//			ParamValueCombo = new Combo(root, SWT.BORDER | SWT.READ_ONLY);
//			ParamValueCombo.setBounds(170,240,120,20);
//			
// 			Label label6 = new Label(root, SWT.NULL);
// 			label6.setText(BUNDLE.getString("EditKeyDialog.paramlist"));
//			label6.setBounds(20,270,280,15);			
// 			
// 			ParamList = new List(root,SWT.BORDER|SWT.V_SCROLL);
// 			ParamList.setBackground(new Color(null,255,255,255));
//			ParamList.setBounds(20,290,250,120);			
//			ParamList.addSelectionListener(new SelectionListener()
//					{
//				
//				public void widgetSelected(SelectionEvent e) {
//					RemoveParamButton.setEnabled(true);
//					
//				}
//				public void widgetDefaultSelected(SelectionEvent e) {
//
//					
//				}				
//					});
//
// 	        AddParamButton= new Button(root, SWT.PUSH);
//			AddParamButton.setBounds(280,310,80,23);			
// 	        AddParamButton.setText(BUNDLE.getString("EditKeyDialog.paramadd"));
// 	        AddParamButton.addSelectionListener(new SelectionAdapter() {
// 				public void widgetSelected(SelectionEvent e) {
// 					AddParam();
// 				}
// 			});
// 			
// 	        RemoveParamButton= new Button(root, SWT.PUSH);
//			RemoveParamButton.setBounds(280,350,80,23);			
// 	        RemoveParamButton.setText(BUNDLE.getString("EditKeyDialog.paramremove"));
// 	        RemoveParamButton.setEnabled(false); 	        
// 	        RemoveParamButton.addSelectionListener(new SelectionAdapter() {
// 				public void widgetSelected(SelectionEvent e) {
// 					RemoveParam();
// 				}
// 			});

// 			Label label7 = new Label(root, SWT.NULL);
// 			label7.setText("");
//			label7.setBounds(20,390,170,15);
 	        // #changed#
			
//			modifyTableList();
			ColumnList.removeAll();
			KeyList.removeAll();
			Iterator iter = (table.getColumnIterator());
 			Column curcol=null;
 			while ( iter.hasNext() ) {
 				curcol=(Column)iter.next();
				ColumnList.add(curcol.getName());
 			}
			
//			modifyParamCombo();			
			
 	      return root;
 	    }
	    
//	    private void RemoveParam() {
//				if (ParamList.getSelectionIndex()>=0)
//				{
//					ParamList.remove(ParamList.getSelectionIndices());
//		 	        RemoveParamButton.setEnabled(false);
//				}
//			}

//		private void modifyTableList() {
//			ColumnList.removeAll();
//			KeyList.removeAll();
//			if (TableList.getItemCount()>0)
//			{
//			Iterator iter = (mod.findTable(TableList.getText()).getColumnIterator());
// 			Column curcol=null;
// 			while ( iter.hasNext() ) {
// 				curcol=(Column)iter.next();
//				ColumnList.add(curcol.getName());
// 			}
//			}
//			}
		
//		private void modifyParamCombo() {
//			if (OrmConfiguration.KEY_PARAMS_VALUES[ParamCombo.indexOf(ParamCombo.getText())].length==0)
//			{
//				ParamValueText.setText("");
//				ParamValueText.setVisible(true);
//				ParamValueCombo.setVisible(false);
//			}
//			else
//			{
//			ParamValueText.setVisible(false);
//			ParamValueCombo.setVisible(true);								
//			ParamValueCombo.setItems(OrmConfiguration.KEY_PARAMS_VALUES[ParamCombo.indexOf(ParamCombo.getText())]);
//			if (ParamValueCombo.getItemCount()>0) ParamValueCombo.setText(ParamValueCombo.getItem(0));
//			}
//		}
		
		
//		private void AddParam() {
//			String ParValue=null;
//			if (ParamValueCombo.getVisible())
//			{
//				if (ParamCombo.getItemCount()>0) ParValue=ParamValueCombo.getText();
//			}
//			else ParValue=ParamValueText.getText();
//			
//			if (ParValue!=null)
//			{
// 			ParamList.add(ParamCombo.getText()+"="+ParValue);//process add param
//			ParamValueText.setText("");
//			}
//		}

		private void RemoveColumn() {
			for(int i=0;i<KeyList.getSelectionCount();i++)
				ColumnList.add(KeyList.getSelection()[i]);			
			KeyList.remove(KeyList.getSelectionIndices());
			KeyList.setFocus();
			if (KeyList.getItemCount()>0)
			{
				KeyList.setSelection(0);
			}
			else RemoveColumnButton.setEnabled(false);
		}

		private void AddColumn() {
			if ((maxColumnnumber>0) && ((KeyList.getItemCount()+ColumnList.getSelectionCount())>maxColumnnumber))
				MessageDialog.openWarning(getShell(),BUNDLE.getString("EditKeyDialog.fullkeytitle"), BUNDLE.getString("EditKeyDialog.fullkeydescr")+maxColumnnumber);
				//openConfirm(getShell(), BUNDLE.getString("FieldMappingWizardPage4.deldlgtitle"), BUNDLE.getString("FieldMappingWizardPage4.deldlgdescr")))
			else
			{
			for(int i=0;i<ColumnList.getSelectionCount();i++)
				KeyList.add(ColumnList.getSelection()[i]);			
			ColumnList.remove(ColumnList.getSelectionIndices());
			ColumnList.setFocus();
			if (ColumnList.getItemCount()>0)
			{
				ColumnList.setSelection(0);	
			}
			else AddColumnButton.setEnabled(false);
			}
		}

		protected void okPressed() {
			table=mod.findTable(StringUtils.hibernateEscapeName(TableList.getText()));//added 7/29
			columns=KeyList.getItems();
//			keyParams=ParamList.getItems();
 			setReturnCode(OK);
 			close();
 	    	}
	    
	    public void setTitle(String title) {
			this.title = title;
		}
	    
	    protected void configureShell(Shell shell) {
			super.configureShell(shell);
			if (title != null)
				shell.setText(title);
		}

		/**
		 * @return Returns the columns.
		 */
		public String[] getColumns() {
			return columns;
		}

		/**
		 * @param columns The columns to set.
		 */
		public void setColumns(String[] columns) {
			this.columns = columns;
			if (table!=null)
			KeyList.setItems(columns);
		}

		/**
		 * @return Returns the keyParams.
		 */
//		public String[] getKeyParams() {
//			return keyParams;
//		}

		/**
		 * @param keyParams The keyParams to set.
		 */
//		public void setKeyParams(String[] keyParams) {
//			this.keyParams = keyParams;
//		}

		/**
		 * @return Returns the table.
		 */
		public IDatabaseTable getTable() {
			return table;
		}

		/**
		 * @param maxColumnnumber The maxColumnnumber to set.
		 */
		public void setMaxColumnnumber(int maxColumnnumber) {
			this.maxColumnnumber = maxColumnnumber;
		}

}
