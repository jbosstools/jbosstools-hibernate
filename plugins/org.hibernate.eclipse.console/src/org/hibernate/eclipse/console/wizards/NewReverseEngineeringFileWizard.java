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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.utils.EclipseImages;

/**
 * Creates a new reveng.xml
 */

public class NewReverseEngineeringFileWizard extends Wizard implements INewWizard {
	private IStructuredSelection selection;
    private WizardNewFileCreationPage cPage;
	private TableFilterWizardPage tableFilterWizardPage;
	private IPath createdFile;
	private String selectedConfiguratonName;

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
		if (selection == null) {
			selection = new StructuredSelection();
		}
	    cPage = new ExtendedWizardNewFileCreationPage( "Ccfgxml", selection ); //$NON-NLS-1$
	    cPage.setTitle( HibernateConsoleMessages.NewReverseEngineeringFileWizard_create_hibernate_reverse_engineering_file );
	    cPage.setDescription( HibernateConsoleMessages.NewReverseEngineeringFileWizard_create_new_hibernate_reveng_xml );
	    cPage.setFileName("hibernate.reveng.xml"); //$NON-NLS-1$
	    addPage( cPage );

	    tableFilterWizardPage = new TableFilterWizardPage( "revengtable", selectedConfiguratonName ); //$NON-NLS-1$
		addPage( tableFilterWizardPage );

	}



	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		createdFile = cPage.getContainerFullPath().append(cPage.getFileName());
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
			HibernateConsolePlugin.getDefault().showError(getShell(), HibernateConsoleMessages.NewReverseEngineeringFileWizard_error, realException);
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
		monitor.beginTask(HibernateConsoleMessages.NewReverseEngineeringFileWizard_creating + file.getName(), 2);
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
		monitor.setTaskName(HibernateConsoleMessages.NewReverseEngineeringFileWizard_opening_file_for_editing);
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
        sw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +  //$NON-NLS-1$
        		"<!DOCTYPE hibernate-reverse-engineering PUBLIC \"-//Hibernate/Hibernate Reverse Engineering DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-reverse-engineering-3.0.dtd\" >\r\n" +  //$NON-NLS-1$
        		"\r\n" +  //$NON-NLS-1$
        		"<hibernate-reverse-engineering>\r\n"); //$NON-NLS-1$
        ITableFilter[] filters = tableFilterWizardPage.getTableFilters();
        for (int i = 0; i < filters.length; i++) {
			ITableFilter filter = filters[i];
			sw.write("  <table-filter"); //$NON-NLS-1$
			if(!".*".equals(filter.getMatchCatalog())) { //$NON-NLS-1$
				sw.write(" match-catalog=\"" + filter.getMatchCatalog() + "\"");  //$NON-NLS-1$//$NON-NLS-2$
			}
			if(!".*".equals(filter.getMatchSchema())) { //$NON-NLS-1$
				sw.write(" match-schema=\"" + filter.getMatchSchema() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			sw.write(" match-name=\"" + filter.getMatchName() + "\"");  //$NON-NLS-1$//$NON-NLS-2$
			if(filter.getExclude().booleanValue()) {
				sw.write(" exclude=\"" + filter.getExclude().booleanValue() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
			}
			sw.write("/>\r\n"); //$NON-NLS-1$
		}

        sw.write("</hibernate-reverse-engineering>"); //$NON-NLS-1$
		try {
            return new ByteArrayInputStream(sw.toString().getBytes("UTF-8") ); //$NON-NLS-1$
        } catch (UnsupportedEncodingException uec) {
            HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.NewReverseEngineeringFileWizard_problems_converting_to_utf8, uec);
            return new ByteArrayInputStream(sw.toString().getBytes() );
        }
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (selection == null) {
			selection = new StructuredSelection();
		}
		this.selection = selection;
	}

	public IPath getCreatedFilePath() {
		return createdFile;
	}



	public void setSelectConfiguration(String configurationName) {
		this.selectedConfiguratonName = configurationName;
	}
}