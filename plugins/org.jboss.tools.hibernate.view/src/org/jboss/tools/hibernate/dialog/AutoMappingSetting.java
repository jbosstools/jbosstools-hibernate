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


import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmProject;
import org.jboss.tools.hibernate.internal.core.properties.BeanPropertySourceBase;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author Gavrs
 * edit tau 21.12.2005
 *
 */
public class AutoMappingSetting extends Dialog{
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(AutoMappingSetting.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IOrmProject ormProject;
	private String title;
	private IOrmConfiguration ormConfiguration;
	
	public IPropertyDescriptor[] propertyDescriptors;
	private Button RemoveButton;
	IPropertyDescriptor[] i;
	BeanPropertySourceBase bp;
	String ss;
	Object nn;
	private PropertySheetPage page;
	
    public AutoMappingSetting(Shell parent) {
		super(parent);
		this.setTitle(BUNDLE.getString("AutoMappingSetting.Title.0"));
	}
    public AutoMappingSetting(Shell parent,IOrmProject project) {
		super(parent);
		this.setTitle(BUNDLE.getString("AutoMappingSetting.Title.0"));
		ormProject=project;
	}
 
    protected Control createDialogArea(Composite parent) {
		
        Composite root = new Composite(parent, SWT.NULL);
        
		GridLayout layout = new GridLayout();
		root.setLayout(layout);

		//GridData gridDataListAutoMap= new GridData(GridData.FILL_BOTH);
		GridData gridDataListAutoMap= new GridData(SWT.FILL,SWT.FILL,true,true);
		gridDataListAutoMap.widthHint = 570;
		gridDataListAutoMap.heightHint=300;
		page=new PropertySheetPageWithDescription();
		page.createControl(root);
		page.getControl().setSize(570,1100);
		page.getControl().setLayoutData(gridDataListAutoMap);
		ormConfiguration = (OrmConfiguration) ormProject.getOrmConfiguration();
	
		 
		if(ormConfiguration!=null) {
			page.selectionChanged(null, new StructuredSelection(ormConfiguration));
		}
		
		page.getControl().getMenu().setVisible(false);
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
    
    protected void cancelPressed() {
    	
    	try {
    		ormConfiguration.reload();
    	} catch(Exception  e) {
			ExceptionHandler.handle(e, getShell(), null, null);
   		}
    	super.cancelPressed();
	}
    
    protected void okPressed() {
    	try {
    		ormConfiguration.save();
    	} catch(Exception  e) {
			ExceptionHandler.handle(e, getShell(), null, null);    		
    	}
		setReturnCode(OK);
		close();
	}

	protected void createButtonsForButtonBar(Composite parent) {
		//parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		 String OS = System.getProperty("os.name").toLowerCase();
		GridLayout layout = (GridLayout) parent.getLayout();
		layout.numColumns++;
        RemoveButton = new Button(parent, SWT.PUSH);
        RemoveButton.setText(BUNDLE.getString("AutoMappingSetting.defaultbutton"));
        RemoveButton.setFont(JFaceResources.getDialogFont());
		RemoveButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDefaultProperties();
			}
		});
		GridData data = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING);//GridData.HORIZONTAL_ALIGN_FILL |
		RemoveButton.setLayoutData(data);
		layout.numColumns++;
	    Label label3 = new Label(parent, SWT.NULL);
	    if (OS.indexOf("linux") > -1)
		 label3.setText("                                                              ");
	   else
		  label3.setText("                                                                                                          ");
		data = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING);//GridData.HORIZONTAL_ALIGN_FILL |
		label3.setLayoutData(data);		
		layout.numColumns++;
		layout.makeColumnsEqualWidth = false;
		super.createButtonsForButtonBar(parent);
	}
	
	// edit tau 21.12.2005
    private void setDefaultProperties() {
    	ormConfiguration = ((OrmProject) ormProject).newConfigiration();
    	
    	// edit tau 22.12.2005
		//ormConfiguration.getProperties().clear();
		Properties properties = ormConfiguration.getProperties();
		ViewPlugin.loadPreferenceStoreProperties(properties, ViewPlugin.autoMappingSettingPrefPage);
    	
		page.selectionChanged(null, new StructuredSelection(ormConfiguration));
    	
		// del tau 21.12.2005		
    	/*
		IFile resource=(IFile)iormConfiguration.getResource();
		if((resource!=null)&&(resource.exists()))
		{
			try {
				resource.delete(true, null);
			} catch (CoreException e) {
				ExceptionHandler.handle(e, getShell(), null, null);
			}
		}
		    ((OrmProject)ormModel).newConfigiration();		
			iormConfiguration=ormModel.getConfiguration();
			if(iormConfiguration!=null)
				page.selectionChanged(null, new StructuredSelection(iormConfiguration));
		*/			
    }

}
