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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author kaa - akuzmin@exadel.com
 * Dialog for rename DatabaseTable(if Column = null) or 
 *	Column 
 */

public class RenameTableObjectDialog extends Dialog{
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(RenameTableObjectDialog.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IDatabaseTable table;
	private Column column;	
	private IMapping mod;
	private Text Newtext;
	private String title,objectname; 
	
	/**
	 * @param parentShell
	 * @param mod - IMapping
	 * @param table - Table 
	 * @param column - Column for rename
	 */

	public RenameTableObjectDialog(Shell parentShell,IMapping mod,IDatabaseTable table,Column column) {
		super(parentShell);
		this.setTitle(BUNDLE.getString("RenameTableDialog.title2"));			
		if (mod==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null project");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("RenameTableDialog.ExceptionTitle"), null);
			throw myException;
			
		}
		if (table==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null table");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("RenameTableDialog.ExceptionTitle"), null);
			throw myException;
			
		}

		if (column==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null column");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("RenameTableDialog.ExceptionTitle"), null);
			throw myException;
			
		}
		
		this.table=table;
		this.mod=mod;
		this.column=column;
		
		objectname=column.getName();
	}

	/**
	 * @param parentShell
	 * @param mod - IMapping
	 * @param table - Table - to rename 
	 * 
	 * 	 */	
	public RenameTableObjectDialog(Shell parentShell,IMapping mod,IDatabaseTable table) {
		super(parentShell);
		this.setTitle(BUNDLE.getString("RenameTableDialog.title1"));
		if (mod==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null project");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("RenameTableDialog.ExceptionTitle"), null);
			throw myException;
			
		}
		if (table==null)
		{
			RuntimeException myException = new UnsupportedOperationException("Cant work with null table");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("RenameTableDialog.ExceptionTitle"), null);
			throw myException;
			
		}
		
		this.table=table;
		this.mod=mod;
		this.column=null;
		
		objectname=table.getName();
	}
	
	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
			root.setBounds(0,0,220,240);
 			
 			Label label2 = new Label(root, SWT.NULL);
			label2.setBounds(20,20,170,15);
			
			Newtext = new Text(root, SWT.BORDER | SWT.SINGLE);
			Newtext.setBounds(20,40,150,20);
			Newtext.setText(objectname);
			Newtext.setSelection(0,objectname.length());
			
 			Label label1 = new Label(root, SWT.NULL);
 			label1.setText("");
			label1.setBounds(20,80,170,15);

			if (column!=null)
			{
	 			label2.setText(BUNDLE.getString("RenameTableDialog.newcolumnnname"));
				
			}
			else {
 			label2.setText(BUNDLE.getString("RenameTableDialog.newtablename"));
			}			
			
 	      return root;
 	    }
	    
	    protected void okPressed() {
			objectname=Newtext.getText().trim();
 	    	if (objectname.length()>0)
 	    	{
				if (column!=null)
					table.renameColumn(column, objectname); 
				else 
					mod.renameTable(table, objectname);
				try {
					// edit tau 06.04.2006
					//mod.save();
					mod.saveMappingStorageForPersistentClassMapping(table.getPersistentClassMappings());					
				} catch (IOException e) {
	            	//TODO (tau-tau) for Exception
					ViewPlugin.getPluginLog().logError(e);
				} catch (CoreException e) {
	            	//TODO (tau-tau) for Exception					
					ViewPlugin.getPluginLog().logError(e);
				} 
			}
 			setReturnCode(OK);
 			close();
 	    	}
	    
	    public void setTitle(String title) {
			this.title = title;
		}
	    /**
	     * @return New renamed object name
	     */
	    public String getTableName() {
			return objectname;
		}
	    
	    protected void configureShell(Shell shell) {
			super.configureShell(shell);
			if (title != null)
				shell.setText(title);
		}

}
