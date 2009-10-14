package org.hibernate.eclipse.launch.core.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class ConsoleConfigurationITypeRenameParticipant extends
		RenameParticipant {

	private IType fType;

	protected boolean initialize(Object element) {
		fType= (IType) element;
		return true;
	}

	public String getName() {
		return HibernateConsoleMessages.ConsoleConfigurationITypeRenameParticipant_update; 
	}
	
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException {
		return HibernateRefactoringUtil.createChangesForTypeRename(fType, getArguments().getNewName());
	}

}
