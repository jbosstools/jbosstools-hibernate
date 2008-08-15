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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
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
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.hibernate.jpt.ui.internal.platform.HibernatePlatformUI;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateEntitiesWizard extends Wizard {

	private JpaProject jpaProject;

	private IStructuredSelection selection;	
	
	private GenerateInitWizardPage initPage;
	
	private GenerateEntitiesWizardPage page2; 
	
	public GenerateEntitiesWizard( JpaProject jpaProject, IStructuredSelection selection) {
		super();
		this.jpaProject = jpaProject;
		this.selection = selection;
		this.setWindowTitle( JptUiMessages.GenerateEntitiesWizard_generateEntities);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		initPage = new GenerateInitWizardPage(jpaProject);
		page2 = new GenerateEntitiesWizardPage("");
		addPage(initPage);
		addPage(page2);
	}
	
	@Override
	public boolean performFinish() {
		String projectName = jpaProject.getName();
		ILaunchConfigurationWorkingCopy wc = HibernatePlatformUI.createDefaultLaunchConfig(projectName);
		if (wc != null) {
			// SHOULD PRESENT THE CONFIGURATION!!!
			String concoleConfigurationName = initPage.getConfigurationName();			
			wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, concoleConfigurationName);

			wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, page2.getOutputDir()); //$NON-NLS-1$

			wc.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, page2.getPackageName());
			wc.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, true);

			wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);
			wc.setAttribute("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
			wc.setAttribute("hibernate.temp.use_jdbc_metadata_defaults", true);

			wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + '.' + HibernatePlatformUI.exporter_id + ".extension_id", 
						HibernateLaunchConstants.ATTR_PREFIX + "hbm2java"); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				wc.launch(ILaunchManager.RUN_MODE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			} finally{
				if (initPage.isTemporaryConfiguration()){
					KnownConfigurations.getInstance().removeConfiguration(KnownConfigurations.getInstance().find(concoleConfigurationName), false);				
				}
			}
		}
		return true;
	}
}
