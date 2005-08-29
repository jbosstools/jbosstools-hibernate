/*
 * Created on 11-Dec-2004
 *
 */
package org.hibernate.eclipse;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Plugin;
import org.hibernate.eclipse.logging.LoggingHelper;
import org.hibernate.eclipse.logging.PluginLogManager;
import org.osgi.framework.BundleContext;

/**
 * @author max
 *
 */
public class HibernatePlugin extends Plugin {

	public void start(BundleContext context) throws Exception {
		super.start(context);
		configureLog4jHooks();
		Log log = LogFactory.getLog(HibernatePlugin.class);
		log.info("HibernatePlugin Started");		
	}
	
	private void configureLog4jHooks() {
		URL entry = getBundle().getEntry("hibernate-log4j.xml");
		if(entry==null) {
			entry = getBundle().getEntry("hibernate-log4j.properties");	
		}
		
		if(entry==null) {
			// should log this!
		} else {
			LoggingHelper helper = LoggingHelper.getDefault();
			PluginLogManager manager = new PluginLogManager(this, helper, entry);			
		}
		
		
		
		
	}

	public void stop(BundleContext context) throws Exception {
		LoggingHelper.getDefault().stop(context);
		super.stop( context );
	}
}
