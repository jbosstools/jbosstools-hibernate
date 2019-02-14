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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;

/**
 * @author Dmitry Geraskov
 *
 */
public class ConsoleConfigurationEntityResolverChange extends Change {

	private ILaunchConfiguration fLaunchConfiguration;
	private String fNewEntityResolverName;
	private String fOldEntityResolverTypeName;
    
	public ConsoleConfigurationEntityResolverChange(ILaunchConfiguration launchConfiguration, String newEntityResolverName) throws CoreException {
		fLaunchConfiguration = launchConfiguration;
		fNewEntityResolverName = newEntityResolverName;
		fOldEntityResolverTypeName = fLaunchConfiguration.getAttribute(IConsoleConfigurationLaunchConstants.NAMING_STRATEGY, (String) null);
	}

	@Override
	public Object getModifiedElement() {
		return fLaunchConfiguration;
	}

	@Override
	public String getName() {
		return HibernateConsoleMessages.ConsoleConfigurationITypeRenameParticipant_update_names;
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) { }

	@Override
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		final ILaunchConfigurationWorkingCopy wc = fLaunchConfiguration.getWorkingCopy();
		String oldEntityResolverTypeName = fOldEntityResolverTypeName;
		wc.setAttribute(IConsoleConfigurationLaunchConstants.ENTITY_RESOLVER, fNewEntityResolverName);

		fLaunchConfiguration = wc.doSave();

		// create the undo change
		return new ConsoleConfigurationEntityResolverChange(fLaunchConfiguration, oldEntityResolverTypeName);
	}
}
