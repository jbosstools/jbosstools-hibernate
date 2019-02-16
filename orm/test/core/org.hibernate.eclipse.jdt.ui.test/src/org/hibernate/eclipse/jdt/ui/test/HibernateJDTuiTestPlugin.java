package org.hibernate.eclipse.jdt.ui.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class HibernateJDTuiTestPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.hibernate.eclipse.jdt.ui.test"; //$NON-NLS-1$

	// The shared instance
	private static HibernateJDTuiTestPlugin plugin;
	
	/**
	 * The constructor
	 */
	public HibernateJDTuiTestPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setPlugin(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		setPlugin(null);
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static HibernateJDTuiTestPlugin getDefault() {
		return plugin;
	}

	private static void setPlugin(HibernateJDTuiTestPlugin plugin) {
		HibernateJDTuiTestPlugin.plugin = plugin;
	}
	
	public File getFileInPlugin(IPath path) throws CoreException {
		try {
			URL installURL= new URL(getBundle().getEntry("/"), path.toString()); //$NON-NLS-1$
			URL localURL= FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, getBundle().getSymbolicName(), IStatus.ERROR, e.getMessage(), e));
		}
	}

}
