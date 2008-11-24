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
