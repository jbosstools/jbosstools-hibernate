/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.ui.internal.JptUiMessages;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;
import org.jboss.tools.hibernate.jpt.ui.internal.platform.HibernateJpaPlatformUi;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateEntitiesWizard extends Wizard {


	private JpaProject jpaProject;
	
	private GenerateEntitiesWizardPage initPage;
	
	
	public GenerateEntitiesWizard( JpaProject jpaProject, IStructuredSelection selection) {
		super();
		this.jpaProject = jpaProject;
		this.setWindowTitle( JptUiMessages.GenerateEntitiesWizard_generateEntities);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		initPage = new GenerateEntitiesWizardPage(jpaProject);
		addPage(initPage);
	}
	
	@Override
	public boolean performFinish() {
		String projectName = jpaProject.getName();
		ILaunchConfigurationWorkingCopy wc = HibernateJpaPlatformUi.createDefaultLaunchConfig(projectName);
		if (wc != null) {
			// SHOULD PRESENT THE CONFIGURATION!!!
			String concoleConfigurationName = initPage.getConfigurationName();			
			wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, concoleConfigurationName);

			wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, initPage.getOutputDir());

			wc.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, initPage.getPackageName());
			wc.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, true);

			wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);
			wc.setAttribute("hibernate.temp.use_jdbc_metadata_defaults", true); //$NON-NLS-1$

			wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + '.' + HibernateJpaPlatformUi.exporter_id + ".extension_id",  //$NON-NLS-1$
						HibernateLaunchConstants.ATTR_PREFIX + "hbm2java"); //$NON-NLS-1$
			try {
				wc.launch(ILaunchManager.RUN_MODE, null);
			} catch (CoreException e) {
				HibernateJptUIPlugin.logException(e);
			} finally{
				if (initPage.isTemporaryConfiguration()){
					KnownConfigurations.getInstance().removeConfiguration(KnownConfigurations.getInstance().find(concoleConfigurationName), false);				
				}
			}
		}
		return true;
	}

}
