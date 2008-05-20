/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.ui.view;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

public class LogHelper {
	public static void logError(Plugin plugin, Throwable t) {
		logError(plugin, null, t);
	}

	public static void logError(Plugin plugin, String message) {
		logError(plugin, message, null);
	}
	
	public static void logError(Plugin plugin, String message, Throwable t) {
		IStatus status = StatusFactory.getInstance(IStatus.ERROR, 
				plugin.getBundle().getSymbolicName(), message, t);
		logStatus(plugin, status); 
	}
	
	public static void logError(String pluginId, Throwable t) {
		logError(pluginId, null, t);
	}

	public static void logError(String pluginId, String message) {
		logError(pluginId, message, null);
	}
	
	public static void logError(String pluginId, String message, Throwable t) {
		IStatus status = StatusFactory.getInstance(IStatus.ERROR, pluginId,
				message, t);
		logStatus(pluginId, status);
	}
	
	public static void logWarning(Plugin plugin, Throwable t) {
		logWarning(plugin, null, t);
	}
	
	public static void logWarning(Plugin plugin, String message) {
		logWarning(plugin, message, null);
	}
	
	
	public static void logWarning(Plugin plugin, String message, Throwable t) {
		IStatus status = StatusFactory.getInstance(IStatus.WARNING, 
				plugin.getBundle().getSymbolicName(), message, t);
		logStatus(plugin, status); 
	}
	
	public static void logWarning(String pluginId, Throwable t) {
		logWarning(pluginId, null, t);
	}
	
	public static void logWarning(String pluginId, String message) {
		logWarning(pluginId, message, null);
	}
	
	
	public static void logWarning(String pluginId, String message,
								Throwable t) {
		IStatus status = StatusFactory.getInstance(IStatus.WARNING, pluginId,
				message, t);
		logStatus(pluginId, status);
	}

	public static void logInfo(Plugin plugin, String message,
								Throwable t) {
		IStatus status = StatusFactory.getInstance(IStatus.INFO, 
				plugin.getBundle().getSymbolicName(), message, t);
		logStatus(plugin, status); 
	}
	
	
	public static void logInfo(Plugin plugin, String message) {
		IStatus status = StatusFactory.getInstance(IStatus.INFO, 
				plugin.getBundle().getSymbolicName(), message);
		logStatus(plugin, status); 
	}

	public static void logInfo(String pluginId, String message,
								Throwable t) {
		IStatus status = StatusFactory.getInstance(IStatus.INFO, pluginId,
				message, t);
		logStatus(pluginId, status);
	}
	
	public static void logInfo(String pluginId, String message) {
		IStatus status = StatusFactory.getInstance(IStatus.INFO, pluginId,
				message);
		logStatus(pluginId, status);
	}

	public static void log(int severity, String pluginId, int code,
							String message, Throwable t) {
		IStatus status = StatusFactory.getInstance(severity, pluginId, code,
				message, t);
		logStatus(pluginId, status);
	}
	
	public static void logStatus(Plugin plugin, IStatus status) {
		plugin.getLog().log(status);
	}

	public static void logStatus(String pluginId, IStatus status) {
		Bundle bundle = Platform.getBundle(pluginId);
		logStatus(bundle, status);
	}
	
	public static void logStatus(Bundle bundle, IStatus status) {
		ILog log = Platform.getLog(bundle);
		log.log(status);
	}
}
