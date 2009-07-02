/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ViewPlugin extends AbstractUIPlugin {

	public static final String ID = "org.jboss.tools.hibernate.view"; //$NON-NLS-1$

	private static ViewPlugin plugin;

	public ViewPlugin() {
		super();
		setPlugin(this);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		setPlugin(null);
	}

	public static ViewPlugin getDefault() {
		return plugin;
	}
	private static void setPlugin(ViewPlugin plugin) {
		ViewPlugin.plugin = plugin;
	}

	public static IWorkbenchPage getPage(){
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	    return window.getActivePage();
	}

}