/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementInfo;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;

/**
 * @author Dmitry Geraskov
 *
 */
@SuppressWarnings("restriction")
public class NewHibernateMappingFileWizard extends Wizard implements INewWizard, IPageChangingListener{

	/**
	 * Selected compilation units for startup processing,
	 * result of processing selection
	 */
	private Set<ICompilationUnit> selectionCU = null;

	private Map<IJavaProject, Collection<EntityInfo>> project_infos = new HashMap<IJavaProject, Collection<EntityInfo>>();

	private IStructuredSelection selection;
	
	private NewHibernateMappingElementsSelectionPage2 page0 = null;
	
	private WizardNewFileCreationPage cPage;
	
	private NewHibernateMappingFilePage page2 = null;	

	public NewHibernateMappingFileWizard(){
		setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
		setNeedsProgressMonitor(true);
		setWindowTitle(JdtUiMessages.NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file);
	}

	@Override
	public void addPages() {
		super.addPages();
		
		if (selection == null) {
			selection = new StructuredSelection();
		}
		
		page0 = new NewHibernateMappingElementsSelectionPage2(JdtUiMessages.NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file, selection);
		page0.setDescription(JdtUiMessages.NewHibernateMappingElementsSelectionPage2_description);
		addPage(page0);
		
		cPage = new WizardNewFileCreationPage( "Ccfgxml", selection ); //$NON-NLS-1$
	    cPage.setTitle( JdtUiMessages.NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file );
	    cPage.setDescription( JdtUiMessages.NewHibernateMappingFileWizard_create_empty_xml_mapping_file );
	    cPage.setFileName("hibernate.hbm.xml"); //$NON-NLS-1$
	    addPage( cPage );	    
	    
		page2 = new NewHibernateMappingFilePage(false);
		page2.setTitle(JdtUiMessages.NewHibernateMappingFilePage_hibernate_xml_mapping_file);
		page2.setMessage(JdtUiMessages.NewHibernateMappingFilePage_this_wizard_creates, IMessageProvider.WARNING);
	
		addPage(page2);		
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == page0 && !page0.getSelection().isEmpty()) return page2;
		if (page == cPage) return null;
		return super.getNextPage(page);
	}
	
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		if (getContainer() instanceof WizardDialog) {
			((WizardDialog) getContainer()).removePageChangingListener(this);
		}
		super.setContainer(wizardContainer);
		if (getContainer() instanceof WizardDialog) {
			((WizardDialog) getContainer()).addPageChangingListener(this);
		}
    }

	public void handlePageChanging(PageChangingEvent event) {
		if (event.getTargetPage() == page2){
			updateCompilationUnits();
			page2.setInput(project_infos);
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Set<IJavaElement> filteredElements = new HashSet<IJavaElement>();
		Object[] elements = selection.toArray();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof JavaProject) {
				JavaProject project = (JavaProject) elements[i];
				try {
					IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
					for (int j = 0; j < roots.length; j++) {
						if (!roots[j].isArchive()){
							IJavaElement[] rootChildren = roots[j].getChildren();
							for (int k = 0; k < rootChildren.length; k++) {
								if (rootChildren[k] instanceof IPackageFragment) {
									IPackageFragment pkg = (IPackageFragment) rootChildren[k];
									try {
										if (pkg.containsJavaResources())
											filteredElements.add(rootChildren[k]);
									} catch (JavaModelException e1) {
										e1.printStackTrace();
									}
								}
							}														
						}
					} 
				} catch (JavaModelException e) {
					e.printStackTrace();
				}				
			} else if (elements[i] instanceof IPackageFragmentRoot) {
				IPackageFragmentRoot root = (IPackageFragmentRoot)elements[i];
				if (!root.isArchive()){							
					try {
						filteredElements.addAll(Arrays.asList((root.getChildren())));
					} catch (JavaModelException e) {
						e.printStackTrace();
					}							
			}
			} else if (elements[i] instanceof ICompilationUnit) {
				ICompilationUnit cu = (ICompilationUnit)elements[i];
				IType[] types;
				try {
					types = cu.getTypes();
					//remove java extension.
					String typeName = cu.getElementName().substring(0, cu.getElementName().length() - 5);
					for (int j = 0; j < types.length; j++) {
						if (types[j].getElementName().equals(typeName)){
							filteredElements.add(types[j]);
							break;
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
					
			} else if (elements[i] instanceof IJavaElement) {
				filteredElements.add((IJavaElement) elements[i]);
			}
		}
		this.selection = new StructuredSelection(filteredElements.toArray());
	}

	@Override
	public boolean performFinish() {
		if (page0.getSelection().isEmpty()){
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
		} else {
			updateCompilationUnits();
			Map<IJavaProject, Configuration> configs = createConfigurations();
			for (Entry<IJavaProject, Configuration> entry : configs.entrySet()) {
				Configuration config = entry.getValue();
				HibernateMappingGlobalSettings hmgs = new HibernateMappingGlobalSettings();

				
				try {
				    IResource container = entry.getKey().getPackageFragmentRoots().length > 0
					? entry.getKey().getPackageFragmentRoots()[0].getResource()
							: entry.getKey().getResource();
					
					IPath temp_path = entry.getKey().getProject().getLocation()
						.append(".settings").append("org.hibernate_tools.temp");
					
					IFolder temp_folder = entry.getKey().getProject().getFolder(new Path(".settings/org.hibernate_tools.temp"));
					
					HibernateMappingExporter hce = new HibernateMappingExporter(config,
							temp_folder.getLocation().toFile());

					hce.setGlobalSettings(hmgs);
					//hce.setForEach("entity");
					//hce.setFilePattern(file.getName());
					try {
						hce.start();
					} catch (Exception e){
						e.getCause().printStackTrace();
					}
					container.refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (JavaModelException e1) {
					HibernateConsolePlugin.getDefault().log(e1);
				} catch (CoreException e) {
					HibernateConsolePlugin.getDefault().log(e);
				}
			}
			return true;
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

	private InputStream openContentStream() {
        StringWriter sw = new StringWriter();
        sw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +  //$NON-NLS-1$
        		"<!DOCTYPE hibernate-mapping PUBLIC \"-//Hibernate/Hibernate Mapping DTD 3.0//EN\" \"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd\" >\r\n" +  //$NON-NLS-1$
        		"<hibernate-mapping>\r\n</hibernate-mapping>"); //$NON-NLS-1$
		try {
            return new ByteArrayInputStream(sw.toString().getBytes("UTF-8") ); //$NON-NLS-1$
        } catch (UnsupportedEncodingException uec) {
            HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.NewConfigurationWizard_problems_converting_to_utf8, uec);
            return new ByteArrayInputStream(sw.toString().getBytes() );
        }
	}
	
	protected void initEntitiesInfo(){
		if (selectionCU.size() == 0) {
			return;
		}
		AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
		Iterator<ICompilationUnit> it = selectionCU.iterator();

		Map<IJavaProject, Set<ICompilationUnit>> mapJP_CUSet =
			new HashMap<IJavaProject, Set<ICompilationUnit>>();
		//separate by parent project
		while (it.hasNext()) {
			ICompilationUnit cu = it.next();
			Set<ICompilationUnit> set = mapJP_CUSet.get(cu.getJavaProject());
			if (set == null) {
				set = new HashSet<ICompilationUnit>();
				mapJP_CUSet.put(cu.getJavaProject(), set);
			}
			set.add(cu);
		}
		Iterator<Map.Entry<IJavaProject, Set<ICompilationUnit>>>
		mapIt = mapJP_CUSet.entrySet().iterator();
		while (mapIt.hasNext()) {
			Map.Entry<IJavaProject, Set<ICompilationUnit>>
			entry = mapIt.next();
			IJavaProject javaProject = entry.getKey();
			Iterator<ICompilationUnit> setIt = entry.getValue().iterator();
			collector.initCollector();
			while (setIt.hasNext()) {
				ICompilationUnit icu = setIt.next();
				collector.collect(icu);
			}
			collector.resolveRelations();
			project_infos.put(javaProject, collector.getMapCUs_Info().values());

		}
	}

	protected void processJavaElements(Object obj) {
		try {
			if (obj instanceof ICompilationUnit) {
				ICompilationUnit cu = (ICompilationUnit) obj;
				selectionCU.add(cu);
			} else if (obj instanceof JavaProject) {
				JavaProject javaProject = (JavaProject) obj;
				IPackageFragmentRoot[] pfr = javaProject.getAllPackageFragmentRoots();
				for (IPackageFragmentRoot element : pfr) {
					processJavaElements(element);
				}
			} else if (obj instanceof PackageFragment) {
				PackageFragment packageFragment = (PackageFragment) obj;
				ICompilationUnit[] cus = packageFragment.getCompilationUnits();
				for (ICompilationUnit cu : cus) {
					selectionCU.add(cu);
				}
			} else if (obj instanceof PackageFragmentRoot) {
				JavaElement javaElement = (JavaElement) obj;
				JavaElementInfo javaElementInfo = (JavaElementInfo) javaElement.getElementInfo();
				IJavaElement[] je = javaElementInfo.getChildren();
				for (IJavaElement element : je) {
					processJavaElements(element);
				}
			} else if (obj instanceof JavaElement) {
				JavaElement javaElement = (JavaElement) obj;
				ICompilationUnit cu = javaElement.getCompilationUnit();
				selectionCU.add(cu);
			}
		} catch (JavaModelException e) {
			// just ignore it!
			//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
		}
	}
	

	protected Map<IJavaProject, Configuration> createConfigurations() {
		ConfigurationActor actor = new ConfigurationActor(selectionCU);
		Map<IJavaProject, Configuration> configs = actor.createConfigurations();
		return configs;
	}
	
	protected void updateCompilationUnits(){
		Assert.isNotNull(page0.getSelection(), JdtUiMessages.NewHibernateMappingFileWizard_selection_cant_be_empty);
		if ((selectionCU == null) || !page0.getSelection().equals(selection)) {
			selectionCU = new HashSet<ICompilationUnit>();
			project_infos.clear();
			selection = page0.getSelection();
				try {
					getContainer().run(false, false, new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask(JdtUiMessages.NewHibernateMappingFileWizard_look_for_dependent_cu, selection.size() + 1);
							Iterator<?> it = selection.iterator();
							int done = 1;
							while (it.hasNext()) {
								Object obj = it.next();
								processJavaElements(obj);
								monitor.worked(done++);
							}
							initEntitiesInfo();
							monitor.worked(1);
							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					HibernateConsolePlugin.getDefault().log(e);
				} catch (InterruptedException e) {
					HibernateConsolePlugin.getDefault().log(e);
				}
		}
	}
	
	@Override
	public boolean canFinish() {
		return !page0.getSelection().isEmpty() || cPage.isPageComplete();
	}
}
