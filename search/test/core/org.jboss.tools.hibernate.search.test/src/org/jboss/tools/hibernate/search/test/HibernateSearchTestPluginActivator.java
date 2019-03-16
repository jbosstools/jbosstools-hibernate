package org.jboss.tools.hibernate.search.test;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class HibernateSearchTestPluginActivator extends AbstractUIPlugin {
	
	private static HibernateSearchTestPluginActivator instance;

	public HibernateSearchTestPluginActivator() {
		super();
		instance = this;
	}
	
	public static HibernateSearchTestPluginActivator getDefault() {
		return instance;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		instance = null;
	}

}
