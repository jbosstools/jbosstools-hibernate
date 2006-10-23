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
package org.hibernate.eclipse.console;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.workbench.ConfigurationAdapterFactory;
import org.hibernate.eclipse.criteriaeditor.CriteriaEditorInput;
import org.hibernate.eclipse.criteriaeditor.CriteriaEditorStorage;
import org.hibernate.eclipse.hqleditor.HQLEditorInput;
import org.hibernate.eclipse.hqleditor.HQLEditorStorage;
import org.hibernate.eclipse.logging.xpl.EclipseLogger;
import org.osgi.framework.BundleContext;
import org.hibernate.eclipse.logging.PluginLogger;

/**
 * The main plugin class to be used in the desktop.
 */
public class HibernateConsolePlugin extends AbstractUIPlugin implements PluginLogger {
	
	public static final String ID = "org.hibernate.eclipse.console";
	
	static public final String LAST_USED_CONFIGURATION_PREFERENCE = "lastusedconfig";

	public static final int PERFORM_SYNC_EXEC = 1;
	
	//The shared instance.
	private static HibernateConsolePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private EclipseLogger logger;

	private JavaTextTools javaTextTools;
	
	/**
	 * The constructor.
	 */
	public HibernateConsolePlugin() {
		super();
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		logger=new EclipseLogger(context.getBundle());
		HibernateConsoleSaveParticipant participant = new HibernateConsoleSaveParticipant();
		participant.doStart(this);
		
		IAdapterManager adapterManager = Platform.getAdapterManager();
		ConfigurationAdapterFactory fact =  new ConfigurationAdapterFactory();
		fact.registerAdapters(adapterManager);
				
	}

	
	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
		resourceBundle = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static HibernateConsolePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = HibernateConsolePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("org.hibernate.eclipse.console.HibernateConsolePluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status status to log
	 */
	public void log(IStatus status) {
		logger.log(status);
	}
	
	
	/**
	 * Logs an internal info with the specified message.
	 * 
	 * @param message the error message to log
	 */
	public void log(String message) {
		log(new Status(IStatus.INFO, HibernateConsolePlugin.ID, 0, message, null) );
	}
	
	/**
	 * Logs an internal error with the specified message.
	 * 
	 * @param message the error message to log
	 */
	public void logErrorMessage(String message, Throwable t) {
		if(t==null) {
			log(message);
		} else {
			log(new MultiStatus(HibernateConsolePlugin.ID, IStatus.ERROR , new IStatus[] { throwableToStatus(t) }, message, null) );
		}
	}
	
	public static IStatus throwableToStatus(Throwable t, int code) {
		List causes = new ArrayList();
		Throwable temp = t;
		while(temp!=null && temp.getCause()!=temp) {
			causes.add(new Status(IStatus.ERROR, ID, code, temp.getMessage()==null?temp.toString() + ": <no message>":temp.toString(), temp) );
			temp = temp.getCause();
		}
        String msg = "<No message>";
        if(t!=null && t.getMessage()!=null) {
            msg = t.toString();
        }
        
        if(causes.isEmpty()) {
        	return new Status(IStatus.ERROR, ID, code, msg, t);
        } else {
        	return new MultiStatus(ID, code,(IStatus[]) causes.toArray(new IStatus[causes.size()]), msg, t);
        }
		
	}
	
	public static IStatus throwableToStatus(Throwable t) {		
		return throwableToStatus(t, 150);
	}
	
	public void logErrorMessage(String message, Throwable t[]) {
		IStatus[] children = new IStatus[t.length];
		for (int i = 0; i < t.length; i++) {
			Throwable throwable = t[i];
			children[i] = throwableToStatus(throwable);
		}
		
		IStatus s = new MultiStatus(ID, 150,children, message, null);
		log(s);
	}

	/**
	 * Logs an internal error with the specified throwable
	 * 
	 * @param e the exception to be logged
	 */	
	public void log(Throwable e) {
		log(new Status(IStatus.ERROR, ID, 150, "Hibernate Console Internal Error", e) );  //$NON-NLS-1$
	}

	void readStateFrom(File f) {
		try {
			EclipseConsoleConfigurationPreferences[] preferences = EclipseConsoleConfigurationPreferences.readStateFrom(f);
			
			for (int i = 0; i < preferences.length; i++) {
				ConsoleConfigurationPreferences prefs = preferences[i];
				KnownConfigurations.getInstance().addConfiguration(new EclipseConsoleConfiguration(prefs), false); // TODO: do we need to broadcast every time when reading state ?
			}
		} catch(HibernateConsoleRuntimeException hcr) {
			logErrorMessage("Error while reading console configuration", hcr);
		}
	
	}

	

	void writeStateTo(File f) {
		//System.out.println("write state to" + f);
		KnownConfigurations.getInstance().writeStateTo(f);
	}

	/**
	 * 
	 */
	public void showError(Shell shell, String message, Throwable he) {
		logErrorMessage(message, he);
		IStatus warning = throwableToStatus(he);
		 	   ErrorDialog.openError(shell, 
		 	      "Hibernate Console", message, warning);		
	}
	
	public void showError(Shell shell, String message, IStatus s) {
		log(s);
	 	   ErrorDialog.openError(shell, 
	 	      "Hibernate Console", message, s);				
	}

	public void openCriteriaEditor(String consoleName, String criteria) {
		 try {
		        final IWorkbenchWindow activeWorkbenchWindow =
		            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		        
		        
		        CriteriaEditorStorage storage = new CriteriaEditorStorage(consoleName, criteria==null?"":criteria);		        
		        
		        final CriteriaEditorInput editorInput = new CriteriaEditorInput(storage);
		        page.openEditor(editorInput, "org.hibernate.eclipse.criteriaeditor.CriteriaEditor", true);
		        //page.openEditor(editorInput, "org.eclipse.jdt.ui.CompilationUnitEditor", true);
		    } catch (PartInitException ex) {
		    	logErrorMessage("Could not open Criteria editor for console:" + consoleName, ex);
		    }
	}
	
	public void openScratchHQLEditor(String consoleName, String hql) {
		 try {
		        final IWorkbenchWindow activeWorkbenchWindow =
		            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		        
		        
		        HQLEditorStorage storage = new HQLEditorStorage(consoleName, hql==null?"":hql);		        
		        
		        final HQLEditorInput editorInput = new HQLEditorInput(storage);
		            page.openEditor(editorInput, "org.hibernate.eclipse.hqleditor.HQLEditor", true);
		    } catch (PartInitException ex) {
		        logErrorMessage("Could not open HQL editor for console:" + consoleName, ex);
		    }
	}

	/*public ConsoleConfiguration getLastUsedConfiguration() {
		String lastUsedName = getDefault().getPreferenceStore().getString(HibernateConsolePlugin.LAST_USED_CONFIGURATION_PREFERENCE);
		
		ConsoleConfiguration lastUsed = (lastUsedName == null || lastUsedName.trim().length()==0) 
				? null 
				: KnownConfigurations.getInstance().find(lastUsedName);
	    
	    if(lastUsed==null && KnownConfigurations.getInstance().getConfigurations().length==1) {
	        lastUsed = KnownConfigurations.getInstance().getConfigurations()[0];
	    }
	    
		return lastUsed;
	}*/
	
	/*public void setLastUsedConfiguration(ConsoleConfiguration lastUsed) {
		String name;
		if(lastUsed==null) {
			name = "";
		} else {
			name = lastUsed.getName();
		}
		
		HibernateConsolePlugin.getDefault().getPreferenceStore().setValue(
				LAST_USED_CONFIGURATION_PREFERENCE, name );
	}*/
	
	/**
	 * Convenience method for showing an error dialog 
	 * @param shell a valid shell or null
	 * @param exception the exception to be report
	 * @param title the title to be displayed
	 * @param flags customizing attributes for the error handling
	 * @return IStatus the status that was displayed to the user
	 */
	public static IStatus openError(Shell providedShell, String title, String message, Throwable exception, int flags) {
		// Unwrap InvocationTargetExceptions
		if (exception instanceof InvocationTargetException) {
			Throwable target = ((InvocationTargetException)exception).getTargetException();
			// re-throw any runtime exceptions or errors so they can be handled by the workbench
			if (target instanceof RuntimeException) {
				throw (RuntimeException)target;
			}
			if (target instanceof Error) {
				throw (Error)target;
			} 
			return openError(providedShell, title, message, target, flags);
		}
		
		// Determine the status to be displayed (and possibly logged)
		IStatus status = null;
		if (exception instanceof CoreException) {
			status = ((CoreException)exception).getStatus();
		} else if (exception != null) {
			status = new MultiStatus(ID, IStatus.ERROR, new IStatus[] { throwableToStatus(exception.getCause())}, exception.toString(), exception); //$NON-NLS-1$
		}
				
		// Check for multi-status with only one child
		/*if (status.isMultiStatus() && status.getChildren().length == 1) {
			status = status.getChildren()[0]; // makes Status.ERROR - Status.OK
		}*/
		
		if (status.isOK()) {
			return status;
		}
		
		// Create a runnable that will display the error status
		final String displayTitle = title;
		final String displayMessage = message;
		final IStatus displayStatus = status;
		final IOpenableInShell openable = new IOpenableInShell() {
			public void open(Shell shell) {
				if (displayStatus.getSeverity() == IStatus.INFO && !displayStatus.isMultiStatus()) {
					MessageDialog.openInformation(shell, "Information", displayStatus.getMessage()); //$NON-NLS-1$
				} else {
					ErrorDialog.openError(shell, displayTitle, displayMessage, displayStatus);
				}
			}
		};
		openDialog(providedShell, openable, flags);
		
		// return the status we display
		return status;
	}
	
	/**
	 * Open the dialog code provided in the IOpenableInShell, ensuring that 
	 * the provided whll is valid. This method will provide a shell to the
	 * IOpenableInShell if one is not provided to the method.
	 * 
	 * @param providedShell
	 * @param openable
	 * @param flags
	 */
	public static void openDialog(Shell providedShell, final IOpenableInShell openable, int flags) {
		// If no shell was provided, try to get one from the active window
		if (providedShell == null) {
			IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				providedShell = window.getShell();
				// sync-exec when we do this just in case
				flags = flags | PERFORM_SYNC_EXEC;
			}
		}
		
		// Create a runnable that will display the error status
		final Shell shell = providedShell;
		Runnable outerRunnable = new Runnable() {
			public void run() {
				Shell displayShell;
				if (shell == null) {
					Display display = Display.getCurrent();
					displayShell = new Shell(display);
				} else {
					displayShell = shell;
				}
				openable.open(displayShell);
				if (shell == null) {
					displayShell.dispose();
				}
			}
		};
		
		// Execute the above runnable as determined by the parameters
		if (shell == null || (flags & PERFORM_SYNC_EXEC) > 0) {
			Display display;
			if (shell == null) {
				display = Display.getCurrent();
				if (display == null) {
					display = Display.getDefault();
				}
			} else {
				display = shell.getDisplay();
			}
			display.syncExec(outerRunnable);
		} else {
			outerRunnable.run();
		}
	}

	/**
	 * Interface that allows a shell to be passed to an open method. The
	 * provided shell can be used without sync-execing, etc.
	 */
	public interface IOpenableInShell {
		public void open(Shell shell);
	}
	
	public static Shell getShell() {
		if (getActiveWorkbenchWindow() != null) {
			return getActiveWorkbenchWindow().getShell();
		}
		return null;
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public JavaTextTools getJavaTextTools() {
		if (javaTextTools == null) {
			javaTextTools = new JavaTextTools(PreferenceConstants.getPreferenceStore());
		}
		
		return javaTextTools;
	}

	
	
}
