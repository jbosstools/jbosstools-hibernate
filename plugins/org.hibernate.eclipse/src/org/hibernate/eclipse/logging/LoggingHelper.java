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
package org.hibernate.eclipse.logging;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class LoggingHelper {
		
	private static LoggingHelper helper;
	
	private List<PluginLogManager> logManagers = new ArrayList<PluginLogManager>(); 
	
	public LoggingHelper() {
		super();	
	}
	
	synchronized public static LoggingHelper getDefault() {
		if(helper==null) { helper = new LoggingHelper(); }
		return helper;
	}

	/**
	 * Iterates over the list of active log managers and shutdowns each one
	 * @see Plugin#stop
	 */
	public void stop(BundleContext context) throws Exception {
		synchronized (this.logManagers) {
			for (PluginLogManager logManager : logManagers) {
				logManager.internalShutdown(); 
			}
			this.logManagers.clear(); 
		}		
	}
	
	/**
	 * Adds a log manager object to the list of active log managers
	 */	
	void addLogManager(PluginLogManager logManager) {
		synchronized (this.logManagers) {
			if (logManager != null)
				this.logManagers.add(logManager); 
		}
	}
	
	/**
	 * Removes a log manager object from the list of active log managers
	 */
	void removeLogManager(PluginLogManager logManager) {
		synchronized (this.logManagers) {
			if (logManager != null)
				this.logManagers.remove(logManager); 
		}
	}
}