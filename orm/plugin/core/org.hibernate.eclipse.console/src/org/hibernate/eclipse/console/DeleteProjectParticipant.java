package org.hibernate.eclipse.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.hibernate.eclipse.launch.core.refactoring.ConsoleConfigurationDeleteJavaProjectChange;
import org.hibernate.eclipse.launch.core.refactoring.HibernateRefactoringUtil;

public class DeleteProjectParticipant extends DeleteParticipant {
	
	private IProject javaProject;

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm,
			CheckConditionsContext context) throws OperationCanceledException {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		ILaunchConfiguration[] configs = HibernateRefactoringUtil.getAffectedLaunchConfigurations(javaProject);

		List<Change> changes = new ArrayList<Change>();
		for (int i= 0; i < configs.length; i++) {
			changes.add(new ConsoleConfigurationDeleteJavaProjectChange(configs[i]));
		}

		return HibernateRefactoringUtil.createChangesFromList(changes, getName());
	}

	@Override
	public String getName() {
		return HibernateConsoleMessages.DeleteProjectParticipant_console_configurations_updates;
	}

	@Override
	protected boolean initialize(Object element) {
		javaProject = (IProject)element;
		return true;
	}

}
