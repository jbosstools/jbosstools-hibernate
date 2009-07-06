/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

public class UiPlugin extends AbstractUIPlugin {

	public final static String ID = "org.jboss.tools.hibernate.ui"; //$NON-NLS-1$
	
	//The shared instance.
	private static UiPlugin plugin;
	
	public UiPlugin() {
		setPlugin(this);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		setPlugin(null);
	}

	public static UiPlugin getDefault() {
		return plugin;
	}

	private static void setPlugin(UiPlugin plugin) {
		UiPlugin.plugin = plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	public static ImageDescriptor getImageDescriptor2(String name) {
		final String iconPath = "images/"; //$NON-NLS-1$
		final URL installURL = getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
		URL url = null;
		try {
			url = new URL(installURL, iconPath + name);
		} catch (MalformedURLException e) {
		}
		return ImageDescriptor.createFromURL(url);
	}

	public static IWorkbenchPage getPage(){
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	    return window.getActivePage();
	}

	public static Shell getShell(){
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	    return window.getShell();
	}
}
