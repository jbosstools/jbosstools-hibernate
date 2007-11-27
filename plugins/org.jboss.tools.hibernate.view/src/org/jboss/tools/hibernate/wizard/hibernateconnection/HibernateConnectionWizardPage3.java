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

import java.util.ResourceBundle;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateConfiguration;



/**
 * @author sushko
 *Hibernate Connection Wizard page3(managed environment)
 */


public class HibernateConnectionWizardPage3 extends WizardPage {
	public static final String BUNDLE_NAME = "hibernateconnection"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConnectionWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mapping;
	private PropertySheetPage  page;
	
/** createControl for the  Page3
 * @param parent
**/
	public void createControl(Composite parent) {
		
        Composite root = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		root.setLayout(layout);
		Group gr = new Group(root, SWT.NONE);
		GridLayout grlayout = new GridLayout();
		gr.setLayout(grlayout);
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	    gr.setLayoutData(groupData);
		page=new PropertySheetPage();
		
		page.createControl(gr);
		//akuzmin 29.07.2005
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        Menu menu = menuMgr.createContextMenu(page.getControl());
		page.getControl().setMenu(menu);
		//
		
		page.getControl().setSize(650,200);
		// #added# by Konstantin Mishin on 27.12.2005 fixed for ESORM-404
		((Tree)page.getControl()).getColumn(0).setWidth(300);
		((Tree)page.getControl()).getColumn(1).setWidth(345);
		// #added#	    
		GridData gd = new GridData(GridData.FILL_BOTH);
		page.getControl().setLayoutData(gd);
		IHibernateConfiguration ihibConfiguration=(IHibernateConfiguration) mapping.getConfiguration();
		if(ihibConfiguration!=null){
			StructuredSelection ss =new StructuredSelection(ihibConfiguration.getManagedConfiguration());
			page.selectionChanged(null, ss);

		}	
		setControl(root);
		setPageComplete(true);
	}

	
	/** setPageComplete for the  Page3
	 * @param complete
	* */
	public void setPageComplete(boolean complete) {
		super.setPageComplete(complete);
	}
	
	/** setErrorMessage for the  Page3
	 * @param newMessage
	* */
	
	public void setErrorMessage(String newMessage) {
		super.setErrorMessage(newMessage);
	}
	
	/** constructor of the canFlipToNextPage
	 **/
	public boolean canFlipToNextPage() {
		return false;
	}
	
	/** constructor of the HibernateConnectionWizardPage3
	 * @param ormmodel
	 **/
	public HibernateConnectionWizardPage3 (IMapping mapping) {
		super("wizardPage3");
		setTitle(BUNDLE.getString("HibernateConnectionWizardPage3.Title"));
		setDescription(BUNDLE.getString("HibernateConnectionWizardPage3.Description"));
		this.mapping = mapping;
	}
	 public PropertySheetPage getPrefPage(){
		 return page;
	 }


}
