/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.libsimport;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * Import Hibernate jars wizard responsible to import Hibernate jars
 * from Hibernate Tools internal libs into user specified place.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ImportHibernateJarsWizard extends Wizard implements IImportWizard {
    private IWorkbench workbench;

    private IStructuredSelection selection;

    private ImportHibernateJarsWizardPage mainPage;

    /**
     * Creates a wizard for importing Hibernate jars into the workspace from
     * the file system.
     */
    public ImportHibernateJarsWizard() {
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    public void addPages() {
        super.addPages();
        mainPage = new ImportHibernateJarsWizardPage(workbench, selection);
        addPage(mainPage);
    }


    /* (non-Javadoc)
     * Method declared on IWorkbenchWizard.
     */
    @SuppressWarnings("rawtypes")
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
        this.workbench = workbench;
        this.selection = currentSelection;

        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }

        setWindowTitle(HibernateConsoleMessages.ImportHibernateJarsWizard_title);
		ImageDescriptor descriptor = HibernateConsolePlugin.getImageDescriptor("icons/images/newhibernate_wiz.gif"); //$NON-NLS-1$
		setDefaultPageImageDescriptor(descriptor);
        setNeedsProgressMonitor(true);
    }

    /* (non-Javadoc)
     * Method declared on IWizard.
     */
    @SuppressWarnings("restriction")
	public boolean performFinish() {
        return mainPage.finish();
    }
}
