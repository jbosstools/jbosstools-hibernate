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

package org.jboss.tools.hibernate.jpt.ui;

import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.hibernate.cfg.Environment;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.launch.ICodeGenerationLaunchConstants;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;

public class ConsoleConfigurationFacetInstallDelegate implements IDelegate {

	public void execute(IProject project, IProjectFacetVersion fv, Object config, IProgressMonitor monitor)
			throws CoreException {
		ConsoleConfigurationFacetModel model = (ConsoleConfigurationFacetModel) config;
		if (model.getConsoleConfigurationName() == null || "".equals(model.getConsoleConfigurationName())) return;
		//create cc
		
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType lct = lm.getLaunchConfigurationType(ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID);
		ILaunchConfigurationWorkingCopy wc = lct.newInstance(null, model.getConsoleConfigurationName());
		
		//TODO: change to work with connection profile when JBIDE-719 is finished.
		
		wc.setAttribute(IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, ConfigurationMode.JPA.toString());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, model.getProjectName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true );
		wc.setAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, (List<String>)null);
		
		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(model.getConnectionProfileName());
		if (null != profile) {					
			Properties cpProperties = profile.getProperties(profile.getProviderId());
			String driver = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.driverClass");
			wc.setAttribute(Environment.DRIVER, driver);
			String url = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.URL");
			wc.setAttribute(Environment.URL, url);
			String user = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.username");
			if (null != user && user.length() > 0) {
				wc.setAttribute(Environment.USER, user);
			}
			String pass = cpProperties.getProperty("org.eclipse.datatools.connectivity.db.password");
			if (null != pass && pass.length() > 0) {
				wc.setAttribute(Environment.PASS, pass);
			}
						
			/* Hibernate dialect must explicitly be set(error)
				need to register driver before
			Properties props = new Properties();
			props.putAll(wc.getAttributes());
			try {
				DatabaseMetaData meta = DriverManager.getConnection(url, user, pass).getMetaData();
				String databaseName = meta.getDatabaseProductName();
				int databaseMajorVersion = meta.getDatabaseMajorVersion();
				//SQL Dialect:
				Dialect dialect = DialectFactory.buildDialect(props, databaseName, databaseMajorVersion );
				wc.setAttribute(Environment.DIALECT, dialect.toString());
			} catch (Exception e) {
				//can't determine dialect
			}
			*/
		}
		
		wc.doSave();
	}	

}
