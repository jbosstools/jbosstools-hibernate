package org.hibernate.eclipse.logging;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import java.util.*;

public class LoggingHelper {
		
	private static LoggingHelper helper;
	
	private List logManagers = new ArrayList(); 
	
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
			Iterator it = this.logManagers.iterator();
			while (it.hasNext()) {
				PluginLogManager logManager = (PluginLogManager) it.next();
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