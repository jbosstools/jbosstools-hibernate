/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation, JBoss Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Max Rydahl Andersen, JBoss Inc. - made non-static for reuse instead of copy/paste.
 *******************************************************************************/
package org.hibernate.eclipse.logging.xpl;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * Non static implementation of the Logger in WST.
 * 
 * Small convenience class to log messages to plugin's log file and also, if
 * desired, the console. Other plugins should create a Logger with their own id.
 */
public class EclipseLogger {
	
	public static final int ERROR = IStatus.ERROR; // 4
	public static final int ERROR_DEBUG = 200 + ERROR;
	public static final int INFO = IStatus.INFO; // 1
	public static final int INFO_DEBUG = 200 + INFO;
	public static final int OK = IStatus.OK; // 0
	public static final int OK_DEBUG = 200 + OK;

	private static final String TRACEFILTER_LOCATION = "/debug/tracefilter"; //$NON-NLS-1$
	public static final int WARNING = IStatus.WARNING; // 2
	public static final int WARNING_DEBUG = 200 + WARNING;

	private final String PLUGIN_ID;
	private Bundle bundle;
	
	public EclipseLogger(String pluginid) {
		this.PLUGIN_ID = pluginid;
		bundle = Platform.getBundle(PLUGIN_ID);
	}

	public EclipseLogger(Bundle bundle) {
		this.bundle = bundle;
		this.PLUGIN_ID = bundle.getSymbolicName();
	}

	/**
	 * Adds message to log.
	 * 
	 * @param level
	 *            severity level of the message (OK, INFO, WARNING, ERROR,
	 *            OK_DEBUG, INFO_DEBUG, WARNING_DEBUG, ERROR_DEBUG)
	 * @param message
	 *            text to add to the log
	 * @param exception
	 *            exception thrown
	 */
	protected void _log(int level, String message, Throwable exception) {
		if (level == OK_DEBUG || level == INFO_DEBUG || level == WARNING_DEBUG || level == ERROR_DEBUG) {
			if (!isDebugging() )
				return;
		}

		int severity = IStatus.OK;
		switch (level) {
			case INFO_DEBUG :
			case INFO :
				severity = IStatus.INFO;
				break;
			case WARNING_DEBUG :
			case WARNING :
				severity = IStatus.WARNING;
				break;
			case ERROR_DEBUG :
			case ERROR :
				severity = IStatus.ERROR;
		}
		Status statusObj = new Status(severity, PLUGIN_ID, severity, message, exception);
		
		if (bundle != null)
			Platform.getLog(bundle).log(statusObj);
	}

	/**
	 * Prints message to log if category matches /debug/tracefilter option.
	 * 
	 * @param message
	 *            text to print
	 * @param category
	 *            category of the message, to be compared with
	 *            /debug/tracefilter
	 */
	protected void _trace(String category, String message, Throwable exception) {
		if (!isDebugging() )
			return;

		String traceFilter = Platform.getDebugOption(PLUGIN_ID + TRACEFILTER_LOCATION);
		if (traceFilter != null) {
			StringTokenizer tokenizer = new StringTokenizer(traceFilter, ","); //$NON-NLS-1$
			while (tokenizer.hasMoreTokens() ) {
				String cat = tokenizer.nextToken().trim();
				if (category.equals(cat) ) {
					Status statusObj = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, message, exception);
					Bundle bundle = Platform.getBundle(PLUGIN_ID);
					if (bundle != null)
						Platform.getLog(bundle).log(statusObj);
					return;
				}
			}
		}
	}

	/**
	 * @return true if the platform is debugging
	 */
	public boolean isDebugging() {
		return Platform.inDebugMode();
	}

	public void log(int level, String message) {
		_log(level, message, null);
	}

	public void log(int level, String message, Throwable exception) {
		_log(level, message, exception);
	}

	public void logException(String message, Throwable exception) {
		_log(ERROR, message, exception);
	}

	public void logException(Throwable exception) {
		_log(ERROR, exception.getMessage(), exception);
	}

	public void trace(String category, String message) {
		_trace(category, message, null);
	}

	public void traceException(String category, String message, Throwable exception) {
		_trace(category, message, exception);
	}

	public void traceException(String category, Throwable exception) {
		_trace(category, exception.getMessage(), exception);
	}

	/**
	 * Only for completeness - please use the more specific log/trace methods.
	 * @param status
	 */
	public void log(IStatus status) {
		Platform.getLog(bundle).log(status);
	}
}
