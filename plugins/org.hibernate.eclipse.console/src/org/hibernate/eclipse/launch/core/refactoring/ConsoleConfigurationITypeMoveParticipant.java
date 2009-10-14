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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class ConsoleConfigurationITypeMoveParticipant extends MoveParticipant {

	private IType fType;
	private IJavaElement fDestination;

	protected boolean initialize(Object element) {
		fType= (IType) element;
		Object destination= getArguments().getDestination();
		if (destination instanceof IPackageFragment || destination instanceof IType) {
			fDestination= (IJavaElement) destination;
			return true;
		}
		return false;
	}

	public String getName() {
		return HibernateConsoleMessages.ConsoleConfigurationITypeRenameParticipant_update; 
	}
	
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException {
		return HibernateRefactoringUtil.createChangesForTypeMove(fType, fDestination);
	}

}
