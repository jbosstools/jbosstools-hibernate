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
package org.jboss.tools.hibernate.internal.core.properties;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ResourceBundle;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.internal.core.data.Column;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Sep 25, 2005
 */
public class DialogDBColumnCellEditor extends DialogCellEditor {
	public static final String BUNDLE_NAME = "properties"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(DialogDBColumnCellEditor.class.getPackage().getName() + "." + BUNDLE_NAME);
	protected IDatabaseTable db;
	
	public DialogDBColumnCellEditor(Composite parent, IDatabaseTable db) {
		super(parent);
		this.db=db;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
	 */
	protected Object openDialogBox(Control cellEditorWindow) {
        Dialog dialog = new Dialog(cellEditorWindow.getShell())
        {
    	    private List ColumnList;
			private List KeyList;
			private Button AddColumnButton;
			private Button RemoveColumnButton;
			private Button NewColumnButton;

			protected void configureShell(Shell shell) {
    			super.configureShell(shell);
   				shell.setText(BUNDLE.getString("DialogDBColumnCellEditor.Title"));
    		}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
			 */
			protected void okPressed() {
				String res=null;
				for(int i=0;i<KeyList.getItemCount();i++)
					if (res!=null) 
						res=res+","+KeyList.getItem(i).split(":")[0];
					else res=KeyList.getItem(i).split(":")[0];
				doSetValue(res);
				super.okPressed();
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
			 */
			protected Control createDialogArea(Composite parent) {
			    Composite root = new Composite(parent, SWT.NULL);
			    // #changed# by Konstantin Mishin on 13.01.2006 fixed for ESORM-446
//			    root.setBounds(0,0,500,375);
			    GridData gridData = new GridData(150,100);
			    root.setLayout(new GridLayout(3,false));

//	 	        Composite root = new Composite(parent, SWT.NULL);
//				root.setBounds(0,0,380,450);

	 			Label label2 = new Label(root, SWT.NULL);
	 			label2.setText(BUNDLE.getString("DialogDBColumnCellEditor.columnname"));
//				label2.setBounds(10,20,280,15);
				
	 			new Label(root, SWT.NULL);

	 			Label label3 = new Label(root, SWT.NULL);
	 			label3.setText(BUNDLE.getString("DialogDBColumnCellEditor.keyname"));
//				label3.setBounds(310,20,140,15);
				
				ColumnList = new List(root,SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
				ColumnList.setBackground(new Color(null,255,255,255));
//				ColumnList.setBounds(10,40,180,120);
				ColumnList.setLayoutData(gridData);
				ColumnList.addSelectionListener(new SelectionListener()
						{
					
					public void widgetSelected(SelectionEvent e) {
						AddColumnButton.setEnabled(true);
						
					}
					public void widgetDefaultSelected(SelectionEvent e) {

						
					}				
						});
				

			    Composite composite = new Composite(root, SWT.NULL);
			    FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
			    fillLayout.spacing = 5;
			    composite.setLayout(fillLayout);
//			    composite.setLayoutData(gridData);	
			    
				KeyList = new List(root,SWT.BORDER|SWT.V_SCROLL|SWT.MULTI);
				KeyList.setBackground(new Color(null,255,255,255));
//				KeyList.setBounds(310,40,180,120);			
				KeyList.setLayoutData(gridData);			
				KeyList.addSelectionListener(new SelectionListener()
						{
					
					public void widgetSelected(SelectionEvent e) {
						RemoveColumnButton.setEnabled(true);
						
					}
					public void widgetDefaultSelected(SelectionEvent e) {

						
					}				
						});
				
	 	        AddColumnButton= new Button(composite, SWT.PUSH);
//				AddColumnButton.setBounds(200,40,100,23);
				AddColumnButton.setEnabled(false);			
				AddColumnButton.setText(BUNDLE.getString("DialogDBColumnCellEditor.columnadd"));
				AddColumnButton.pack();
				AddColumnButton.addSelectionListener(new SelectionAdapter() {
	 				public void widgetSelected(SelectionEvent e) {
	 					AddColumn();
	 				}
	 			});
	 			
	 	        RemoveColumnButton= new Button(composite, SWT.PUSH);
//				RemoveColumnButton.setBounds(200,80,100,23);		
				RemoveColumnButton.setText(BUNDLE.getString("DialogDBColumnCellEditor.columnremove"));
				RemoveColumnButton.setEnabled(false);
	 	        RemoveColumnButton.pack();
				RemoveColumnButton.addSelectionListener(new SelectionAdapter() {
	 				public void widgetSelected(SelectionEvent e) {
	 					RemoveColumn();
	 				}
	 			});

	 	        NewColumnButton= new Button(composite, SWT.PUSH);
//	 	        NewColumnButton.setBounds(200,120,100,23);		
	 	        NewColumnButton.setText(BUNDLE.getString("DialogDBColumnCellEditor.columnnew"));
	 	        NewColumnButton.pack();
	 	        NewColumnButton.addSelectionListener(new SelectionAdapter() {
	 				public void widgetSelected(SelectionEvent e) {
	 					NewColumn();
	 				}

	 			});
				
//	 			Label label1 = new Label(root, SWT.NULL);
//	 			label1.setText("");
//				label1.setBounds(20,180,480,15);
			    // #changed#

				ColumnList.removeAll();
				KeyList.removeAll();
				if (db!=null)
				{
				Iterator iter = (db.getColumnIterator());
	 			Column curcol=null;
	 			while ( iter.hasNext() ) {
	 				curcol=(Column)iter.next();
					ColumnList.add(curcol.getName()+":"+curcol.getSqlTypeName());
	 			}
				}
	 			if (doGetValue()!=null)
	 			{
		 			String[] keys=((String) doGetValue()).split(",");
		 			for (int i=0;i<keys.length;i++)
		 			{
		 				Column curcol=(Column) db.getColumn(keys[i]);	
			 			if ((curcol!=null)&&(ColumnList.indexOf(curcol.getName()+":"+curcol.getSqlTypeName())>=0))
			 			{
			 				KeyList.add(curcol.getName()+":"+curcol.getSqlTypeName());
			 				ColumnList.remove(curcol.getName()+":"+curcol.getSqlTypeName());
			 			}
		 			}
	 			}
	 			sortLists();
				return root;
			}
			
			private void NewColumn() {
				Dialog dlg=new AddColumnDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),db);
			 	dlg.open();
			 	if (dlg.getReturnCode()==Window.OK)
			 	{
			 		ColumnList.add(((AddColumnDialog)dlg).getColumnName()+":"+db.getColumn(((AddColumnDialog)dlg).getColumnName()).getSqlTypeName());
			 	}
			}
			
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
				sortLists();
			}

			private void AddColumn() {
				for(int i=0;i<ColumnList.getSelectionCount();i++)
					KeyList.add(ColumnList.getSelection()[i]);			
				ColumnList.remove(ColumnList.getSelectionIndices());
				ColumnList.setFocus();
				if (ColumnList.getItemCount()>0)
				{
					ColumnList.setSelection(0);	
				}
				else AddColumnButton.setEnabled(false);
				sortLists();
			}
			
			private void sortLists() {
				ColumnList.setRedraw(false);
				String [] values=ColumnList.getItems();
				Arrays.sort(values);
				ColumnList.removeAll();
				ColumnList.setItems(values);
				ColumnList.setRedraw(true);				
			}			
			
			
			
        };
        if (dialog.open()==Window.OK)
        	return getValue();
        else return null;
	}
}
