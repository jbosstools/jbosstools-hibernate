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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.ColumnPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;
import org.jboss.tools.hibernate.view.ViewPlugin;
import org.jboss.tools.hibernate.view.views.ViewsUtils;


public class ColumnPropertyDialog extends Dialog{
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(ColumnPropertyDialog.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IMapping mod;
	private String title;
	private Column column;
	private BeanPropertySourceBase bp;
	private PropertySheetPage page;
	private TreeViewer viewer;	

	public ColumnPropertyDialog(Shell parent,IMapping mod,Column column, TreeViewer viewer) {
		super(parent);
		this.setTitle(BUNDLE.getString("ColumnPropertyDialog.Title"));

		if (mod == null) {
			RuntimeException myException = new UnsupportedOperationException("Cant work with null project");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("ColumnPropertyDialog.ExceptionTitle"), null);
			throw myException;
			
		}
		if (column==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null column");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("ColumnPropertyDialog.ExceptionTitle"), null);
			throw myException;
			
		}
		
		
		this.mod=mod;
		this.column=column;
		this.viewer = viewer; // add tau 16.02.2006
	}
 
    protected Control createDialogArea(Composite parent) {
        Composite root = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		root.setLayout(layout);

		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint = 570;
		data.heightHint=300;
		
		page=new PropertySheetPageWithDescription();
		page.createControl(root);
		page.getControl().setLayoutData(data);
		bp = new BeanPropertySourceBase(column);
		bp.setPropertyDescriptors(ColumnPropertyDescriptorsHolder.getInstance(column.getSqlTypeName()));		
		FormList();
		page.getControl().addMouseListener(
				new MouseAdapter(){

			public void mouseDown(MouseEvent e) {
				if (bp.isRefresh())
				{
					FormList();
					bp.setRefresh(false);
				}
			}

	});
		
      return root;
    }
    
    protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null)
			shell.setText(title);
	}
    public void setTitle(String title) {
		this.title = title;
	}
	
     protected void okPressed() {
			try {
				//edit tau 06.04.2005
				//mod.save();
				mod.saveMappingStorageForPersistentClassMapping(column.getOwnerTable().getPersistentClassMappings());				
			} catch (IOException e) {
				ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,	null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
			} 
		setReturnCode(OK);
		close();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	protected void cancelPressed() {
		try {
			if (bp.isDirty())
			{
				if ((column.getPersistentValueMapping()!=null)&&(column.getPersistentValueMapping().getFieldMapping()!=null)&&
						(column.getPersistentValueMapping().getFieldMapping().getPersistentField()!=null)&&
						(column.getPersistentValueMapping().getFieldMapping().getPersistentField().getOwnerClass()!=null)&&
						(column.getPersistentValueMapping().getFieldMapping().getPersistentField().getOwnerClass().getPersistentClassMapping()!=null)&&
						(column.getPersistentValueMapping().getFieldMapping().getPersistentField().getOwnerClass().getPersistentClassMapping().getStorage()!=null))
					{
						column.getPersistentValueMapping().getFieldMapping().getPersistentField().getOwnerClass().getPersistentClassMapping().getStorage().reload();
					}
				else 
				{
					mod.reload(true); // edit tau 17.11.2005
				}
				mod.refresh(false, true);  // edit tau 17.11.2005			
			}
		} catch (IOException e) {
			ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,	null);
		} catch (CoreException e) {
			ExceptionHandler.handle(e, getShell(), null, null);			
		} catch (Throwable e) { 
			ExceptionHandler.handle(new InvocationTargetException(e), getShell(), null,	null);			
			//ExceptionHandler.logThrowableWarning(e,null); // tau 15.09.2005
		}

		super.cancelPressed();
	}

    protected void FormList() {
		page.selectionChanged(null, new StructuredSelection(bp));
	}

    // add tau 16.02.2006
	public void create() {
		super.create();
		IDatabaseTable table = column.getOwnerTable();
		if (table != null){
			getButton(IDialogConstants.OK_ID).setEnabled(!ViewsUtils.isReadOnlyMappingStoragesForDatabaseTable(table.getPersistentClassMappings()));
		}
	}
	
}
