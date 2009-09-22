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

import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;

/**
 * Hibernate JPA refactoring wizard dialog
 *
 * @author Vitali
 */
@SuppressWarnings("restriction")
public class HibernateJPAWizard extends RefactoringWizard {

	protected final String wizard_title = JdtUiMessages.AllEntitiesProcessor_header;

	protected IHibernateJPAWizardData data;

	protected IHibernateJPAWizardParams params;

	public HibernateJPAWizard(IHibernateJPAWizardData data, IHibernateJPAWizardParams params) {
		super(new HibernateJPARefactoring(data.getChanges()), RefactoringWizard.WIZARD_BASED_USER_INTERFACE);
		this.data = data;
		this.params = params;
		setWindowTitle(wizard_title);
		setDefaultPageTitle(wizard_title);
	}

	@Override
	protected void addUserInputPages() {
		UserInputWizardPage page = new EntitiesList(wizard_title, data, params);
		addPage(page);
		/** /
		UserInputWizardPage page2 = new ResolveAmbiguous(wizard_title, data, params);
		addPage(page2);
		/**/
	}

	public HibernateJPARefactoring getHibernateJPARefactoring() {
		return (HibernateJPARefactoring)getRefactoring();
	}

	public boolean showWizard() {
		final IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final Shell shell = win.getShell();
		final RefactoringStarter refactoringStarter = new RefactoringStarter();
		boolean res = refactoringStarter.activate(this, shell, wizard_title, RefactoringSaveHelper.SAVE_ALL);
		//RefactoringStatus rs = refactoringStarter.getInitialConditionCheckingStatus();
		return res;
	}

}
