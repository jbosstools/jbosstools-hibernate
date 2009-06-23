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

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;

/**
 * PluginLogManager
 * This class encapsulates a Log4J Hierarchy and centralizes all Logger access.
 * @author Manoel Marques
 */
public class PluginLogManager {

	private ILog log;
	private IPath stateLocation;
	private Hierarchy hierarchy;
	private HashMap<String, ILogListener> hookedPlugins = new HashMap<String, ILogListener>();
	private LoggingHelper helper; 
	
	private class PluginEventListener implements HierarchyEventListener {
		
		/**
		 * Called when a new appender is added for a particular level.
		 * Internally it checks if the appender is one of our custom ones
		 * and sets its custom properties. 
		 * @param category level
		 * @param appender appender added for this level
		 */
		public void addAppenderEvent(Category cat, Appender appender) {
			if (appender instanceof PluginLogAppender) {
				((PluginLogAppender)appender).setLog(log);
			}			
			if (appender instanceof PluginFileAppender) {
				((PluginFileAppender)appender).setStateLocation(stateLocation);
			}
		}
		
		/**
		 * Called when a appender is removed from for a particular level.
		 * Does nothing.
		 * @param category level
		 * @param appender appender added for this level
		 */
		public void removeAppenderEvent(Category cat, Appender appender) {
		}
	}
	
	/**
	 * Creates a new PluginLogManager. Saves the plug-in log and state location.
	 * Creates a new Hierarchy and add a new PluginEventListener to it.
	 * Configure the hierarchy with the properties passed.
	 * Add this object to the list of active plug-in log managers. 
	 * @param plugin the plug-in object
	 * @param properties log configuration properties
	 */
	public PluginLogManager(Plugin plugin, LoggingHelper helper, URL log4jUrl) {
		this.log = plugin.getLog();  
		this.stateLocation = plugin.getStateLocation(); 
		this.hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
		this.hierarchy.addHierarchyEventListener(new PluginEventListener());
		LogManager.setRepositorySelector(new RepositorySelector() {
		
			public LoggerRepository getLoggerRepository() {
				return hierarchy;
			}
		
		}, "hibernate-tools"); //$NON-NLS-1$
		OptionConverter.selectAndConfigure(log4jUrl, null, this.hierarchy);
		this.helper = helper;
		helper.addLogManager(this); 
	}
	
	/**
	 * Hooks a plug-in into this PluginLogManager. When the hooked plug-in uses the
	 * Eclipse log API, it will be channeled to this logging framework.
	 * @param id logger name (usually the the plug-in id)
	 * @param pluginLog plug-in log
	 */
	public boolean hookPlugin(String id, ILog pluginLog) {
		synchronized(this.hookedPlugins) {
			if (pluginLog == null || id == null || this.hookedPlugins.containsKey(id))
				return false;
				
			PluginLogListener listener = new PluginLogListener(pluginLog,getLogger(id));
			this.hookedPlugins.put(id,listener);
		}		
		return true;
	}

	/**
	 * Unhooks a plug-in from this PluginLogManager. The Eclipse log API
	 * won't be channeled to this logging framework anymore.
	 * @param id logger name (usually the the plug-in id)
	 */
	public boolean unHookPlugin(String id) {
		synchronized(this.hookedPlugins) {
			if (id == null || !this.hookedPlugins.containsKey(id))
				return false;
					
			PluginLogListener listener = (PluginLogListener) this.hookedPlugins.get(id);
			listener.dispose(); 
			this.hookedPlugins.remove(id);
		}		
		return true;
	}
	
	/**
	 * Checks if this PluginLogManager is disabled for this level.
	 * @param level level value
	 * @return boolean true if it is disabled
	 */
	public boolean isDisabled(int level) {
		return this.hierarchy.isDisabled(level);
	}
	
	/**
	 * Enable logging for logging requests with level l or higher.
	 * By default all levels are enabled.
	 * @param level level object
	 */
	public void setThreshold(Level level) {
		this.hierarchy.setThreshold(level);
	}
	
	/**
	 * The string version of setThreshold(Level level)
	 * @param level level string
	 */
	public void setThreshold(String level) {
		this.hierarchy.setThreshold(level);
	}

	/**
	 * Get the repository-wide threshold.
	 * @return Level
	 */
	public Level getThreshold() {
		return this.hierarchy.getThreshold();
	}

	/**
	 * Returns a new logger instance named as the first parameter
	 * using the default factory. If a logger of that name already exists,
	 * then it will be returned. Otherwise, a new logger will be instantiated 
	 * and then linked with its existing ancestors as well as children.
	 * @param name logger name
	 * @return Logger
	 */
	public Logger getLogger(String name) {
		return this.hierarchy.getLogger(name);
	}
	
	/**
	 * The same as getLogger(String name) but using a factory instance instead of
	 * a default factory.
	 * @param name logger name
	 * @param factory factory instance 
	 * @return Logger
	 */
	public Logger getLogger(String name, LoggerFactory factory) {
		return this.hierarchy.getLogger(name,factory);
	}

	/**
	 * Returns the root of this hierarchy.
	 * @return Logger
	 */
	public Logger getRootLogger() {
		return this.hierarchy.getRootLogger();
	}

	/**
	 * Checks if this logger exists.
	 * @return Logger
	 */
	public Logger exists(String name) {
		return this.hierarchy.exists(name);
	}
	
	/**
	 * Removes appenders and disposes the logger hierarchy
	 *
	 */
	public void shutdown() {
		internalShutdown();
		helper.removeLogManager(this); 
	}
	
	/**
	 * Used by LoggingHelper to shutdown without removing it from the LoggingHelper list
	 *
	 */
	void internalShutdown() {
		synchronized(this.hookedPlugins) {
			Iterator<String> it = this.hookedPlugins.keySet().iterator();
			while (it.hasNext()) {
				String id = it.next(); 
				PluginLogListener listener = (PluginLogListener) this.hookedPlugins.get(id);
				listener.dispose(); 
			}
			this.hookedPlugins.clear(); 
		}	
		this.hierarchy.shutdown();
	}
	
	/**
	 * Returns all the loggers in this manager.
	 * @return Enumeration logger enumeration
	 */
	public Enumeration<?> getCurrentLoggers() {
		return this.hierarchy.getCurrentLoggers();
	}

	/**
	 * Resets configuration values to its defaults.
	 * 
	 */
	public void resetConfiguration() {
		this.hierarchy.resetConfiguration();
	}
}