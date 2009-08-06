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
package org.jboss.tools.hibernate.jpt.core.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jpt.core.JpaProject;
import org.osgi.service.prefs.Preferences;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJptPlugin extends Plugin {
	
	public static final String ID = "org.jboss.tools.hibernate.jpt.core"; //$NON-NLS-1$
	
	private static HibernateJptPlugin inst = null;
	
    public static HibernateJptPlugin getDefault() {
    	if (inst == null) {
    		inst = new HibernateJptPlugin();
    	}
        return inst;
    }

	/**
	 * Log message
	 *
	 */
	private static void log(int severity, String message, Throwable e) {
		getDefault().getLog().log(new Status(severity, ID, message, e));
	}
	
	/**
	 * Short exception log
	 *
	 */
	public static void logException(Throwable e) {
		log(IStatus.ERROR, e.getMessage(),  e);
	}
	
	/**
	 * Short exception log
	 *
	 */
	public static void logException(String message, Throwable e) {
		log(IStatus.ERROR, message,  e);
	}
	
	/**
	 * Short error log call
	 *
	 */
	public static void logError(String message) {
		log(IStatus.ERROR, message, null);
	}
	
	/**
	 * Short warning log call
	 *
	 */
	public static void logWarning(String message) {
		log(IStatus.WARNING, message, null);
	}
	
	/**
	 * Short information log call
	 *
	 */
	public static void logInfo(String message) {
		log(IStatus.INFO, message, null);
	}
	
	public static String getDefaultConsoleConfiguration(JpaProject jpaProject){
		IProject project = jpaProject.getProject();
		IScopeContext scope = new ProjectScope(project);

		Preferences node = scope.getNode("org.hibernate.eclipse.console"); //$NON-NLS-1$

		if(node!=null) {
			return node.get("default.configuration", project.getName() ); //$NON-NLS-1$
		}
		return null;
	}	
	
}
