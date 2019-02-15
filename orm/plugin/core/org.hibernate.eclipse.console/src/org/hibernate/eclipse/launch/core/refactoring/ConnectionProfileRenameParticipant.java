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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
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
public class ConnectionProfileRenameParticipant extends RenameParticipant {
	
	IConnectionProfile profile;

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {		
		ILaunchConfiguration[] configs = HibernateRefactoringUtil.getAffectedLaunchConfigurations(profile);

		List<Change> changes = new ArrayList<Change>();
		Change change = null;
		for (int i= 0; i < configs.length; i++) {
			change = new ConnectionProfileRenameChange(configs[i], profile, getArguments());
			changes.add(change);
		}

		return HibernateRefactoringUtil.createChangesFromList(changes, getName());
	}

	@Override
	public String getName() {
		return HibernateConsoleMessages.ConnectionProfileRenameParticipant_launch_configurations_updates;
	}

	@Override
	protected boolean initialize(Object element) {
		profile = (IConnectionProfile) element;
		return true;
	}

}
