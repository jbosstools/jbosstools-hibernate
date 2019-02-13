/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.logging.LoggingHelper;
import org.hibernate.eclipse.logging.PluginLogManager;
import org.osgi.framework.BundleContext;

/**
 * @author max
 */
public class HibernatePlugin extends Plugin {

	// The shared instance.
	private static HibernatePlugin plugin;

	/**
	 * The constructor.
	 */
	public HibernatePlugin() {
		super();
		setPlugin(this);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		configureLog4jHooks();
		Log log = LogFactory.getLog(HibernatePlugin.class);
		log.info("HibernatePlugin Started"); //$NON-NLS-1$
	}

	private void configureLog4jHooks() {
		URL entry = getBundle().getEntry("hibernate-log4j.xml"); //$NON-NLS-1$
		if (entry == null) {
			entry = getBundle().getEntry("hibernate-log4j.properties"); //$NON-NLS-1$
		}

		if (entry == null) {
			// should log this!
		} else {
			LoggingHelper helper = LoggingHelper.getDefault();
			new PluginLogManager(this, helper, entry);
		}
	}

	public void stop(BundleContext context) throws Exception {
		LoggingHelper.getDefault().stop(context);
		super.stop(context);
		setPlugin(null);
	}

	/**
	 * Returns the shared instance.
	 */
	public static HibernatePlugin getDefault() {
		return plugin;
	}

	private static void setPlugin(HibernatePlugin plugin) {
		HibernatePlugin.plugin = plugin;
	}

	public static File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		URL entry = getDefault().getBundle().getEntry(strResPath);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}
	
	public void log(Throwable t) {
		plugin.getLog().log(new Status(Status.ERROR, getId(), t.getMessage(), t));
	}
	
	public static IStatus throwableToStatus(Throwable t, int code) {
		List<IStatus> causes = new ArrayList<IStatus>();
		Throwable temp = t;
		while(temp!=null && temp.getCause()!=temp) {
			causes.add(new Status(IStatus.ERROR, getId(), code, temp.getMessage()==null?temp.toString() + HibernateConsoleMessages.HibernateConsolePlugin_no_message_1:temp.toString(), temp) );
			temp = temp.getCause();
		}
        String msg = HibernateConsoleMessages.HibernateConsolePlugin_no_message_2;
        if(t!=null && t.getMessage()!=null) {
            msg = t.toString();
        }

        if(causes.isEmpty()) {
        	return new Status(IStatus.ERROR, getId(), code, msg, t);
        } else {
        	return new MultiStatus(getId(), code,causes.toArray(new IStatus[causes.size()]), msg, t);
        }
	}
	
	public static String getId() {
		return plugin.getBundle().getSymbolicName();
	}

	
}
