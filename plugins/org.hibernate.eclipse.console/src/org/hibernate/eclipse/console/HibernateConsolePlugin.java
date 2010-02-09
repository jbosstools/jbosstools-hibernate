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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsAdapter;
import org.hibernate.console.KnownConfigurationsListener;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.actions.AddConfigurationAction;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.console.workbench.ConfigurationAdapterFactory;
import org.hibernate.eclipse.criteriaeditor.CriteriaEditorInput;
import org.hibernate.eclipse.criteriaeditor.CriteriaEditorStorage;
import org.hibernate.eclipse.hqleditor.HQLEditorInput;
import org.hibernate.eclipse.hqleditor.HQLEditorStorage;
import org.hibernate.eclipse.launch.ICodeGenerationLaunchConstants;
import org.hibernate.eclipse.logging.PluginLogger;
import org.hibernate.eclipse.logging.xpl.EclipseLogger;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class HibernateConsolePlugin extends AbstractUIPlugin implements PluginLogger {

	public static final String ID = "org.hibernate.eclipse.console"; //$NON-NLS-1$

	static public final String LAST_USED_CONFIGURATION_PREFERENCE = "lastusedconfig"; //$NON-NLS-1$

	public static final int PERFORM_SYNC_EXEC = 1;

	//The shared instance.
	private static HibernateConsolePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private EclipseLogger logger;

	private JavaTextTools javaTextTools;

	private ILaunchConfigurationListener icl;
	private KnownConfigurationsListener kcl;

	/**
	 * The constructor.
	 */
	public HibernateConsolePlugin() {
		super();
		setPlugin(this);
	}



	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		logger=new EclipseLogger(context.getBundle());
		//HibernateConsoleSaveParticipant participant = new HibernateConsoleSaveParticipant();
		//participant.doStart(this);

		IAdapterManager adapterManager = Platform.getAdapterManager();
		ConfigurationAdapterFactory fact =  new ConfigurationAdapterFactory();
		fact.registerAdapters(adapterManager);

		loadExistingConfigurations();

		listenForConfigurations();

	}

	private void listenForConfigurations() {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		kcl = new KnownConfigurationsAdapter() {

			/**
			 * @param root
			 * @param forUpdate - shows whether physical removal necessary
			 */
			public void configurationRemoved(ConsoleConfiguration root, boolean forUpdate) {
				if(!forUpdate) {
					try {
						removeConfiguration(root.getName());
					} catch (CoreException e) {
						logErrorMessage(HibernateConsoleMessages.HibernateConsolePlugin_could_not_delete_launch_config_for + root.getName(), e);
					}
				}
			}

			public void configurationReset(ConsoleConfiguration ccfg) {				
				
			}
		};

		KnownConfigurations.getInstance().addConsoleConfigurationListener(kcl);

		icl = new ILaunchConfigurationListener() {

			boolean isConsoleConfiguration(ILaunchConfiguration configuration) {
				try {
					return configuration.getType().getIdentifier().equals(ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID);
				}
				catch (CoreException e) {
					//HibernateConsolePlugin.getDefault().log( e );
					// ignore since it occurs on delete
				}
				return false;
			}

			public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
				ConsoleConfiguration cfg = KnownConfigurations.getInstance().find( configuration.getName() );
				if(cfg!=null) {
					// file system removal have been made already. 
					KnownConfigurations.getInstance().removeConfiguration( cfg, true );
				}
			}

			public void launchConfigurationChanged(ILaunchConfiguration configuration) {
				if(configuration.isWorkingCopy() || isTemporary(configuration)) {
					return;
				}
				if(isConsoleConfiguration( configuration )) {
					KnownConfigurations instance = KnownConfigurations.getInstance();
					ConsoleConfiguration oldcfg = instance.find( configuration.getName() );
					if(oldcfg!=null) {
						oldcfg.reset(); // reset it no matter what.
					} else { // A new one!
						ConsoleConfigurationPreferences adapter = buildConfigurationPreferences(configuration);
						instance.addConfiguration(new ConsoleConfiguration(adapter), true);
					}
				}
			}

			private ConsoleConfigurationPreferences buildConfigurationPreferences(ILaunchConfiguration configuration) {
				return new EclipseLaunchConsoleConfigurationPreferences(configuration);
			}

			public void launchConfigurationAdded(ILaunchConfiguration configuration) {
				if(isConsoleConfiguration( configuration )) {

					ILaunchConfiguration movedFrom = launchManager.getMovedFrom( configuration );
					if(movedFrom!=null && isConsoleConfiguration( movedFrom )) {
						KnownConfigurations instance = KnownConfigurations.getInstance();
						ConsoleConfiguration oldcfg = instance.find( movedFrom.getName() );
						if(oldcfg!=null) {
							oldcfg.reset(); // reset it no matter what.
							instance.removeConfiguration(oldcfg, false);
						}
					}

					KnownConfigurations instance = KnownConfigurations.getInstance();
					ConsoleConfigurationPreferences adapter = buildConfigurationPreferences(configuration);
					boolean temporary = isTemporary(configuration);

					if(!temporary) {
						instance.addConfiguration(new ConsoleConfiguration(adapter), true);
					}

					}
				}

			private boolean isTemporary(ILaunchConfiguration configuration) {
				boolean temporary = true;
				try {
					temporary = configuration.getAttribute(AddConfigurationAction.TEMPORARY_CONFIG_FLAG, false);
				} catch (CoreException e) {
					HibernateConsolePlugin.getDefault().showError( getShell(), HibernateConsoleMessages.HibernateConsolePlugin_problem_to_get_flag,  e);
				}
				return temporary;
			}
			};
		launchManager.addLaunchConfigurationListener( icl );


	}

	private void stopListeningForConfigurations() {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		launchManager.removeLaunchConfigurationListener( icl );
		KnownConfigurations.getInstance().removeConfigurationListener(kcl);
	}



	private void loadExistingConfigurations() throws CoreException {
		ILaunchConfiguration[] launchConfigurations = LaunchHelper.findHibernateLaunchConfigs();
		for (int i = 0; i < launchConfigurations.length; i++) {
			KnownConfigurations.getInstance().addConfiguration(
					new ConsoleConfiguration(new EclipseLaunchConsoleConfigurationPreferences(launchConfigurations[i])), false );
		}
	}

	/**
	 * Remove configuration from the file system.
	 * @param name
	 * @throws CoreException
	 */
	private void removeConfiguration(String name) throws CoreException {
		ILaunchConfiguration findLaunchConfig = findLaunchConfig(name);
		if (findLaunchConfig != null) {
			findLaunchConfig.delete();
		}
	}

	public ILaunchConfiguration findLaunchConfig(String name)
			throws CoreException {
		return LaunchHelper.findHibernateLaunchConfig(name);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		stopListeningForConfigurations();
		setPlugin(null);
		resourceBundle = null;
	}




	/**
	 * Returns the shared instance.
	 */
	public static HibernateConsolePlugin getDefault() {
		return plugin;
	}

	private static void setPlugin(HibernateConsolePlugin plugin) {
		HibernateConsolePlugin.plugin = plugin;
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
				resourceBundle = ResourceBundle.getBundle("org.hibernate.eclipse.console.HibernateConsolePluginResources"); //$NON-NLS-1$
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
		logMessage(IStatus.ERROR, message, t);
	}

	public void logMessage(int lvl, String message, Throwable t) {
		if(t==null) {
			log(message);
		} else {
			log(new MultiStatus(HibernateConsolePlugin.ID, lvl , new IStatus[] { throwableToStatus(t) }, message, null));
		}
	}

	public static IStatus throwableToStatus(Throwable t, int code) {
		List<IStatus> causes = new ArrayList<IStatus>();
		Throwable temp = t;
		while(temp!=null && temp.getCause()!=temp) {
			causes.add(new Status(IStatus.ERROR, ID, code, temp.getMessage()==null?temp.toString() + HibernateConsoleMessages.HibernateConsolePlugin_no_message_1:temp.toString(), temp) );
			temp = temp.getCause();
		}
        String msg = HibernateConsoleMessages.HibernateConsolePlugin_no_message_2;
        if(t!=null && t.getMessage()!=null) {
            msg = t.toString();
        }

        if(causes.isEmpty()) {
        	return new Status(IStatus.ERROR, ID, code, msg, t);
        } else {
        	return new MultiStatus(ID, code,causes.toArray(new IStatus[causes.size()]), msg, t);
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
			logErrorMessage(HibernateConsoleMessages.HibernateConsolePlugin_error_while_reading_console_config, hcr);
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
		 	      HibernateConsoleMessages.HibernateConsolePlugin_hibernate_console, message, warning);
	}

	public void showError(Shell shell, String message, IStatus s) {
		log(s);
	 	   ErrorDialog.openError(shell,
	 			  HibernateConsoleMessages.HibernateConsolePlugin_hibernate_console, message, s);
	}

	public IEditorPart openCriteriaEditor(String consoleName, String criteria) {
		 try {
			 	IWorkbenchPage page = getActiveWorkbenchWindow().getActivePage();


		        CriteriaEditorStorage storage = new CriteriaEditorStorage(consoleName, criteria==null?"":criteria);		         //$NON-NLS-1$

		        final CriteriaEditorInput editorInput = new CriteriaEditorInput(storage);
		        return page.openEditor(editorInput, "org.hibernate.eclipse.criteriaeditor.CriteriaEditor", true); //$NON-NLS-1$
		        //page.openEditor(editorInput, "org.eclipse.jdt.ui.CompilationUnitEditor", true);
		    } catch (PartInitException ex) {
		    	logErrorMessage(HibernateConsoleMessages.HibernateConsolePlugin_could_not_open_criteria_editor_for_console + consoleName, ex);
		    	return null;
		    }
	}

	public IEditorPart openScratchHQLEditor(String consoleName, String hql) {
		 try {
		        IWorkbenchPage page = getActiveWorkbenchWindow().getActivePage();

		        HQLEditorStorage storage = new HQLEditorStorage(consoleName, hql==null?"":hql);		         //$NON-NLS-1$

		        final HQLEditorInput editorInput = new HQLEditorInput(storage);
		            return page.openEditor(editorInput, "org.hibernate.eclipse.hqleditor.HQLEditor", true); //$NON-NLS-1$
		    } catch (PartInitException ex) {
		        logErrorMessage(HibernateConsoleMessages.HibernateConsolePlugin_could_not_open_hql_editor_for_console + consoleName, ex);
		        return null;
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
			status = new MultiStatus(ID, IStatus.ERROR, new IStatus[] { throwableToStatus(exception.getCause())}, exception.toString(), exception);
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
			javaTextTools = new JavaTextTools(PreferenceConstants.getPreferenceStore()){
				public void setupJavaDocumentPartitioner(IDocument document, String partitioning) {
					IDocumentPartitioner partitioner= createDocumentPartitioner();
					if (document instanceof IDocumentExtension3) {
						IDocumentExtension3 extension3= (IDocumentExtension3) document;
						partitioner.connect(document);
						extension3.setDocumentPartitioner(partitioning, partitioner);
					} else {
						document.setDocumentPartitioner(partitioner);
						partitioner.connect(document);
					}

				}
			};
		}

		return javaTextTools;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(ID, path);
	}

	public void logWarning(HibernateException he) {
		logMessage(IStatus.WARNING, he==null?null:he.getMessage(), he);
	}



}
