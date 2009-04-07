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
package org.hibernate.eclipse.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class GeneralExceptionStatusHandler implements IStatusHandler {

	public Object handleStatus(IStatus status, Object source)
			throws CoreException {
		final boolean[] result = new boolean[1];
		HibernateConsolePlugin.getDefault().log( status );
		HibernateConsolePlugin.openError(null, HibernateConsoleMessages.GeneralExceptionStatusHandler_generating_code, HibernateConsoleMessages.GeneralExceptionStatusHandler_exception_while_generating_code, status.getException(), HibernateConsolePlugin.PERFORM_SYNC_EXEC);
		return Boolean.valueOf(result[0]);
	}

}
