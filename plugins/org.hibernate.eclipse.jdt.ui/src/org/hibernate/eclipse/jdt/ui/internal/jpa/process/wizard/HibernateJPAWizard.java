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
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;

/**
 * Hibernate JPA refactoring wizard dialog
 *
 * @author Vitali
 */
@SuppressWarnings("restriction")
public class HibernateJPAWizard extends RefactoringWizard implements IPageChangingListener {

	protected final String wizard_title = JdtUiMessages.AllEntitiesProcessor_header;

	protected IHibernateJPAWizardData data;

	protected IHibernateJPAWizardParams params;

	public HibernateJPAWizard(IHibernateJPAWizardData data, IHibernateJPAWizardParams params) {
		super(new HibernateJPARefactoring(data.getChanges()), RefactoringWizard.WIZARD_BASED_USER_INTERFACE);
		this.data = data;
		this.params = params;
		setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD));
		setWindowTitle(wizard_title);
		setDefaultPageTitle(wizard_title);
	}

	@Override
	protected void addUserInputPages() {
		
		IStructuredSelection selection = null;
		if (data != null && data.getSelection2Update() != null) {
			selection = data.getSelection2Update();
		}
		if (selection == null) {
			selection = new StructuredSelection();
		}
		String title = JdtUiMessages.EntitiesSource_header;
		EntitiesSource page0 = new EntitiesSource(title, selection);
		addPage(page0);
		
		title = JdtUiMessages.EntitiesList_header;
		UserInputWizardPage page1 = new EntitiesList(title, data, params);
		addPage(page1);
		setDefaultPageTitle(page1.getName());
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

	public IWizardPage getStartingPage() {
        if (getPages().length <= 0) {
			return null;
		}
        if (getPages().length == 1) {
			return getPages()[0];
		}
		return getPages()[1];
    }
	
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		if (getContainer() instanceof WizardDialog) {
			((WizardDialog) getContainer()).removePageChangingListener(this);
		}
		super.setContainer(wizardContainer);
		if (getContainer() instanceof WizardDialog) {
			((WizardDialog) getContainer()).addPageChangingListener(this);
		}
    }

	public void handlePageChanging(PageChangingEvent event) {
		Object currentPage = event.getCurrentPage();
		Object targetPage = event.getTargetPage();
		if (targetPage instanceof IWizardPage) {
			setDefaultPageTitle(((IWizardPage)targetPage).getName());
		}
		if (currentPage instanceof EntitiesSource && targetPage instanceof EntitiesList){
			EntitiesSource entitiesSource = (EntitiesSource)currentPage;
			EntitiesList entitiesList = (EntitiesList)targetPage;
			final IStructuredSelection selection = entitiesSource.getSelection();
			IHibernateJPAWizardData data = 
				HibernateJPAWizardDataFactory.createHibernateJPAWizardData(selection,
				params);
			entitiesList.setData(data);
		}
	}

}
