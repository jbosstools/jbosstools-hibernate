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
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingProperties;
import org.jboss.tools.hibernate.internal.core.properties.PropertySheetPageWithDescription;
import org.jboss.tools.hibernate.view.ViewPlugin;

/**
 * @author Gavrs
 *
 * 
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RunTimeSetting extends Dialog{
	//private IJavaLoggingProperties jLogProp;
	//private ILog4JProperties log4JProp;
	private IMappingProperties mapProperty;

	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(AutoMappingSetting.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IMapping mapping;
	private String title;
    public RunTimeSetting(Shell parent) {
		super(parent);
		this.setTitle(BUNDLE.getString("RunTimeSetting.Title.0"));		
	}
    
    public RunTimeSetting(Shell parent,IMapping mapping) {
		super(parent);
		this.setTitle(BUNDLE.getString("RunTimeSetting.Title.0"));
		this.mapping=mapping;
	}
    protected Control createDialogArea(Composite parent) {
        Composite root = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		root.setLayout(layout);

		GridData gridDataListAutoMap= new GridData(GridData.FILL_BOTH);
		gridDataListAutoMap.widthHint = 570;
		gridDataListAutoMap.heightHint=300;
		
		PropertySheetPage page=new PropertySheetPageWithDescription();
		page.createControl(root);
		page.getControl().setSize(570,1100);
		page.getControl().setLayoutData(gridDataListAutoMap);
    	
	    mapProperty=mapping.getProperties();
	    // #added# by Konstantin Mishin on 28.11.2005 fixed for ESORM-351
	    try {
		    mapProperty.reload();
		} catch (Exception e) {
			ViewPlugin.getPluginLog().logError(e);
		}
		// #added#
		if(mapProperty!=null)
			page.selectionChanged(null, new StructuredSelection(mapProperty));
//		hiberPropTab.setControl(hibPropComposite);
		
//--------------------------------------------------------------------------------------

//		TabItem javaLogTab = new TabItem(tabbedComposite,SWT.NULL);//2
//		javaLogTab.setText("Java Logging");
//		Composite JavaLogComposite = new Composite(tabbedComposite, SWT.NULL);
//		GridLayout layoutJavaLog = new GridLayout();
//		JavaLogComposite.setLayout(layoutJavaLog);
//		PropertySheetPage pageJlog=new PropertySheetPage();
//		pageJlog.createControl(JavaLogComposite);
//		GridData gridDataPageJavaLog= new GridData(GridData.FILL_BOTH);
//		pageJlog.getControl().setLayoutData(gridDataPageJavaLog);
//		jLogProp=mapping.getProject().getJavaLoggingProperties();
//		if(jLogProp!=null)
//			pageJlog.selectionChanged(null, new StructuredSelection(jLogProp));
//		javaLogTab.setControl(JavaLogComposite);	
//
//		
//		TabItem log4jTab = new TabItem(tabbedComposite,SWT.NULL);//3
//		log4jTab.setText("log4J properties");
//		Composite log4jComposite = new Composite(tabbedComposite, SWT.NULL);
//		GridLayout layoutlog4J = new GridLayout();
//		log4jComposite.setLayout(layoutlog4J);
//		PropertySheetPage pagelog4j=new PropertySheetPage();
//		pagelog4j.createControl(log4jComposite);
//		GridData gridDataPageLo4J= new GridData(GridData.FILL_BOTH);
//		pagelog4j.getControl().setLayoutData(gridDataPageLo4J);
//		log4JProp=mapping.getProject().getLog4jProperties();
//		//  write PropertyDescriptorsHolder for log4J 
//		if(log4JProp!=null)
//		//	pagelog4j.selectionChanged(null, new StructuredSelection(log4JProp));
//		log4jTab.setControl(log4jComposite);	

			
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
		super.cancelPressed();
	}
    protected void okPressed() {
    	try{
    		//jLogProp.save();
    		//log4JProp.save();
    		mapProperty.save();
        	}catch(Exception  e) {
    			ViewPlugin.getPluginLog().logError(e);
        	}	
	super.okPressed();
    }
   

}

