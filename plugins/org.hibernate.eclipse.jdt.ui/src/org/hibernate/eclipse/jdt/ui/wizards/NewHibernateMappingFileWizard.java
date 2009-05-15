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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
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
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.HibernateMappingGlobalSettings;

/**
 * @author Dmitry Geraskov
 *
 */
public class NewHibernateMappingFileWizard extends Wizard implements INewWizard, IPageChangingListener{

	/**
	 * Selected compilation units for startup processing,
	 * result of processing selection
	 */
	private Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();

	private Map<IJavaProject, Collection<EntityInfo>> project_infos = new HashMap<IJavaProject, Collection<EntityInfo>>();

	private IStructuredSelection selection;

	private NewHibernateMappingFilePage page2 = null;

	private NewHibernateMappingElementsSelectionPage page1 = null;

	public NewHibernateMappingFileWizard(){
		setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		super.addPages();
		page1 = new NewHibernateMappingElementsSelectionPage(selection);
		page1.setTitle( HibernateConsoleMessages.NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file );
		page1.setDescription( HibernateConsoleMessages.NewHibernateMappingFileWizard_create_new_xml_mapping_file );
		addPage(page1);
		page2 = new NewHibernateMappingFilePage();
		addPage(page2);
		if (getContainer() instanceof WizardDialog) {
			((WizardDialog) getContainer()).addPageChangingListener(this);
		} else {
			throw new IllegalArgumentException("Must use WizardDialog implementation as WizardContainer");
		}
	}

	public void handlePageChanging(PageChangingEvent event) {
		if (event.getTargetPage() == page2){
			selection = page1.getSelection();
			try {
				getContainer().run(false, false, new IRunnableWithProgress(){

					public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
						monitor.beginTask("Find dependent compilation units", selection.size() + 1);
						Iterator it = selection.iterator();
						int done = 1;
						while (it.hasNext()) {
							Object obj = it.next();
							processJavaElements(obj);
							monitor.worked(done++);
							Thread.currentThread();
							Thread.sleep(1000);
						}
						initEntitiesInfo();
						monitor.worked(1);
						monitor.done();
					}
				});
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			page2.setInput(project_infos);
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	@Override
	public boolean performFinish() {
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
			//I don't check here if any non abstract class selected
			project_infos.put(javaProject, collector.getMapCUs_Info().values());

		}
	}

	protected void processJavaElements(Object obj) {
		if (obj instanceof ICompilationUnit) {
			ICompilationUnit cu = (ICompilationUnit)obj;
			selectionCU.add(cu);
		}
		else if (obj instanceof File) {
			File file = (File)obj;
			if (file != null && file.getProject() != null) {
				IJavaProject javaProject = JavaCore.create(file.getProject());
				ICompilationUnit[] cus = Utils.findCompilationUnits(javaProject,
						file.getFullPath());
				if (cus != null) {
					for (ICompilationUnit cu : cus) {
						selectionCU.add(cu);
					}
				}
			}
		}
		else if (obj instanceof JavaProject) {
			JavaProject javaProject = (JavaProject)obj;
			IPackageFragmentRoot[] pfr = null;
			try {
				pfr = javaProject.getAllPackageFragmentRoots();
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (pfr != null) {
				for (IPackageFragmentRoot element : pfr) {
					processJavaElements(element);
				}
			}
		}
		else if (obj instanceof PackageFragment) {
			PackageFragment packageFragment = (PackageFragment)obj;
			ICompilationUnit[] cus = null;
			try {
				cus = packageFragment.getCompilationUnits();
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (cus != null) {
				for (ICompilationUnit cu : cus) {
					selectionCU.add(cu);
				}
			}
		}
		else if (obj instanceof PackageFragmentRoot) {
			JavaElement javaElement = (JavaElement)obj;
			JavaElementInfo javaElementInfo = null;
			try {
				javaElementInfo = (JavaElementInfo)javaElement.getElementInfo();
			} catch (JavaModelException e) {
				// just ignore it!
				//HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
			}
			if (javaElementInfo != null) {
				IJavaElement[] je = javaElementInfo.getChildren();
				for (IJavaElement element : je) {
					processJavaElements(element);
				}
			}
		}
		else if (obj instanceof JavaElement) {
			JavaElement javaElement = (JavaElement)obj;
			ICompilationUnit cu = javaElement.getCompilationUnit();
			selectionCU.add(cu);
		}
	}
	

	protected Map<IJavaProject, Configuration> createConfigurations() {
		ConfigurationActor actor = new ConfigurationActor(selectionCU);
		Map<IJavaProject, Configuration> configs = actor.createConfigurations();
		return configs;
	}

}
