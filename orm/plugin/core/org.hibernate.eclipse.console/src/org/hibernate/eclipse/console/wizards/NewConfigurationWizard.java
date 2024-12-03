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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
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
import org.hibernate.eclipse.console.actions.AddConfigurationAction;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IService;
import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;

/**
 * Creates a new hibernate.cfg.xml
 */
public class NewConfigurationWizard extends Wizard implements INewWizard {
	private NewConfigurationWizardPage connectionInfoPage;
	private ISelection selection;
    private WizardNewFileCreationPage cPage;
	private ConsoleConfigurationWizardPage confPage;

	/**
	 * Constructor for NewConfigurationWizard.
	 */
	public NewConfigurationWizard() {
		super();
        setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
		setNeedsProgressMonitor(true);
	}

    /** extended to update status messages on first show **/
    static class ExtendedWizardNewFileCreationPage extends WizardNewFileCreationPage {

        public ExtendedWizardNewFileCreationPage(String pageName, IStructuredSelection selection) {
            super(pageName, selection);
            setAllowExistingResources(false);
        }

        boolean firstTime = true;
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            if(firstTime) {
                validatePage();
                firstTime = false;
            }
        }
        
        @Override
        protected boolean validatePage() {
        	if (super.validatePage()){
        		String fileName = getFileName();
        		if (!fileName.endsWith(".cfg.xml") ) { //$NON-NLS-1$
        			setMessage(HibernateConsoleMessages.NewConfigurationWizardPage_file_extension_should_be_cfgxml, WARNING);
                } else if (!fileName.equals(HibernateConsoleMessages.NewConfigurationWizardPage_filefile_name)){
                	setMessage(NLS.bind(HibernateConsoleMessages.NewConfigurationWizardPage_fileshould_pass_configuration,
                			fileName), WARNING);
                }
        		IPath path = getContainerFullPath().append(getFileName());
        		IContainer container = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
        		
        		try {
					if (container != null && container.getProject().hasNature(JavaCore.NATURE_ID)) {
						IJavaProject proj = JavaCore.create(container.getProject());
						IPath projRelPath = container.getProjectRelativePath();
						IPackageFragmentRoot[] roots;

						roots = proj.getAllPackageFragmentRoots();
						boolean found = false;
						for (IPackageFragmentRoot root : roots) {
							if (root.isArchive()) continue;
							if (root.getResource() != null && root.getResource().getProjectRelativePath().isPrefixOf(projRelPath)){
								if (!root.getResource().getProjectRelativePath().equals(projRelPath)){
				                	//this is not "src" folder
									setMessage(NLS.bind(HibernateConsoleMessages.NewConfigurationWizardPage_fileshould_pass_configuration,
				                			fileName), WARNING);
								}
								found = true;
								break;
							}
						}
						if (!found){
							setMessage(HibernateConsoleMessages.NewConfigurationWizardPage_fileoutside_classpath, WARNING);
						}										
					}
				} catch (CoreException e) {
					HibernateConsolePlugin.getDefault().log(e);
				}
        		return true;
        	}
        	return false;
        }
    }
    
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
        cPage = new ExtendedWizardNewFileCreationPage( "Ccfgxml", (IStructuredSelection) selection ) { //$NON-NLS-1$
        	protected InputStream getInitialContents() {
        		final Properties props = new Properties();
                putIfNotNull(props, "hibernate.session_factory_name", connectionInfoPage.getSessionFactoryName() ); //$NON-NLS-1$
                putIfNotNull(props, "hibernate.dialect", connectionInfoPage.getDialect() ); //$NON-NLS-1$
                putIfNotNull(props, "hibernate.connection.driver_class", connectionInfoPage.getDriver() ); //$NON-NLS-1$
                putIfNotNull(props, "hibernate.connection.url", connectionInfoPage.getConnectionURL() ); //$NON-NLS-1$
                putIfNotNull(props, "hibernate.connection.username", connectionInfoPage.getUsername() ); //$NON-NLS-1$
                putIfNotNull(props, "hibernate.connection.password", connectionInfoPage.getPassword() ); //$NON-NLS-1$
                putIfNotNull(props, "hibernate.default_catalog", connectionInfoPage.getDefaultCatalog() ); //$NON-NLS-1$
                putIfNotNull(props, "hibernate.default_schema", connectionInfoPage.getDefaultSchema() ); //$NON-NLS-1$
        		return openContentStream(props);
        	}
        };
        cPage.setTitle( HibernateConsoleMessages.NewConfigurationWizard_create_hibernate_cfg_file );
        cPage.setDescription( HibernateConsoleMessages.NewConfigurationWizard_create_new_hibernate_cfg_xml );
        cPage.setFileName("hibernate.cfg.xml"); //$NON-NLS-1$
        cPage.setFileExtension("cfg.xml"); //$NON-NLS-1$
        addPage( cPage );


        connectionInfoPage = new NewConfigurationWizardPage(selection, cPage);
		addPage(connectionInfoPage);

		confPage = new ConsoleConfigurationWizardPage(selection);
		addPage(confPage);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
	    connectionInfoPage.saveHibernateVersion();
		
		final Properties props = new Properties();
		putIfNotNull(props, "hibernate.session_factory_name", connectionInfoPage.getSessionFactoryName() ); //$NON-NLS-1$
		putIfNotNull(props, "hibernate.dialect", connectionInfoPage.getDialect() ); //$NON-NLS-1$
		putIfNotNull(props, "hibernate.default_catalog", connectionInfoPage.getDefaultCatalog() ); //$NON-NLS-1$
        putIfNotNull(props, "hibernate.default_schema", connectionInfoPage.getDefaultSchema() ); //$NON-NLS-1$
        putIfNotNull(props, "hibernate.connection.driver_class", connectionInfoPage.getDriver() ); //$NON-NLS-1$
        putIfNotNull(props, "hibernate.connection.url", connectionInfoPage.getConnectionURL() ); //$NON-NLS-1$
        putIfNotNull(props, "hibernate.connection.username", connectionInfoPage.getUsername() ); //$NON-NLS-1$
        putIfNotNull(props, "hibernate.connection.password", connectionInfoPage.getPassword() ); //$NON-NLS-1$
		
        final IFile file = cPage.createNewFile();

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					createHibernateCfgXml(file, props, monitor);
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
			HibernateConsolePlugin.getDefault().showError(getShell(), HibernateConsoleMessages.NewConfigurationWizard_error, realException);
			return false;
		}
		
		try {
			if (connectionInfoPage.isCreateConsoleConfigurationEnabled()) {
				confPage.performFinish();
			} else {
				AddConfigurationAction.deleteTemporaryLaunchConfigurations();
	        }
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().showError(getShell(), HibernateConsoleMessages.AddConfigurationAction_problem_add_console_config,  ce);
		}
        
		return true;
	}

	public boolean performCancel() {
		try {
			confPage.performCancel();
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().showError(getShell(), HibernateConsoleMessages.AddConfigurationAction_problem_add_console_config,  ce);
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
	private void createHibernateCfgXml(
		final IFile file, Properties props, IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask(HibernateConsoleMessages.NewConfigurationWizard_creating + file.getName(), 2);
		InputStream stream = openContentStream(props);
		if (file.exists() ) {
            file.setContents(stream, true, true, monitor);
		} else {
			file.create(stream, true, monitor);
		}
		try {
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName(HibernateConsoleMessages.NewConfigurationWizard_open_file_for_editing);
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
        IService service = RuntimeServiceManager.getInstance().findService(connectionInfoPage.getHibernateVersion()); 
        IExporter hce = service.createCfgExporter();
 		hce.setCustomProperties(props);
		hce.setOutput(stringWriter);
        hce.start();
        try {
            return new ByteArrayInputStream(stringWriter.toString().getBytes("UTF-8") ); //$NON-NLS-1$
        } catch (UnsupportedEncodingException uec) {
            HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.NewConfigurationWizard_problems_converting_to_utf8, uec);
            return new ByteArrayInputStream(stringWriter.toString().getBytes() );
        }
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		if (!selection.isEmpty()){
			IJavaProject jproj = null; 
			if (selection.getFirstElement() instanceof IJavaProject) {
				jproj = (IJavaProject) selection.getFirstElement();
			}
			if (selection.getFirstElement() instanceof IProject){
				try {
					if (((IProject)selection.getFirstElement()).getNature(JavaCore.NATURE_ID) != null) {
						jproj = JavaCore.create((IProject)selection.getFirstElement());
					}
				} catch (CoreException e) {
					HibernateConsolePlugin.getDefault().log(e);
				}
			}
			if (jproj != null){
				IPackageFragmentRoot[] roots;
				try {
					roots = jproj.getAllPackageFragmentRoots();
					if (roots.length > 0){
						this.selection = new StructuredSelection(roots[0]);
					}
				} catch (JavaModelException e) {
					HibernateConsolePlugin.getDefault().log(e);
				}				
			};			
		}		
	}

	public IWizardPage getNextPage(IWizardPage page) {
		if(page==connectionInfoPage) {
			if(!connectionInfoPage.isCreateConsoleConfigurationEnabled()) {
				return null;
			}
			confPage.setConfigurationFilePath(cPage.getContainerFullPath().append(cPage.getFileName()));
			confPage.getHibernateVersionCombo().select(connectionInfoPage.getHibernateVersionComboSelectionIndex());
			confPage.getHibernateVersionCombo().setEnabled(false);
			confPage.setPreviousPage(page);
		}
		return super.getNextPage( page );
	}

	public boolean canFinish() {
		if(!connectionInfoPage.isCreateConsoleConfigurationEnabled()) {
			return connectionInfoPage.isPageComplete();
		}
		return super.canFinish();
	}
	
}
