/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.xpl.ExceptionErrorDialog;

/**
 */
public class ExceptionHandler {

    private static ExceptionHandler fgInstance = new ExceptionHandler();
	private static ThreadLocal<Throwable> lastException = new ThreadLocal<Throwable>();

    /**
     * 
     */
	public static IStatus logThrowableError(Throwable e, String message) {
		if (e != null) {
			setLastException(e);
		}
		if (message == null) {
			message = OrmCore.getResourceString("OrmCore.internal_error");
		}
		IStatus status= new Status(
			IStatus.ERROR, 
			OrmCore.PLUGIN_ID, 
			IStatus.ERROR, 
			message, 
			e); 
		OrmCore.getPluginLog().logError(message,e);
        return status; 
	}
	
	public static IStatus logThrowableWarning(Throwable e, String message) {
		if (e != null) {
			setLastException(e);
		}
		if (message == null) {
			message = OrmCore.getResourceString("OrmCore.diagnostic_warning");
		}
		IStatus status= new Status(
			IStatus.WARNING, 
			OrmCore.PLUGIN_ID, 
			IStatus.WARNING, 
			message, 
			e); 
        return status;
	}
	
	public static IStatus logInfo(String message) {
		String newMessage = Thread.currentThread().getName() + " " + message; 
		IStatus status= new Status(
			IStatus.INFO, 
			OrmCore.PLUGIN_ID, 
			IStatus.INFO, 
			newMessage, 
			null); 
		return status; // add tau 31.03.2005		
	}
	
	public static IStatus logObjectPlugin(String message, String pluginID, Object object  ) {
		String newMessage = Thread.currentThread().getName() + " " + message;		
		IStatus status= new Status(
			IStatus.INFO, 
			pluginID, 
			IStatus.INFO, 
			newMessage + " || " + object, 
			null);
		return status;
	}		


	public static void handle(Exception e, Shell parent, String title,
			String message) {
		if (e instanceof CoreException) {
			fgInstance.performCoreException((CoreException) e, parent, title,
					message);
		} else if (e instanceof InvocationTargetException) {
			fgInstance.performInvocationTargetException(
					(InvocationTargetException) e, parent, title, message,
					IStatus.ERROR);
		} else {
			fgInstance.performException(e, parent, title, message,
					IStatus.ERROR);
		}
	}
    
    public static void handleSeverity(Exception e, Shell parent, String title, String message, int severity) {
    	if (e instanceof CoreException){
            fgInstance.performCoreException((CoreException)e, parent, title, message); // not put severity !!!    		
    	} else if (e instanceof InvocationTargetException){
            fgInstance.performInvocationTargetException((InvocationTargetException)e, parent, title, message, severity);  // put severity
    	} else {
            fgInstance.performException(e, parent, title, message, severity);  // put severity
    	}
    }    

    /**
     * Handles the given <code>InvocationTargetException</code>.
     * 
     * @param e
     *            the <code>InvocationTargetException</code> to be handled
     * @param parent
     *            the dialog window's parent shell
     * @param title
     *            the dialog window's window title
     * @param message
     *            message to be displayed by the dialog window
     */
    public static void handleAsyncExec(final Exception e, final Shell parent, final String title, final String message, final int severity ) {
    	parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				ExceptionHandler.handleSeverity(e, parent, title, message, severity);
			}
		});    
        
    }    

    protected void performCoreException(CoreException e, Shell shell, String title, String message) {
		setLastException(e);
        IStatus status = e.getStatus();
        if (status != null) {
            ErrorDialog.openError(shell, title, message, status);
        } else {
            displayMessageDialog(e, shell, title, message);
        }
    }


    protected void performInvocationTargetException(InvocationTargetException e, Shell shell, String title, String message, int severity) {
        Throwable target = e.getTargetException();
        if (target instanceof CoreException) {
            performCoreException((CoreException) target, shell, title, message);
        } else if (target instanceof InvocationTargetException) {
        	performInvocationTargetException((InvocationTargetException) target, shell, title, message, severity);
        } else if (target instanceof Exception) {
        	performException((Exception) target, shell, title, message, severity);        	
        } else {
			setLastException(e);
            if (e.getMessage() != null && e.getMessage().length() > 0) {
                displayMessageDialog(e, shell, title, message);
            } else {
                displayMessageDialog(target, shell, title, message);
            }        	
        }

    }    
    
    protected void performException(Exception e, Shell shell, String title, String message, int severity) {
		setLastException(e);
		displayMessageDialog(e, shell, title, message);
    }

	public static void displayMessageDialog(Throwable t, Shell shell, String title, String message) {
        StringWriter msg = new StringWriter();
        
      if (t != null) {
    	    t.printStackTrace(new PrintWriter(msg));    	  
      } else if (message != null) {
    		  msg.write(message);
   	  }
        
   	 (new ExceptionErrorDialog(shell, title, t.getClass().getName(), msg.toString())).open();// add 8/9/2005 gavrs
    }

	public static void clearLastException(){
		lastException.set(null);
	}
	
	public static Throwable getLastException(){
		return (Throwable)lastException.get();
	}
	private static void setLastException(Throwable t){
		lastException.set(t);
	}
}

