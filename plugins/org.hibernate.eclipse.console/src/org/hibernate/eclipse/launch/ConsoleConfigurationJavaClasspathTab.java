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
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * 
 * @author Vitali
 */
public class ConsoleConfigurationJavaClasspathTab extends JavaClasspathTab {

	public boolean isShowBootpath() {
		return false;
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (!super.isValid(launchConfig)) {
			return false;
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
		return resUserClasses && resExistArchive;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {

		super.initializeFrom( configuration );
	}

	public boolean canSave() {
		return super.canSave();
	}
}
