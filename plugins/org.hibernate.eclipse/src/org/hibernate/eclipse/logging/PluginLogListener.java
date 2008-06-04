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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * PluginLogListener
 * This class is responsible for adding itself to the plug-in logging framework
 * and translating plug-in log requests to Logger events.
 * @author Manoel Marques
 */
class PluginLogListener implements ILogListener {

	private ILog log;
	private Logger logger;
	
	/**
	 * Creates a new PluginLogListener. Saves the plug-in log and logger instance.
	 * Adds itself to the plug-in log.
	 * @param plugin the plug-in object
	 * @param logger logger instance
	 */
	PluginLogListener(ILog log,Logger logger) {
		this.log = log;
		this.logger = logger;
		log.addLogListener(this);
	}
	
	/**
	 * Removes itself from the plug-in log, reset instance variables.
	 */	
	void dispose() {
		if (this.log != null) {
			this.log.removeLogListener(this);
			this.log = null;
			this.logger = null;
		}
	}

	/**
	 * Log event happened.
	 * Translates status instance to Logger level and message.
	 * Status.ERROR - Level.ERROR
	 * Status.WARNING - Level.WARN
	 * Status.INFO - Level.INFO
	 * Status.CANCEL - Level.FATAL
	 * default - Level.DEBUG
	 * @param status Log Status
	 * @param plugin plug-in id
	 */	
	public void logging(IStatus status, String plugin) {
		if (null == this.logger || null == status) 
			return;
		
		int severity = status.getSeverity();
		Level level = Level.DEBUG;  
		if (severity == IStatus.ERROR)
			level = Level.ERROR;
		else
		if (severity == IStatus.WARNING)
			level = Level.WARN;
		else
		if (severity == IStatus.INFO)
			level = Level.INFO;
		else
		if (severity == IStatus.CANCEL)
			level = Level.FATAL;

		plugin = formatText(plugin);
		String statusPlugin = formatText(status.getPlugin());
		String statusMessage = formatText(status.getMessage());
	    StringBuffer message = new StringBuffer();
		if (plugin != null) {
		    message.append(plugin);
			message.append(" - "); //$NON-NLS-1$
		}    
		if (statusPlugin != null &&
		    (plugin == null || !statusPlugin.equals(plugin))) {
		    message.append(statusPlugin);
		   	message.append(" - "); //$NON-NLS-1$
		}	
		message.append(status.getCode());
		if (statusMessage != null) {
		    message.append(" - "); //$NON-NLS-1$
		    message.append(statusMessage);
		}   				
		this.logger.log(level,message.toString(),status.getException());		
	}
	
	static private String formatText(String text) {
	    if (text != null) {
	        text = text.trim();
		    if (text.length() == 0) return null;
		} 
	    return text;
	}
}