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
package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jpt.core.JpaDataSource;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.internal.platform.GenericJpaFactory;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.launch.ICodeGenerationLaunchConstants;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;


/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateFactory extends GenericJpaFactory {

	@Override
	public PersistenceUnit buildPersistenceUnit(Persistence parent, XmlPersistenceUnit persistenceUnit) {
		return new HibernatePersistenceUnit(parent, persistenceUnit);
	}


	@Override
	public JpaDataSource buildJpaDataSource(JpaProject jpaProject, String connectionProfileName) {
		try {
			buildConsoleConfiguration(jpaProject, connectionProfileName);
		} catch (CoreException e) {
			//logErrorMessage("Can't create console configuration for project " + jpaProject.getName(), e);
		}
		return super.buildJpaDataSource(jpaProject, connectionProfileName);
	}
	
	protected void buildConsoleConfiguration(JpaProject jpaProject, String connectionProfileName) throws CoreException{
		if (connectionProfileName == null || connectionProfileName.length() == 0) return;
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType lct = lm.getLaunchConfigurationType(ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID);
		ILaunchConfigurationWorkingCopy wc = lct.newInstance(null, jpaProject.getName());
					
		wc.setAttribute(IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, ConfigurationMode.JPA.toString());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, jpaProject.getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true );
		wc.setAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, (List<String>)null);
		wc.setAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME, connectionProfileName);
				
		wc.doSave();
	}
}
