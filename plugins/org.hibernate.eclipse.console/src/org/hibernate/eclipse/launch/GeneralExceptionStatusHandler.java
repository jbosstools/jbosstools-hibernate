package org.hibernate.eclipse.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class GeneralExceptionStatusHandler implements IStatusHandler {

	public Object handleStatus(IStatus status, Object source)
			throws CoreException {
		final boolean[] result = new boolean[1];
		HibernateConsolePlugin.openError(null, "Generating code", "Exception while generating code", status.getException(), HibernateConsolePlugin.PERFORM_SYNC_EXEC);
		return new Boolean(result[0]);		
	}

}
