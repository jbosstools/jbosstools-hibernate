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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class StatusFactory {
	public final static int UNDEFINED_ERROR = 0;
	public final static String UNSPECIFIED_MESSAGE = null;
	public final static String EMPTY_MESSAGE = "";
	public final static String EMPTY_PLUGIN = "";
	
	public static IStatus getInstance(int severity, String pluginId,
									int code, String message, Throwable t) {
		return new Status(severity, pluginId == null ? EMPTY_PLUGIN : pluginId,
						code, checkMessage(message, t) , t);
	}

	public static IStatus getInstance(int severity, int code, String message,
									Throwable t) {
		return getInstance(severity, EMPTY_PLUGIN, code, message, t);
	}

	public static IStatus getInstance(int severity, String pluginId,
			String message, Throwable t) {
		return getInstance(severity, pluginId, UNDEFINED_ERROR, message, t);
	}

	public static IStatus getInstance(int severity, String pluginId,
			String message) {
		return getInstance(severity, pluginId, UNDEFINED_ERROR, message, null);
	}

	public static IStatus getInstance(int severity, String pluginId,
			Throwable t) {
		return getInstance(severity, pluginId, UNDEFINED_ERROR, EMPTY_MESSAGE, t);
	}
	
	public static IStatus getInstance(int severity, String pluginId,
			int code, Throwable t) {
		return getInstance(severity, pluginId, code, EMPTY_MESSAGE, t);
	}
	
	public static IStatus getInstance(int severity, String pluginId,
			int code, String message) {
		return getInstance(severity, pluginId, code, message, null);
	}
	
	private static String checkMessage(String message, Throwable t) {
		if (message == UNSPECIFIED_MESSAGE) {
			if (t != null && t.getMessage() != null) {
				return t.getMessage();
			}
			
			return EMPTY_MESSAGE;
		}
		
		return message;
	}
}
