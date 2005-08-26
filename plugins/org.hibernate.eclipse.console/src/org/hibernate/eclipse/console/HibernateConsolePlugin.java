package org.hibernate.eclipse.console;

import java.io.File;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
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
import org.hibernate.eclipse.EclipseLogger;
import org.hibernate.eclipse.console.workbench.ConfigurationAdapterFactory;
import org.hibernate.eclipse.hqleditor.HQLEditorInput;
import org.hibernate.eclipse.hqleditor.HQLEditorStorage;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class HibernateConsolePlugin extends AbstractUIPlugin {
	
	public static final String ID = "org.hibernate.eclipse.console";
	
	static public final String LAST_USED_CONFIGURATION_PREFERENCE = "lastusedconfig";
	
	//The shared instance.
	private static HibernateConsolePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private EclipseLogger logger = new EclipseLogger(ID);
	
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
		log(new MultiStatus(HibernateConsolePlugin.ID, IStatus.ERROR , new IStatus[] { throwableToStatus(t) }, message, t) );
	}
	
	static IStatus throwableToStatus(Throwable t) {
		ArrayList causes = new ArrayList();
		Throwable temp = t;
		while(temp!=null && temp.getCause()!=temp) {
			causes.add(new Status(IStatus.ERROR, ID, 150, temp.getMessage()==null?"<no message>":temp.getMessage(), temp) );
			temp = temp.getCause();
		}
        String msg = "<No message>";
        if(t!=null && t.getMessage()!=null) {
            msg = t.getMessage();
        }
		
		return new MultiStatus(ID, IStatus.ERROR,(IStatus[]) causes.toArray(new IStatus[causes.size()]), msg, t);
		
	}
	
	public void logErrorMessage(String message, Throwable t[]) {
		IStatus[] children = new IStatus[t.length];
		for (int i = 0; i < t.length; i++) {
			Throwable throwable = t[i];
			children[i] = throwableToStatus(throwable);
		}
		
		IStatus s = new MultiStatus(ID, IStatus.ERROR,children, message, null);
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
		System.out.println("write state to" + f);
		KnownConfigurations.getInstance().writeStateTo(f);
	}

	/**
	 * 
	 */
	public void showError(Shell shell, String message, Throwable he) {
		logErrorMessage(message, he);
		String string = he==null?"<No message>":he.getClass().getName() + ":" + he.getMessage();
		IStatus warning = new Status(IStatus.WARNING, 
		 	      HibernateConsolePlugin.ID, 1, string , he);
		 	   ErrorDialog.openError(shell, 
		 	      "Hibernate Console", message, warning);		
	}
	
	
	public void openScratchHQLEditor(String consoleName, String hql) {
		 try {
		        final IWorkbenchWindow activeWorkbenchWindow =
		            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		        IWorkbenchPage page = activeWorkbenchWindow.getActivePage();
		        
		        
		        HQLEditorStorage storage = new HQLEditorStorage(consoleName, "HQL: " + consoleName, hql==null?"":hql);		        
		        
		        final HQLEditorInput editorInput = new HQLEditorInput(storage);
		            page.openEditor(editorInput, "org.hibernate.eclipse.hqleditor.HQLEditor", true);
		    } catch (PartInitException ex) {
		        ex.printStackTrace();
		    }
	}

	public ConsoleConfiguration getLastUsedConfiguration() {
		String lastUsedName = getDefault().getPreferenceStore().getString(HibernateConsolePlugin.LAST_USED_CONFIGURATION_PREFERENCE);
		
		ConsoleConfiguration lastUsed = (lastUsedName == null || lastUsedName.trim().length()==0) 
				? null 
				: KnownConfigurations.getInstance().find(lastUsedName);
	    
	    if(lastUsed==null && KnownConfigurations.getInstance().getConfigurations().length==1) {
	        lastUsed = KnownConfigurations.getInstance().getConfigurations()[0];
	    }
	    
		return lastUsed;
	}
	
	public void setLastUsedConfiguration(ConsoleConfiguration lastUsed) {
		String name;
		if(lastUsed==null) {
			name = "";
		} else {
			name = lastUsed.getName();
		}
		
		HibernateConsolePlugin.getDefault().getPreferenceStore().setValue(
				LAST_USED_CONFIGURATION_PREFERENCE, name );
	}
	
}
