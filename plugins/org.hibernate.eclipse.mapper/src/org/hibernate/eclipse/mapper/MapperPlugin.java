package org.hibernate.eclipse.mapper;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.eclipse.EclipseLogger;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MapperPlugin extends AbstractUIPlugin {
	
	final public static String ID = MapperPlugin.class.getName();
	
	//The shared instance.
	private static MapperPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	private final EclipseLogger logger = new EclipseLogger(ID);
	
	/**
	 * The constructor.
	 */
	public MapperPlugin() {
		super();
		plugin = this;
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
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MapperPlugin getDefault() {
		return plugin;
	}
	
	public EclipseLogger getLogger() {
		return logger;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = MapperPlugin.getDefault().getResourceBundle();
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
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.hibernate.eclipse.mapper.MapperPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	public void logException(Throwable exception) {
		getLogger().logException(exception);
	}
}
