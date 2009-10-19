/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.process.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.ChangeStructure;

/**
 * Hibernate JPA refactoring
 *
 * @author Vitali
 */
public class HibernateJPARefactoring extends Refactoring {

	/**
	 * change info storage
	 */
	protected ArrayList<ChangeStructure> changes;

	public HibernateJPARefactoring(ArrayList<ChangeStructure> changes) {
		this.changes = changes;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm){
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
		return RefactoringStatus.create(Status.OK_STATUS);
	}

	@Override
	public Change createChange(IProgressMonitor pm){

		final CompositeChange cc = new CompositeChange(""); //$NON-NLS-1$
		for (int i = 0; i < changes.size(); i++) {
			ChangeStructure cs = changes.get(i);
			final String change_name = cs.path.toString();
			TextFileChange change = new TextFileChange(change_name, (IFile)cs.icu.getResource());
			change.setSaveMode(TextFileChange.LEAVE_DIRTY);
			change.setEdit(cs.textEdit);
			cs.change = change;
			cc.add(change);
		}
		cc.markAsSynthetic();
		return cc;
	}

	@Override
	public String getName() {
		return JdtUiMessages.SaveQueryEditorListener_composite_change_name;
	}
}
