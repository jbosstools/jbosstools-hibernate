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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ViewPlugin extends BaseUIPlugin {
	private static ViewPlugin plugin;
	private ResourceBundle resourceBundle;

	/**
	 * @deprecated use bundle via ImageBundle.getString()
	 */
	public static final ResourceBundle BUNDLE_IMAGE = ImageBundle.getBundle();

	public static boolean TRACE = false;
	public static boolean TRACE_VIEW = false;
	public static boolean TRACE_WIZARD = false;

	public static final String PLUGIN_ID = "org.jboss.tools.hibernate.view"; //$NON-NLS-1$
	public static final String autoMappingSettingPrefPage = "autoMappingSettingPrefPage";	 //$NON-NLS-1$

	public ViewPlugin() {
		super();
		plugin = this;

		try {
			resourceBundle = ResourceBundle.getBundle(PLUGIN_ID + ".EditPluginResources"); //$NON-NLS-1$
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "images/"; //$NON-NLS-1$
		try {
			URL installURL = getDefault().getBundle().getEntry("/");; //$NON-NLS-1$
			URL url = new URL(installURL, iconPath + name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public static ViewPlugin getDefault() {
		return plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = ViewPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static IWorkbenchPage getPage(){
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	    return window.getActivePage();
	}

	static {

		String value = Platform.getDebugOption(PLUGIN_ID + "/debug"); //$NON-NLS-1$
		if (value != null && value.equalsIgnoreCase("true")) TRACE = true; //$NON-NLS-1$

		value = Platform.getDebugOption(PLUGIN_ID + "/debug/view"); //$NON-NLS-1$
		if (value != null && value.equalsIgnoreCase("true")) TRACE_VIEW = true; //$NON-NLS-1$

		value = Platform.getDebugOption(PLUGIN_ID + "/debug/view/wizard"); //$NON-NLS-1$
		if (value != null && value.equalsIgnoreCase("true")) TRACE_WIZARD = true;		 //$NON-NLS-1$

	}

	public static void loadPreferenceStoreProperties(Properties properties, String key){
		IPreferenceStore preferenceStore = ViewPlugin.getDefault().getPreferenceStore();
		String value = preferenceStore.getString(key);
		if (value.length() != 0){
			ByteArrayInputStream bain = new ByteArrayInputStream(value.getBytes());
			try {
				properties.load(bain);
			} catch (IOException e) {
				getDefault().logError(UIViewMessages.ViewPlugin_canot_load_preference_store_properties, e);
			}
		}
	}
}