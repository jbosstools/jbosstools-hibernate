/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.LaunchHelper;

/**
 * 
 * @author Dmitry Geraskov (geraskov@gmail.com)
 *
 */
public class RenameAction extends SelectionListenerAction {

	public static final String RENAME_ACTIONID = "actionid.rename"; //$NON-NLS-1$

	private final StructuredViewer viewer;
	private String imageFilePath = "icons/images/refresh_run.gif"; //$NON-NLS-1$

	public RenameAction(StructuredViewer viewer) {
		super(HibernateConsoleMessages.RenameAction_name);
		this.viewer = viewer;
		setImageDescriptor(HibernateConsolePlugin.getImageDescriptor(imageFilePath  ));
		setId(RENAME_ACTIONID);
	}

	public void run() {
		for (Iterator<?> i = getSelectedNonResources().iterator(); i.hasNext();) {
			Object node = i.next();
			if (!(node instanceof ConsoleConfiguration)) {
				continue;
			}
			if (renameConsoleConfiuration((ConsoleConfiguration) node)){
				viewer.refresh(node);
			}
			break;
		}
	}
	
	public boolean renameConsoleConfiuration(ConsoleConfiguration config){
		ILaunchConfiguration launchConfiguration = null;;
		try {
			launchConfiguration = LaunchHelper.findHibernateLaunchConfig(config.getName());
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().showError(null, HibernateConsoleMessages.RenameAction_error_title, e);
		}
		return launchConfiguration != null ? renameLaunchConfiguration(launchConfiguration) : false;
	}
	
	public boolean renameLaunchConfiguration(ILaunchConfiguration launchConfiguration){
		Shell mParentShell = null;
		IInputValidator inputValidator = new NameValidator();
		InputDialog d = new InputDialog(
				mParentShell,
				HibernateConsoleMessages.RenameAction_dialog_title,
				HibernateConsoleMessages.RenameAction_dialog_message,
				launchConfiguration.getName(), inputValidator);
		if (d.open() == Window.OK ){
			try {
				ILaunchConfigurationWorkingCopy wc = launchConfiguration.getWorkingCopy();
				wc.rename(d.getValue());
				wc.doSave();
				return true;
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().showError(mParentShell, HibernateConsoleMessages.RenameAction_error_title, e);
			}
		}

		return false;
	}

}

class NameValidator implements IInputValidator {
	
	@Override
	public String isValid(String newText) {
		ILaunchManager mgr = DebugPlugin.getDefault().getLaunchManager();
		try {
			if (mgr.isExistingLaunchConfigurationName(newText))
				return HibernateConsoleMessages.RenameAction_existing_name;
			if (mgr.isValidLaunchConfigurationName(newText))
				return null;
		}
		catch(Exception iae) {
			HibernateConsolePlugin.getDefault().log(iae);
		}
		return HibernateConsoleMessages.RenameAction_error_name;
	}

}
