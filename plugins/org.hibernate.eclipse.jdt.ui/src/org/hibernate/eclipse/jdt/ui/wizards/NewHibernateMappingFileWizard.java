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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementInfo;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.dialogs.IPageChangingListener;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
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

	//private NewHibernateMappingElementsSelectionPage page1 = null;
	
	private NewHibernateMappingFilePage page2 = null;

	public NewHibernateMappingFileWizard(){
		setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
		setNeedsProgressMonitor(true);
		setWindowTitle(HibernateConsoleMessages.NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file);
	}

	@Override
	public void addPages() {
		super.addPages();
		
		page0 = new NewHibernateMappingElementsSelectionPage2(HibernateConsoleMessages.NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file, selection);
		addPage(page0);
		
		//page1 = new NewHibernateMappingElementsSelectionPage(selection);
		//page1.setTitle( HibernateConsoleMessages.NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file );
		//page1.setDescription( HibernateConsoleMessages.NewHibernateMappingFileWizard_create_new_xml_mapping_file );
		//addPage(page1);
		page2 = new NewHibernateMappingFilePage();
		addPage(page2);
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
								if (rootChildren[k] instanceof IParent && ((IParent)rootChildren[k]).hasChildren()){
									filteredElements.add(rootChildren[k]);	
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
		updateCompilationUnits();
		Map<IJavaProject, Configuration> configs = createConfigurations();
		for (Entry<IJavaProject, Configuration> entry : configs.entrySet()) {
			Configuration config = entry.getValue();
			HibernateMappingGlobalSettings hmgs = new HibernateMappingGlobalSettings();

			IResource container;
			try {
				container = entry.getKey().getPackageFragmentRoots().length > 0
				? entry.getKey().getPackageFragmentRoots()[0].getResource()
						: entry.getKey().getResource();

				HibernateMappingExporter hce = new HibernateMappingExporter(config,
						container.getLocation().toFile());

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
			collector.initCollector(javaProject);
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
		Assert.isNotNull(page0.getSelection(), HibernateConsoleMessages.NewHibernateMappingFileWizard_selection_cant_be_empty);
		if ((selectionCU == null) || !page0.getSelection().equals(selection)) {
			selectionCU = new HashSet<ICompilationUnit>();
			project_infos.clear();
			selection = page0.getSelection();
				try {
					getContainer().run(false, false, new IRunnableWithProgress() {

						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask(HibernateConsoleMessages.NewHibernateMappingFileWizard_finding_dependent_cu, selection.size() + 1);
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
}
