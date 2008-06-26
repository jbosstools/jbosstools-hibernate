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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.jboss.tools.hibernate.core.IMapping;

/**
 * @author sushko
 * Hibernate Connection Wizard page1(Select Connection Type)
 */
public class HibernateConnectionWizardPage1 extends WizardPage {
	
	
	public static final String BUNDLE_NAME = "hibernateconnection"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(HibernateConnectionWizardPage1.class.getPackage().getName() + "." + BUNDLE_NAME);	
	//private ResourceBundle BUNDLE_IMAGE = ViewPlugin.BUNDLE_IMAGE;
	
	public Button JDBCButton;
	public Button DataSourceButton;
	public Button CustomConnButton;
    private Button HibDefButton;
	private IMapping mapping;
//    private boolean  isExistStartHPFile,isExistEndHPFile;
    
	/** createControl for the First Page
	 * @param parent
	 * 
	 **/
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		
		container.setBounds(0,0,convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_MARGIN));

	    Group group = new Group(container, SWT.NONE);
		GridLayout Grlayout = new GridLayout();
		Grlayout.numColumns = 1;
		group.setLayout(Grlayout);
	    
	    group.setText(BUNDLE.getString("HibernateConnectionWizardPage1.GROUPTEXT"));
	    GridData groupData =   new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
	    group.setLayoutData(groupData);
		
	    JDBCButton = new Button(group, SWT.RADIO);
	    JDBCButton.setText(BUNDLE.getString("HibernateConnectionWizardPage1.JDBCButton"));
	    JDBCButton.setSelection(true);
	    JDBCButton.addListener(SWT.Selection, JDBClistener);
	    
	    DataSourceButton = new Button(group, SWT.RADIO);
	    DataSourceButton.setText(BUNDLE.getString("HibernateConnectionWizardPage1.DataSourceButton"));
		DataSourceButton.addListener(SWT.Selection, DataSourcelistener);
		
	    CustomConnButton= new Button(group, SWT.RADIO);
	    CustomConnButton.setText(BUNDLE.getString("HibernateConnectionWizardPage1.CustomConnButton"));
	    CustomConnButton.addListener(SWT.Selection, CustomConnlistener);
	    
		HibDefButton= new Button(container, SWT.PUSH);
	    HibDefButton.setText(BUNDLE.getString("HibernateConnectionWizardPage1.HibDefButton"));
	    HibDefButton.setToolTipText(BUNDLE.getString("HibernateConnectionWizardPage1.buttonhibernateprophelp"));
		HibDefButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if ((new HibernateConfigurationDialog(getShell(), mapping)).open()==Window.OK) {
					((HibernateConnectionWizard)(getWizard())).getPage2().init();
					((HibernateConnectionWizard)(getWizard())).getPage2().setDialect();
				}
				
			}
		});		
		setControl(container);		
	}

	Listener JDBClistener = new Listener() {
	    public void handleEvent(Event event) {
	        Button button = (Button) event.widget;
	        if (!button.getSelection()) return;
	    }};	
	Listener DataSourcelistener = new Listener() {
	    public void handleEvent(Event event) {
	        Button button = (Button) event.widget;
	        if (!button.getSelection()) return;
	    }};
	Listener CustomConnlistener = new Listener() {
	    public void handleEvent(Event event) {
	        Button button = (Button) event.widget;
	        if (!button.getSelection()) return;
	    }};	
	    
	    

/** constructor of the HibernateConnectionWizardPage1
* @param ormmodel
* 
* */
	public HibernateConnectionWizardPage1 (IMapping mapping) {
		super("wizardPage1");
//		super(ResourceBundle.getBundle(this.getClass().getPackage().getName()+".hibernateconnection").getString("HibernateConnectionWizardPage1.super"));
		setTitle(BUNDLE.getString("HibernateConnectionWizardPage1.Title"));
		setDescription(BUNDLE.getString("HibernateConnectionWizardPage1.Description"));
		this.mapping = mapping;
//		init();
	}
//	public void init(){
//		if(mapping.getProperties().getResource()==null)
//			isExistStartHPFile=false;
//		else isExistStartHPFile=true;
//	}
//public boolean isDeleteHPFile(){
//	if(!isExistStartHPFile  &&  isExistEndHPFile)
//		return true;
//	else return false;
//}
}
