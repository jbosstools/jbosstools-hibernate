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
package org.hibernate.eclipse.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.eclipse.console.EclipseLaunchConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * 
 * @author Vitali
 */
public class ConsoleConfigurationJavaClasspathTab extends JavaClasspathTab {

	/**
	 * validation process should include check for successful creation
	 * of configuration xml file for Hibernate Console Configuration.
	 */
	protected boolean configurationFileWillBeCreated = false;
	// for validation process optimization:
	// presave last time validated configuration and validate result,
	// to avoid several unnecessary validation -> buildWith for ConsoleConfig
	// is rather slow operation (for classpaths with many jar files) - 
	// so several time rebuild is visible for GUI performance operation.
	protected ILaunchConfiguration lastValidatedLaunchConfig = null;
	protected String lastErrorMessage = null;
	protected boolean lastRes = false;
	
	public boolean isShowBootpath() {
		return false;
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (!super.isValid(launchConfig)) {
			return false;
		}
		if (lastValidatedLaunchConfig != null && lastValidatedLaunchConfig.contentsEqual(launchConfig)) {
			setErrorMessage(lastErrorMessage);
			return lastRes;
		}
		setErrorMessage(null);
		setMessage(null);
		boolean resUserClasses = false, resExistArchive = true;
		IRuntimeClasspathEntry[] entries;
		try {
			entries = JavaRuntime.computeUnresolvedRuntimeClasspath(launchConfig);
			for (int i = 0; i < entries.length; i++) {
				IRuntimeClasspathEntry entry = entries[i];
				if (entry.getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
					resUserClasses = true;
					if (entry.getType() == IRuntimeClasspathEntry.ARCHIVE) {
						if (!entry.getPath().toFile().exists()) {
							resExistArchive = false;
							String out = NLS.bind(HibernateConsoleMessages.ConsoleConfigurationTabGroup_archive_classpath_entry_does_not_exist, entry.getPath().toString());
							setErrorMessage(out);
						}
					}
				}
			}
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log( e );
		}
		if (!resUserClasses) {
			setErrorMessage(HibernateConsoleMessages.ConsoleConfigurationTabGroup_classpath_must_be_set_or_restored_to_default);
		}
		if (resUserClasses && resExistArchive) {
			boolean flagTryToBuild = true;
			final ConsoleConfiguration ccTest = new ConsoleConfiguration(new EclipseLaunchConsoleConfigurationPreferences(launchConfig));
			if (configurationFileWillBeCreated) {
				// do not make a try to build console configuration in case of "configurationFileWillBeCreated" and
				// exception to resolve the file
				try {
					ccTest.getConfigXMLFile();
				} catch (HibernateConsoleRuntimeException ex) {
					flagTryToBuild = false;
				}
			}
			if (flagTryToBuild) {
				try {
					ccTest.buildWith(null, false);
				} catch (Exception ex) {
					resUserClasses = false;
					setErrorMessage(ex.getMessage());
				}
				// accurate reset for ccTest after buildWith, should avoid possible "leaks"
				try {
					ccTest.reset();
				} catch (Exception ex) {
					if (resUserClasses) {
						resUserClasses = false;
						setErrorMessage(ex.getMessage());
					}
				}
				try {
					lastValidatedLaunchConfig = launchConfig.getWorkingCopy();
				} catch (CoreException e1) {
					lastValidatedLaunchConfig = null;
				}
			}
		}
		final boolean res = resUserClasses && resExistArchive;
		if (lastValidatedLaunchConfig != null) {
			lastErrorMessage = getErrorMessage();
			lastRes = res;
		}
		return res;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
	}

	public boolean canSave() {
		return super.canSave();
	}

	public void markConfigurationFileWillBeCreated() {
		configurationFileWillBeCreated = true;
	}
}
