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
		if(super.isValid( launchConfig )) {
			setErrorMessage( null );
			setMessage( null );
			IRuntimeClasspathEntry[] entries;
			try {
				entries = JavaRuntime.computeUnresolvedRuntimeClasspath(launchConfig);
				for (int i = 0; i < entries.length; i++) {
					IRuntimeClasspathEntry entry = entries[i];
					if(entry.getClasspathProperty()==IRuntimeClasspathEntry.USER_CLASSES) {
						return true;
					}
				}

			}
			catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log( e );
			}
			setErrorMessage( HibernateConsoleMessages.ConsoleConfigurationTabGroup_classpath_must_be_set_or_restored_to_default );
			return false;
		}
		return false;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {

		super.initializeFrom( configuration );
	}

	public boolean canSave() {
		return super.canSave();
	}
}
