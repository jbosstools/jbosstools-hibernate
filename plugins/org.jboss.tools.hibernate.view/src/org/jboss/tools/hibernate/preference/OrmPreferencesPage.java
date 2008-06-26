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
import java.util.ResourceBundle;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class OrmPreferencesPage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	Control contents;	

	public OrmPreferencesPage() {
		super();
	}

	public OrmPreferencesPage(String title) {
		super(title);
	}

	public OrmPreferencesPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();
		StyledText newControl = new StyledText(parent,SWT.WRAP);
		newControl.setText(ResourceBundle.getBundle(this.getClass().getPackage().getName()+".preferences").getString("ORM_PREF"));
		newControl.setBackground(parent.getBackground());
		newControl.setEditable(false);
		return contents = newControl;
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}
