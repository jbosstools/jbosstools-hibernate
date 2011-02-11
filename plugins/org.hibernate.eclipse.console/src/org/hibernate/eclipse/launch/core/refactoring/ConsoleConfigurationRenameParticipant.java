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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.hibernate.console.KnownConfigurations;

/**
 * 
 * @author Dmitry Geraskov
 *
 */
public class ConsoleConfigurationRenameParticipant extends RenameParticipant {
	
	private String oldName;
	
	@Override
	protected boolean initialize(Object element) {
		if (element instanceof ILaunchConfiguration) {
			oldName = ((ILaunchConfiguration) element).getName();
			return KnownConfigurations.getInstance().isKnownConfiguration(oldName);
		}
		return false;
	}

	@Override
	public String getName() {
		return Messages.ConsoleConfigurationRenameParticipant_name;
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		CompositeChange change = new CompositeChange(Messages.ConsoleConfigurationRenameParticipant_change_name);
		change.markAsSynthetic();
		CompositeChange change1 = new CompositeChange(Messages.ConsoleConfigurationRenameParticipant_update_code_generations);
		change1.addAll(getCodeGenerationConsoleNameChanges());
		CompositeChange change2 = new CompositeChange(Messages.ConsoleConfigurationRenameParticipant_update_project_config);
		change2.addAll(getProjectDefaultConfigurationChanges());
		change.add(change1);
		change.add(change2);
		return change;
	}
	
	/**
	 * @return CodeGenerationConsoleNameChanges
	 */
	private Change[] getCodeGenerationConsoleNameChanges() {
		ILaunchConfiguration[] affectedConfigurations = HibernateRefactoringUtil.getAffectedCodeGenerationConfigs(oldName);
		Change[] changes = new Change[affectedConfigurations.length];
		for (int i = 0; i < affectedConfigurations.length; i++) {
			changes[i] = new CodeGenerationConsoleNameChange(affectedConfigurations[i], getArguments().getNewName());
		}
		return changes;
	}
	
	/**
	 * @return
	 */
	private Change[] getProjectDefaultConfigurationChanges() {
		IProject[] affectedProjects = HibernateRefactoringUtil.getAffectedProjects(oldName);
		Change[] changes = new Change[affectedProjects.length];
		for (int i = 0; i < affectedProjects.length; i++) {
			changes[i] = new ProjectDefaultConfigurationChange(affectedProjects[i], getArguments().getNewName());
		}
		return changes;
	}

}
