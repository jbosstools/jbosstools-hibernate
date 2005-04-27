package org.hibernate.eclipse.console.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.hibernate.cfg.Environment;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.tool.hbm2x.HibernateConfigurationExporter;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "cfg.xml". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class NewConfigurationWizard extends Wizard implements INewWizard {
	private NewConfigurationWizardPage page;
	private ISelection selection;
    private WizardNewFileCreationPage cPage;

	/**
	 * Constructor for NewConfigurationWizard.
	 */
	public NewConfigurationWizard() {
		super();
        setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD));
		setNeedsProgressMonitor(true);
	}

    /** extended to update status messages on first show **/
    static class ExtendedWizardNewFileCreationPage extends WizardNewFileCreationPage {

        public ExtendedWizardNewFileCreationPage(String pageName, IStructuredSelection selection) {
            super(pageName, selection);
        }

        boolean firstTime = true;
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            if(firstTime) {
                validatePage();
                firstTime = false;
            }
        }
    }
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
        cPage =
        new ExtendedWizardNewFileCreationPage( "Ccfgxml", (IStructuredSelection) selection );
        cPage.setTitle( "Create Hibernate Configuration file (cfg.xml)" );
        cPage.setDescription( "Create a new hibernate.cfg.xml." );
        cPage.setFileName("hibernate.cfg.xml");
        addPage( cPage );        
        
        page = new NewConfigurationWizardPage(selection, cPage);
		addPage(page);
	}
    
    

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final Properties props = new Properties();
        putIfNotNull(props, Environment.SESSION_FACTORY_NAME, page.getSessionFactoryName());
        putIfNotNull(props, Environment.DIALECT, page.getDialect());
        putIfNotNull(props, Environment.DRIVER, page.getDriver());
        putIfNotNull(props, Environment.URL, page.getConnectionURL());
        putIfNotNull(props, Environment.USER, page.getUsername());
        putIfNotNull(props, Environment.PASS, page.getPassword());
        final IFile file = cPage.createNewFile();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(file, props, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			HibernateConsolePlugin.getDefault().showError(getShell(), "Error", realException);
			return false;
		}
		return true;
	}
	
	/**
     * @param props
     * @param dialect
     * @param dialect2
     */
    private void putIfNotNull(Properties props, String key, String value) {
        if(value!=null) {
            props.put(key,value);
        }
    }

    /**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
     * @param file 
     * @param props 
	 */

	private void doFinish(
		final IFile file, Properties props, IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + file.getName(), 2);		
		try {
			InputStream stream = openContentStream(props);
			if (file.exists()) {
                file.setContents(stream, true, true, monitor);                
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 * @throws UnsupportedEncodingException 
	 */

	private InputStream openContentStream(Properties props) {
        StringWriter stringWriter = new StringWriter();
        HibernateConfigurationExporter hce = new HibernateConfigurationExporter(null,props, stringWriter);
        hce.init();
        hce.finish();
        try {
            return new ByteArrayInputStream(stringWriter.toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException uec) {
            HibernateConsolePlugin.getDefault().logErrorMessage("Problems converting to UTF-8", uec);
            return new ByteArrayInputStream(stringWriter.toString().getBytes());
        }
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