/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.launch.core.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ui.refactoring.ConnectionProfileChange;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;

/**
 * @author Dmitry Geraskov
 *
 */
public class ConnectionProfileRenameChange extends ConnectionProfileChange {
	
	
	private ILaunchConfiguration fLaunchConfiguration;
	
	private RenameArguments fRenameArguments;
	
	/**
	 * @param source
	 * @param renameArguments 
	 */
	public ConnectionProfileRenameChange(ILaunchConfiguration config, IConnectionProfile source, RenameArguments renameArguments) {
		super(source, null);
		this.fLaunchConfiguration = config;
		this.fRenameArguments = renameArguments;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	@Override
	public String getName() {
		return HibernateConsoleMessages.ConnectionProfileRenameChange_update_connection_profile_name;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initializeValidationData(IProgressMonitor pm) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		if (fLaunchConfiguration.exists()){
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			IPath rootLoacation = root.getLocation();
			IPath location = fLaunchConfiguration.getLocation();

			IFile[] files = root.findFilesForLocation(rootLoacation.append(location));
			if (files.length > 0){
				fLaunchConfiguration = DebugPlugin.getDefault().getLaunchManager().getLaunchConfiguration(files[0]);
			}
			
			final ILaunchConfigurationWorkingCopy wc = fLaunchConfiguration.getWorkingCopy();
			String oldName = wc.getAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME, ""); //$NON-NLS-1$
			wc.setAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME, fRenameArguments.getNewName());
			ILaunchConfiguration newConfig = wc.isDirty() ? wc.doSave() : fLaunchConfiguration;
			RenameArguments args = new RenameArguments(oldName, true);
			return new ConnectionProfileRenameChange(newConfig, mSource, args);
		}
		
		return null;		
	}

}
