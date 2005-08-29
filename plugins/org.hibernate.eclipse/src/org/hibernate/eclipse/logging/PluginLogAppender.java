package org.hibernate.eclipse.logging;

import org.eclipse.core.runtime.ILog; 
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status; 
import org.apache.log4j.Level; 
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * PluginLogAppender
 * This class is a custom Log4J appender that sends Log4J events to 
 * the Eclipse plug-in log.
 * @author Manoel Marques
 */
public class PluginLogAppender extends AppenderSkeleton {

	private ILog pluginLog;
	
	/**
	 * Sets the Eclipse log instance
	 * @param log plug-in log
	 */	
	void setLog(ILog pluginLog) {
		this.pluginLog = pluginLog;
	}
	
	/**
	 * Log event happened.
	 * Translates level to status instance codes:
	 * level > Level.ERROR - Status.ERROR
	 * level > Level.WARN - Status.WARNING
	 * level > Level.DEBUG - Status.INFO
	 * default - Status.OK
	 * @param event LoggingEvent instance
	 */	
	public void append(LoggingEvent event) {
		
		if (this.layout == null) {
			this.errorHandler.error("Missing layout for appender " + 
			       this.name,null,ErrorCode.MISSING_LAYOUT); 
			return;
		}
		
		String text = this.layout.format(event);
		
		Throwable thrown = null;
		if (this.layout.ignoresThrowable()) {
			ThrowableInformation info = event.getThrowableInformation();
			if (info != null)
				thrown = info.getThrowable(); 
		}
				
		Level level = event.getLevel();
		int severity = IStatus.OK;
		
		if (level.toInt() >= Priority.ERROR_INT) 
			severity = IStatus.ERROR;
		else
		if (level.toInt() >= Priority.WARN_INT)
			severity = IStatus.WARNING;
		else
		if (level.toInt() >= Priority.DEBUG_INT) 
			severity = IStatus.INFO;
			
		this.pluginLog.log(new Status(severity,
		         this.pluginLog.getBundle().getSymbolicName(),
				 level.toInt(),text,thrown));
	}
	
	/**
	 * Closes this appender
	 */	
	public void close() {
		this.closed = true; 
	}
	
	/**
	 * Checks if this appender requires layout
	 * @return true if layout is required.
	 */	
	public boolean requiresLayout() {
		return true;
	}
}