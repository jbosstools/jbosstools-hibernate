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
package org.jboss.tools.hibernate.view;

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
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;



/**
 * The main plugin class to be used in the desktop.
 */
public class ViewPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static ViewPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	public static final ResourceBundle BUNDLE_IMAGE = ResourceBundle.getBundle(ViewPlugin.class.getPackage().getName() + ".image");
	
	// add Tau 28.04.2005 for trace
	public static boolean TRACE = false;
	public static boolean TRACE_VIEW = false;	
	public static boolean TRACE_WIZARD = false;
	
	public static final String PLUGIN_ID = "org.jboss.tools.hibernate.view";
	public static final String autoMappingSettingPrefPage = "autoMappingSettingPrefPage";	
	
	/**
	 * The constructor.
	 */
	public ViewPlugin() {
		super();
		plugin = this;
		
		try {
			resourceBundle = ResourceBundle.getBundle(PLUGIN_ID + ".EditPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		
//		if (TRACE || TRACE_VIEW ) 
//			ExceptionHandler.logObjectPlugin("ViewPlugin()",PLUGIN_ID, null);		
		
	}

	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "images/";
		try {
			URL installURL = getDefault().getBundle().getEntry("/");;
			URL url = new URL(installURL, iconPath + name);
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static ViewPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ViewPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
	// add tau 05.04.2005	
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}	

	// add tau 05.04.2005	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}
	
	// add tau 19.04.2005	
	public static IWorkbenchPage getPage(){
	    IWorkbench workbench = PlatformUI.getWorkbench();
	    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
	    return window.getActivePage();
	}
	
	//	 add Tau 28.04.2005 for trace
	static {
		
		String value = Platform.getDebugOption(PLUGIN_ID + "/debug");
		if (value != null && value.equalsIgnoreCase("true")) TRACE = true;
		
		value = Platform.getDebugOption(PLUGIN_ID + "/debug/view");
		if (value != null && value.equalsIgnoreCase("true")) TRACE_VIEW = true;
		
		value = Platform.getDebugOption(PLUGIN_ID + "/debug/view/wizard");
		if (value != null && value.equalsIgnoreCase("true")) TRACE_WIZARD = true;		
		
	}
	
	// add tau 22.12.2005	
	public static void loadPreferenceStoreProperties(Properties properties, String key){
		IPreferenceStore preferenceStore = ViewPlugin.getDefault().getPreferenceStore();
		String value = preferenceStore.getString(key);
		if (value.length() != 0){
			ByteArrayInputStream bain = new ByteArrayInputStream(value.getBytes());		
			try {
				properties.load(bain);
			} catch (IOException e) {
//				ExceptionHandler.logThrowableError(e, null);
			}
		}
	}	

}
