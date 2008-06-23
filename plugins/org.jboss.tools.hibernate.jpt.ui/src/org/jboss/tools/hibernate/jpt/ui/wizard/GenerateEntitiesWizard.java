/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.db.ConnectionProfile;
import org.eclipse.jpt.db.Schema;
import org.eclipse.jpt.db.Table;
import org.eclipse.jpt.gen.internal.EntityGenerator;
import org.eclipse.jpt.gen.internal.PackageGenerator;
import org.eclipse.jpt.ui.internal.JptUiMessages;
import org.eclipse.jpt.ui.internal.wizards.DatabaseReconnectWizardPage;
import org.eclipse.jpt.utility.internal.CollectionTools;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateEntitiesWizard extends Wizard {

	private JpaProject jpaProject;

	private IStructuredSelection selection;

	
	
	private Collection<Table> selectedTables;

	public GenerateEntitiesWizard( JpaProject jpaProject, IStructuredSelection selection) {
		super();
		this.jpaProject = jpaProject;
		this.selection = selection;
		this.setWindowTitle( JptUiMessages.GenerateEntitiesWizard_generateEntities);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(new EntitiesInitWizardPage(jpaProject));
	}
	
	@Override
	public boolean performFinish() {

		return true;
	}
	
	private IPackageFragment buildPackageFragment() {
		IPackageFragmentRoot packageFragmentRoot = null;//this.generateEntitiesPage.getPackageFragmentRoot();
		IPackageFragment packageFragment = null;//this.generateEntitiesPage.getPackageFragment();
		
		if ( packageFragment == null) {
			packageFragment= packageFragmentRoot.getPackageFragment( ""); //$NON-NLS-1$
		}
		
		if ( packageFragment.exists()) {
			return packageFragment;
		}

		try {
			return packageFragmentRoot.createPackageFragment( packageFragment.getElementName(), true, null);
		} 
		catch ( JavaModelException ex) {
			throw new RuntimeException( ex);
		}
	}
	


}
