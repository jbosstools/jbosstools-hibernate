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
package org.jboss.tools.hibernate.wizard.hibernateconnection;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;
import org.jboss.tools.hibernate.internal.core.properties.PropertySourceBase;





public class HibernateConfigurationDialog extends Dialog {
		//IPropertySource2 properties;
	
	public static final String BUNDLE_NAME ="hibernateconnection";
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConfigurationDialog.class.getPackage().getName() + "." + BUNDLE_NAME);	
	    
		IMapping properties;
		String title;
	    PropertySheetPage page;
		private PropertySourceBase base;	    
		   public HibernateConfigurationDialog(Shell parent,IMapping properties) {
			super(parent);
			this.properties=properties;
			this.setTitle(BUNDLE.getString("HibernateConfigurationDialog.Title"));
		}
	    protected Control createDialogArea(Composite parent) {
	    	Composite root = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			root.setLayout(layout);
		
			GridData gridDataListAutoMap= new GridData(GridData.FILL_BOTH);
			gridDataListAutoMap.widthHint = 760;
			gridDataListAutoMap.heightHint=300;
			
		    page=new PropertySheetPageWithDescription();
			
			page.createControl(root);
			page.getControl().setLayoutData(gridDataListAutoMap);
			page.getControl().getShell().forceActive();
			page.getControl().setSize(760,1100);
			base=(PropertySourceBase) properties.getConfiguration();
			// #added# by Konstantin Mishin on 24.12.2005 fixed for ESORM-440
			base.cloneProperties();
			// #added#
			FormList();
			page.getControl().addMouseListener(
						new MouseAdapter(){

					public void mouseDown(MouseEvent e) {
						if (base.isRefresh())
						{
							FormList();
							base.setRefresh(false);
						}
					}
			});
			return root;
	    }
	    
	    protected void FormList() {
			page.selectionChanged(null, new StructuredSelection(base));
		}
	    
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			if (title != null)
				shell.setText(title);
		}
	    public void setTitle(String title) {
			this.title = title;
			
		}
	    protected void cancelPressed() {
	    	// #changed# by Konstantin Mishin on 24.12.2005 fixed for ESORM-440
//	    	try {
	    		// #changed# by Konstantin Mishin on 05.12.2005 fixed for ESORM-413
	    		//properties.getConfiguration().reload();
	    		//properties.reload(true);
	    		// #changed#
//			} catch (IOException e) {
//				ExceptionHandler.logThrowableError(e, e.getMessage());
//			} catch (CoreException e) {
//				ExceptionHandler.logThrowableError(e, e.getMessage());
//			}
    		base.restoreProperties();	    		
    		// #changed#
	        setReturnCode(CANCEL);
	        close();
	    }
	    protected void okPressed(){
        	//TODO (tau-tau) for Exception	    	
	    	try {
	    		// #added# by Konstantin Mishin on 24.12.2005 fixed for ESORM-440
	    		base.resetCloneProperties();
	    		// #added#
	    		properties.getConfiguration().save();
			} catch (IOException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage());
			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e, e.getMessage());
			}
			super.okPressed();
	    }

}
