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
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Provides an easy way to log status of events.
 *
 * NOTE: It is useful to make the static method getPluginLog()
 * which provides the interface IPluginLog for using it in your code
 * in future
 *
 * @author Sergey Vasilyev
 *
 */

public class BaseUIPlugin extends AbstractUIPlugin {

	public void logError(String message, Throwable t) {
		LogHelper.logError(this, message, t);
	}

	public void logError(String message) {
		LogHelper.logError(this, message);
	}

	public void logError(Throwable t) {
		LogHelper.logError(this, t);
	}

	public void logInfo(String message, Throwable t) {
		LogHelper.logInfo(this, message, t);
	}

	public void logInfo(String message) {
		LogHelper.logInfo(this, message);
	}

	public void logWarning(String message, Throwable t) {
		LogHelper.logWarning(this, message, t);
	}

	public void logWarning(String message) {
		LogHelper.logWarning(this, message);
	}

	public void logWarning(Throwable t) {
		LogHelper.logWarning(this, t);
	}

	public void showError(String message, Throwable t) {
		logError(message, t);
		Shell shell = Display.getDefault().getActiveShell();
		IStatus s = StatusFactory.getInstance(IStatus.ERROR, this.getBundle().getSymbolicName(), message, t);
		ErrorDialog.openError(shell, UIViewMessages.BaseUIPlugin_hibernate_console, message, s);
	}
}