/*******************************************************************************
 * Copyright (c) 2007-2011 Red Hat, Inc.
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
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class MoveResourceParticipant extends MoveParticipant {

	private IResource fResource;

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		ILaunchConfiguration[] configs = HibernateRefactoringUtil.getAffectedConsoleConfigs(fResource.getFullPath());

		List<Change> changes = new ArrayList<Change>();
		Change change = null;
		for (int i= 0; i < configs.length; i++) {
			change = new LaunchConfigurationResourceNameChange(configs[i], fResource.getFullPath(), ((IResource)getArguments().getDestination()).getFullPath().append(fResource.getName()));
			changes.add(change);
		}
		
		configs = HibernateRefactoringUtil.getAffectedCodeGenerationConfigs(fResource.getFullPath());
		for (int i= 0; i < configs.length; i++) {
			change = new CodeGenerationResourceNameChange(configs[i], fResource.getFullPath(), ((IResource)getArguments().getDestination()).getFullPath().append(fResource.getName()));
			changes.add(change);
		}

		return HibernateRefactoringUtil.createChangesFromList(changes, getName());
	}

	public String getName() {
		return HibernateConsoleMessages.MoveResourceParticipant_launch_configurations_updates;
	}

	protected boolean initialize(Object element) {
		fResource = (IResource) element;
		return true;
	}

}
