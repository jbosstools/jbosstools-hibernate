/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;

/**
 * @author Dmitry Geraskov
 *
 */
public class CodeGenerationConsoleNameChange extends Change {
	
	private ILaunchConfiguration fLaunchConfiguration;
	
	private String newCCName;
	
	public CodeGenerationConsoleNameChange(ILaunchConfiguration launchConfiguration, String newCCName){
		assert KnownConfigurations.getInstance().isKnownConfiguration(launchConfiguration.getName());
		this.fLaunchConfiguration = launchConfiguration;
		this.newCCName = newCCName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getName()
	 */
	@Override
	public String getName() {
		return NLS.bind(Messages.CodeGenerationConsoleNameChange_update, fLaunchConfiguration.getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initializeValidationData(IProgressMonitor pm) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		if (fLaunchConfiguration.getAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, (String)null) == null){
			return RefactoringStatus.createFatalErrorStatus(Messages.CodeGenerationConsoleNameChange_error_null_confi);
		} else if (newCCName == null || newCCName.trim().length() == 0){
			return RefactoringStatus.createFatalErrorStatus(Messages.CodeGenerationConsoleNameChange_error_empty_name);
		}
		return new RefactoringStatus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		String oldName = fLaunchConfiguration.getAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, new String());
		ILaunchConfigurationWorkingCopy wc = fLaunchConfiguration.getWorkingCopy();
		wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, newCCName);
		ILaunchConfiguration newLaunchConfig = wc.doSave();
		return new CodeGenerationConsoleNameChange(newLaunchConfig, oldName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.Change#getModifiedElement()
	 */
	@Override
	public Object getModifiedElement() {
		return fLaunchConfiguration;
	}

}
