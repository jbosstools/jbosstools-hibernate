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
package org.jboss.tools.hibernate.jpt.ui.internal.platform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.ui.JpaPlatformUiProvider;
import org.eclipse.jpt.ui.JpaUiFactory;
import org.eclipse.jpt.ui.internal.platform.generic.GenericJpaPlatformUi;
import org.eclipse.jpt.ui.navigator.JpaNavigatorProvider;
import org.eclipse.jpt.ui.structure.JpaStructureProvider;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;
import org.jboss.tools.hibernate.jpt.ui.wizard.GenerateDdlWizard;
import org.jboss.tools.hibernate.jpt.ui.wizard.GenerateEntitiesWizard;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernateJpaPlatformUi extends GenericJpaPlatformUi {

	public static final String LaunchConfigurationType_ID = "org.hibernate.eclipse.launch.CodeGenerationLaunchConfigurationType"; //$NON-NLS-1$

	public static final String exporter_id = "hbmexporter"; //$NON-NLS-1$
	public static final String full_exporter_id = HibernateLaunchConstants.ATTR_EXPORTERS + '.' + "hbmexporter"; //$NON-NLS-1$

	public HibernateJpaPlatformUi(
			JpaUiFactory jpaUiFactory,
			JpaNavigatorProvider navigatorProvider, 
			JpaStructureProvider persistenceStructureProvider, 
			JpaStructureProvider javaStructureProvider,
			JpaPlatformUiProvider... platformUiProviders) 
	{
		super(jpaUiFactory, navigatorProvider, persistenceStructureProvider, javaStructureProvider, platformUiProviders);
	}

	@Override
	public void generateEntities(JpaProject project, IStructuredSelection selection) {
		GenerateEntitiesWizard wizard = new GenerateEntitiesWizard(project, selection);
		
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.open();
	}

	@Override
	public void generateDDL(JpaProject project, IStructuredSelection selection) {
		GenerateDdlWizard wizard = new GenerateDdlWizard(project, selection);
		
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.open();
	}
	
	public static ILaunchConfigurationWorkingCopy createDefaultLaunchConfig(String projectName) {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType launchConfigurationType = launchManager
				.getLaunchConfigurationType(LaunchConfigurationType_ID);

		ILaunchConfigurationWorkingCopy wc = null;
		try {
			wc = launchConfigurationType.newInstance(null, projectName + "-hibernate-generate"); //$NON-NLS-1$
			// Create exporters
			List<String> exporters = new ArrayList<String>();
			exporters.add(exporter_id);
			wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS, exporters);
			wc.setAttribute(full_exporter_id, true);
		} catch (CoreException e) {
			HibernateJptUIPlugin.logException(e);
		}

		return wc;
	}
}
