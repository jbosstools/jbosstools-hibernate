/*
 * Created on 11-Dec-2004
 *
 */
package org.hibernate.eclipse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author max
 *
 */
public class HibernatePlugin extends Plugin {

	public void start(BundleContext context) throws Exception {
		super.start(context);
		Log log = LogFactory.getLog(HibernatePlugin.class);
		log.info("HibernatePlugin Started");		
	}
}
