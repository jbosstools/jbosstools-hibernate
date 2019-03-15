package org.jboss.tools.hibernate.search.runtime.spi.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class HibernateSearchServicePlugin extends Plugin {
	
	private static final String pluginId = "org.jboss.tools.hibernate.search.runtime.spi";

	private static HibernateSearchServicePlugin plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static HibernateSearchServicePlugin getDefault() {
		return plugin;
	}
	
	public void log(Throwable t) {
		getLog().log(new Status(IStatus.ERROR, pluginId, t.getMessage(), t));
	}

}
