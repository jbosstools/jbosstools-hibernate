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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class RenameResourceParticipant extends RenameParticipant {

	private IResource fResource;

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#checkConditions(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
	 */
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#createChange(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		ILaunchConfiguration[] configs = HibernateRefactoringUtil.getAffectedConsoleConfigs(fResource.getFullPath());

		List<Change> changes = new ArrayList<Change>();
		Change change = null;
		for (int i= 0; i < configs.length; i++) {
			change = new LaunchConfigurationResourceNameChange(configs[i], fResource.getFullPath(),
					fResource.getParent().getFullPath().append(getArguments().getNewName()));
			changes.add(change);
		}
		
		configs = HibernateRefactoringUtil.getAffectedCodeGenerationConfigs(fResource.getFullPath());
		for (int i= 0; i < configs.length; i++) {
			change = new CodeGenerationReseourceNameChange(configs[i], fResource.getFullPath(),
					fResource.getParent().getFullPath().append(getArguments().getNewName()));
			changes.add(change);
		}

		return HibernateRefactoringUtil.createChangesFromList(changes, getName());
	}



	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#getName()
	 */
	public String getName() {
		return HibernateConsoleMessages.RenameResourceParticipant_launch_configurations_updates;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant#initialize(java.lang.Object)
	 */
	protected boolean initialize(Object element) {
		fResource = (IResource) element;
		return true;
	}

}
