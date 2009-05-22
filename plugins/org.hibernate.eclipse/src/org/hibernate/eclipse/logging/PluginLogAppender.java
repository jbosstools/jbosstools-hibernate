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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.console.MessageConsoleStream;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.KnownConfigurations;

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
			this.errorHandler.error(ConsoleMessages.PluginLogAppender_missing_layout_for_appender +
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
		/*
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
				 level.toInt(),text,thrown));*/

		Object peek = CurrentContext.peek();
		MessageConsoleStream stream = KnownConfigurations.getInstance().findLoggingStream( (String)peek );
		if(stream!=null) {
			stream.println(text);
			if(thrown!=null) {
				StringWriter stringWriter = new StringWriter();
				thrown.printStackTrace( new PrintWriter(stringWriter) );
				stream.println(stringWriter.getBuffer().toString());
			}
		}

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