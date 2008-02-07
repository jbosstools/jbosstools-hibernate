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

public class MoveResourceParticipant extends MoveParticipant {
	
	private IResource fResource;

	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		ILaunchConfiguration[] configs = HibernateRefactoringUtil.getChangedLaunchConfigurations(fResource.getFullPath());
		
		List changes = new ArrayList();		
		LaunchConfigurationResourceNameChange change = null;
		for (int i= 0; i < configs.length; i++) {
			change = new LaunchConfigurationResourceNameChange(configs[i], fResource.getFullPath(), ((IResource)getArguments().getDestination()).getFullPath().append(fResource.getName()));
			changes.add(change);
		}

		return HibernateRefactoringUtil.createChangesFromList(changes, getName());
	}

	public String getName() {
		return "Launch Configurations updates";
	}

	protected boolean initialize(Object element) {		
		fResource = (IResource) element;
		return true;
	}

}
