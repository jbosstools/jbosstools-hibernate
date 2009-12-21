/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.hibernate.eclipse.launch.core.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class CodeGenerationReseourceNameChange extends Change {
	
	private ILaunchConfiguration fLaunchConfiguration;
	private IPath fOldPath;
	private IPath fNewPath;
	
	CodeGenerationReseourceNameChange(ILaunchConfiguration launchConfiguration, IPath oldPath, IPath newPath){
		fLaunchConfiguration = launchConfiguration;
		fOldPath = oldPath;
		fNewPath = newPath;
	}

	@Override
	public Object getModifiedElement() {
		return fLaunchConfiguration;
	}

	@Override
	public String getName() {
		return NLS.bind(HibernateConsoleMessages.LaunchConfigurationResourceNameChange_update_resource_path_in_launch_cfg, fLaunchConfiguration.getName());
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) {	}

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		fLaunchConfiguration = HibernateRefactoringUtil.updateCodeGenerationConfig(fLaunchConfiguration, fOldPath, fNewPath);
		return new CodeGenerationReseourceNameChange(fLaunchConfiguration, fNewPath, fOldPath);
	}

}
