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
package org.jboss.tools.hibernate.preference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author tau edit 21.12.2005
 *
 */
public class AutoMappingSettingPrefPage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private IOrmConfiguration ormConfiguration;
	private PropertySheetPage page=null;

	public AutoMappingSettingPrefPage() {
		super();
		setPreferenceStore(ViewPlugin.getDefault().getPreferenceStore());
	}

	public AutoMappingSettingPrefPage(String title) {
		super(title);
	}

	public AutoMappingSettingPrefPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	protected Control createContents(Composite parent) {
		    Composite root = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			root.setLayout(layout);

			GridData gridDataListAutoMap= new GridData(GridData.FILL_BOTH);
			gridDataListAutoMap.widthHint = 400;
			gridDataListAutoMap.heightHint=300;
			
			page=new PropertySheetPage();//new PropertySheetPageWithDescription();
			page.createControl(root);
			page.getControl().setSize(400,1100);
			page.getControl().setLayoutData(gridDataListAutoMap);
			
			if(ormConfiguration!=null)
				page.selectionChanged(null, new StructuredSelection(ormConfiguration));

		return root;
	}

	public void init(IWorkbench workbench) {
		ormConfiguration = new OrmConfiguration(OrmPropertyDescriptorsHolder.getInstance(null));
		Properties properties = ormConfiguration.getProperties();
		ViewPlugin.loadPreferenceStoreProperties(properties, ViewPlugin.autoMappingSettingPrefPage);
		
		// edit 22.12.2005
		/*
		IPreferenceStore preferenceStore = getPreferenceStore();
		String valueAvtoMappingSettingPrefPage = preferenceStore.getString(autoMappingSettingPrefPage);
		if (valueAvtoMappingSettingPrefPage.length() != 0){
			ByteArrayInputStream bain = new ByteArrayInputStream(valueAvtoMappingSettingPrefPage.getBytes());		
			Properties properties = ormConfiguration.getProperties();
			try {
				properties.load(bain);
			} catch (IOException e) {
				ExceptionHandler.logThrowableError(e, null);
			}
		}
		*/
	}
	
	protected IPreferenceStore doGetPreferenceStore() {
		return ViewPlugin.getDefault().getPreferenceStore();
	}

	protected void performDefaults() {
		ormConfiguration = new OrmConfiguration(OrmPropertyDescriptorsHolder.getInstance(null));
		Properties properties = ormConfiguration.getProperties();
		properties.clear();		
		page.selectionChanged(null, new StructuredSelection(ormConfiguration));
		super.performDefaults();
	}

	public boolean performOk() {
		IPreferenceStore preferenceStore = getPreferenceStore();		
		Properties properties = ormConfiguration.getProperties();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			properties.store(baos, null);
			String value = baos.toString();
			preferenceStore.putValue(ViewPlugin.autoMappingSettingPrefPage,value);
		} catch (IOException e) {
			ExceptionHandler.handle(e, getShell(), null, null);			
			//ExceptionHandler.logThrowableError(e, null);
		}
		
		return super.performOk();
	}

}
