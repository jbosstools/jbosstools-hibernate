package org.hibernate.eclipse.console.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
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
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.utils.xpl.SelectionHelper;
import org.hibernate.util.StringHelper;

public class NewHibernateMappingFileWizard extends Wizard implements INewWizard {
	private NewHibernateMappingFilePage mappingFileInfoPage;
	private ISelection selection;
    private WizardNewFileCreationPage cPage;
	
	
	/**
	 * Constructor for NewConfigurationWizard.
	 */
	public NewHibernateMappingFileWizard() {
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
        new ExtendedWizardNewFileCreationPage( "Chbmxml", (IStructuredSelection) selection );
        cPage.setTitle( "Create Hibernate XML Mapping file (hbm.xml)" );
        cPage.setDescription( "Create a new XML Mapping file." );
        IType initialJavaElement = SelectionHelper.getClassFromElement(SelectionHelper.getInitialJavaElement(selection));
		if(initialJavaElement!=null) {
			cPage.setFileName(initialJavaElement.getElementName() + ".hbm.xml");
		} else {
			cPage.setFileName("NewMapping.hbm.xml");
		}
        addPage( cPage );        
        
        
        mappingFileInfoPage = new NewHibernateMappingFilePage(selection, cPage);
		addPage(mappingFileInfoPage);
						
	}
    
    

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final IFile file = cPage.createNewFile();
		final String classToMapText = mappingFileInfoPage.getClassToMapText();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					createFile(file, classToMapText, monitor);
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
			HibernateConsolePlugin.getDefault().log(realException);
			return false;
		}
		return true;
	}
	

    /**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
     * @param file 
     * @param props 
	 */

	private void createFile(
		final IFile file, String classToMapText, IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + file.getName(), 2);		
		try {
			InputStream stream = openContentStream(classToMapText);
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
	
	/**
	 * We will initialize file contents with a sample text.
	 */
	
	private InputStream openContentStream(String classToMapText) {		
		
		String classname = null;
		String packagename = null;
		
		if(StringHelper.isNotEmpty(classToMapText)) {
			classname = StringHelper.unqualify(classToMapText);
			packagename = StringHelper.qualifier(classToMapText);
		}
		String contents =
			"<?xml version=\"1.0\"?>\n" + 
			"<!DOCTYPE hibernate-mapping PUBLIC\n" + 
			"	\"-//Hibernate/Hibernate Mapping DTD 3.0//EN\"\n" + 
			"	\"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\">\n" + 
			"<hibernate-mapping";
		
		if(StringHelper.isNotEmpty(packagename)) {
			contents +=" package=\"" + packagename + "\">";
		} else {
			contents +=">\n";
		}
		
		if(StringHelper.isNotEmpty(classname)) {
			contents +="\n  <class name=\"" + classname + "\">\n" +
					"  </class>";
		} 
		
		contents += "\n</hibernate-mapping>";
		
		return new ByteArrayInputStream(contents.getBytes());
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