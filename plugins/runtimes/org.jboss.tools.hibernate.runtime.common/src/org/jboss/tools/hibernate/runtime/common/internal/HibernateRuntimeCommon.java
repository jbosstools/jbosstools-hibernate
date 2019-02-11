package org.jboss.tools.hibernate.runtime.common.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class HibernateRuntimeCommon extends Plugin {
	
	private static final String PLUGIN_ID = "org.jboss.tools.hibernate.runtime.common";
	private static HibernateRuntimeCommon PLUGIN;


	public static HibernateRuntimeCommon getDefault() {
		return PLUGIN;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		PLUGIN = this;
	}

	public void stop(BundleContext context) throws Exception {
		PLUGIN = null;
		super.stop(context);
	}

	public static void log(Throwable t) {
		PLUGIN.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, t.getMessage(), t));
	}
	
}
