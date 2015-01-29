package org.jboss.tools.hibernate.runtime.common.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class HibernateRuntimeCommon extends Plugin {
	
	private static final String pluginId = "org.jboss.tools.hibernate.runtime.common";

	private static HibernateRuntimeCommon plugin;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static void log(Throwable t) {
		plugin.getLog().log(new Status(IStatus.ERROR, pluginId, t.getMessage(), t));
	}

}
