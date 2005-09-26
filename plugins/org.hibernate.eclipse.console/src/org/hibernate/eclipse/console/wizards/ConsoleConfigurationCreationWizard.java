/*
 * Created on 2004-10-13
 */
package org.hibernate.eclipse.console.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.EclipseConsoleConfiguration;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * @author max
 */
public class ConsoleConfigurationCreationWizard extends Wizard implements
		INewWizard {

	private ConsoleConfigurationWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for SampleNewWizard.
	 */
	public ConsoleConfigurationCreationWizard() {
		super();
        setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new ConsoleConfigurationWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final ConsoleConfigurationWizardPage confPage = this.page;
		return createConsoleConfiguration( getContainer(), confPage );
	}

	static boolean createConsoleConfiguration(IWizardContainer container, final ConsoleConfigurationWizardPage confPage) {
		final String configName = confPage.getConfigurationName();
		final String entityResolver = confPage.getEntityResolverClassName();
		final IPath propertyFile = confPage.getPropertyFilePath();
		final IPath fileName = confPage.getConfigurationFilePath();
		final boolean annotations = confPage.useAnnotations();
		final IPath[] mappings = confPage.getMappingFiles();
		final IPath[] classpaths = confPage.getClassPath();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					createConsoleConfiguration(confPage.getOldConfiguration(), configName, annotations, entityResolver, propertyFile, fileName, mappings, classpaths, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			container.run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();			
			IStatus s = null;
			if(realException instanceof CoreException) {
				s = ( (CoreException)realException).getStatus();
			} else {
				s = new Status(IStatus.ERROR,HibernateConsolePlugin.ID, IStatus.OK, "Probably missing classes or errors with classloading", e);
				
			}
			ErrorDialog.openError(container.getShell(), "Create Conscole Configuration Wizard", "Error while finishing Wizard", s);
			return false;
		}
		return true;
	}
	
	static private void createConsoleConfiguration(
			EclipseConsoleConfiguration oldConfig,
			String configName,
			boolean annotations, String entityResolver, IPath propertyFilename,
			IPath cfgFile, IPath[] mappings, IPath[] classpaths, IProgressMonitor monitor)
		throws CoreException {

		monitor.beginTask("Configuring Hibernate Console" + propertyFilename, IProgressMonitor.UNKNOWN);
								
		ConsoleConfigurationPreferences ccp = new EclipseConsoleConfigurationPreferences(configName, annotations, entityResolver, cfgFile, propertyFilename, mappings, classpaths);
		
		final ConsoleConfiguration cfg = new EclipseConsoleConfiguration(ccp);
			
		if(oldConfig!=null) {
			oldConfig.reset(); // reset it no matter what.
			KnownConfigurations.getInstance().removeConfiguration(oldConfig);
		} 
		KnownConfigurations.getInstance().addConfiguration(cfg, true);
		
		monitor.worked(1);
	} 
	
	
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
