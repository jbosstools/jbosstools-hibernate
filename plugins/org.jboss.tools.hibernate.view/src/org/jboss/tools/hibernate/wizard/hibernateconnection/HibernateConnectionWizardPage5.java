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
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateConfiguration;

/**
 * @author sushko
 * Hibernate Connection Wizard page5(use connection pool)
 */
public class HibernateConnectionWizardPage5 extends WizardPage {
	
	public static final String BUNDLE_NAME = "hibernateconnection"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConnectionWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	private IMapping mapping;
	private PropertySheetPage page;
	private IHibernateConfiguration ihibConfiguration;
	
/** createControl for the  Page5
 * @param parent
* */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
	    Group groupe = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		groupe.setLayout(Grlayout);
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	    groupe.setLayoutData(groupData);
//		
//	    UsePoolButton = new Button(groupe, SWT.CHECK);
//	    UsePoolButton.setText(BUNDLE.getString("HibernateConnectionWizardPage5.UsePoolButton"));
//	    UsePoolButton.setLayoutData(buttonData);
//	    UsePoolButton.setSelection(true);
//	
	    GridData lData =   new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	    page=new PropertySheetPage();
	    
		page.createControl(groupe);
		//akuzmin 29.07.2005
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        Menu menu = menuMgr.createContextMenu(page.getControl());
		page.getControl().setMenu(menu);
		//
		
		page.getControl().setLayoutData(lData);
		page.getControl().setSize(650,480);
	    ihibConfiguration=(IHibernateConfiguration) mapping.getConfiguration();
		if(ihibConfiguration!=null)
		{
			page.selectionChanged(null, new StructuredSelection(ihibConfiguration.getPoolConfiguration()));
		}
		setControl(groupe);	
		setPageComplete(true);
	}
	
	/** constructor of the HibernateConnectionWizardPage5
	 * @param ormmodel
	 **/
	public HibernateConnectionWizardPage5 (IMapping mapping) {
		super("wizardPage5");
		setTitle(BUNDLE.getString("HibernateConnectionWizardPage5.Title"));
		setDescription(BUNDLE.getString("HibernateConnectionWizardPage5.Description"));
		this.mapping = mapping;
	}
	
public boolean canFlipToNextPage() {
	return false;
		
	}
public void setPageComplete(boolean complete) {
	super.setPageComplete(complete);
}
public void setNewProperty(){
	if(ihibConfiguration!=null)
	{
		page.selectionChanged(null, new StructuredSelection(ihibConfiguration.getPoolConfiguration()));
	}
	
}
}
