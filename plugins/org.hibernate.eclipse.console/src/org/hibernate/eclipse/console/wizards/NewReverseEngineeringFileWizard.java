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
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * Creates a new reveng.xml
 */

public class NewReverseEngineeringFileWizard extends Wizard implements INewWizard {
	private ISelection selection;
    private WizardNewFileCreationPage cPage;
	private TableFilterWizardPage tableFilterWizardPage;

	/**
	 * Constructor for NewConfigurationWizard.
	 */
	public NewReverseEngineeringFileWizard() {
		super();
        setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
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
        cPage.setTitle( "Create Hibernate Reverse Engineering file (reveng.xml)" );
        cPage.setDescription( "Create a new hibernate.reveng.xml." );
        cPage.setFileName("hibernate.reveng.xml");
        addPage( cPage );  
        
        tableFilterWizardPage = new TableFilterWizardPage( "revengtable" );
		addPage( tableFilterWizardPage );
        
	}
    
    

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final IFile file = cPage.createNewFile();
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(file, monitor);
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
		final IFile file, IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + file.getName(), 2);		
		try {
			InputStream stream = openContentStream();
			if (file.exists() ) {
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
	
	private InputStream openContentStream() {
        StringWriter sw = new StringWriter();
        sw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
        		"<!DOCTYPE hibernate-reverse-engineering PUBLIC \"-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd\" >\r\n" + 
        		"\r\n" + 
        		"<hibernate-reverse-engineering>\r\n");
        TableFilter[] filters = (TableFilter[]) tableFilterWizardPage.getTableFilters().toArray(new TableFilter[0]);
        for (int i = 0; i < filters.length; i++) {
			TableFilter filter = filters[i];
			sw.write("  <table-filter");
			if(!".*".equals(filter.getMatchCatalog())) {
				sw.write(" match-catalog=\"" + filter.getMatchCatalog() + "\"");
			}
			if(!".*".equals(filter.getMatchSchema())) {
				sw.write(" match-schema=\"" + filter.getMatchSchema() + "\"");
			}
			sw.write(" match-name=\"" + filter.getMatchName() + "\"");
			if(filter.getExclude().booleanValue()) {
				sw.write(" exclude=\"" + filter.getExclude().booleanValue() + "\"");
			}
			sw.write("/>\r\n");
		}
    	
        sw.write("</hibernate-reverse-engineering>");
		try {
            return new ByteArrayInputStream(sw.toString().getBytes("UTF-8") );
        } catch (UnsupportedEncodingException uec) {
            HibernateConsolePlugin.getDefault().logErrorMessage("Problems converting to UTF-8", uec);
            return new ByteArrayInputStream(sw.toString().getBytes() );
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