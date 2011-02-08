/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.libsimport;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.wizards.datatransfer.WizardFileSystemResourceImportPage1;
import org.hibernate.eclipse.HibernatePlugin;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class ImportHibernateJarsWizardPage extends WizardFileSystemResourceImportPage1 {

    public static final String HIBERNATE_PLUGIN_LIB_PATH = "lib"; //$NON-NLS-1$

    protected String errorMessage = null;

	public ImportHibernateJarsWizardPage(IWorkbench aWorkbench, IStructuredSelection selection) {
		super(aWorkbench, selection);
        setTitle(HibernateConsoleMessages.ImportHibernateJarsWizardPage_title);
        setDescription(HibernateConsoleMessages.ImportHibernateJarsWizardPage_description);
	}

    protected void createRootDirectoryGroup(Composite parent) {
    	super.createRootDirectoryGroup(parent);
    	//
    	sourceBrowseButton.setEnabled(false);
    	String libsPath = ""; //$NON-NLS-1$
    	errorMessage = null;
		try {
			File libFolderHibernatePlugin = getHibernatePluginFolder(HIBERNATE_PLUGIN_LIB_PATH);
			if (libFolderHibernatePlugin.exists()) {
				libsPath = libFolderHibernatePlugin.getAbsolutePath();
			} else {
				errorMessage = HibernateConsoleMessages.ImportHibernateJarsWizardPage_error_message;
			}
		} catch (IOException e) {
			errorMessage = "IOException: " + e.getMessage(); //$NON-NLS-1$
		}
		if (libsPath.length() > 0 && !libsPath.endsWith(File.separator)) {
			libsPath += File.separator;
		}
    	sourceNameField.setText(libsPath);
    	sourceNameField.setEnabled(false);
    }

	protected File getHibernatePluginFolder(String path) throws IOException {
		URL entry = HibernatePlugin.getDefault().getBundle().getEntry(path);
		URL resProject = FileLocator.resolve(entry);
		String resolvePath = FileLocator.resolve(resProject).getFile();
		File folder = new File(resolvePath);
		return folder;
	}
	
    protected boolean ensureSourceIsValid() {
    	boolean res = true;
    	if (errorMessage != null) {
    		setErrorMessage(errorMessage);
    		res = false;
    	}
    	if (res) {
    		res = super.ensureSourceIsValid();
    	}
    	return res;
    }
	
}
