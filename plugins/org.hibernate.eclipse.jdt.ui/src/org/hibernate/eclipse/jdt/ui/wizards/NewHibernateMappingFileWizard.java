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

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaElementInfo;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ImageConstants;
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
public class NewHibernateMappingFileWizard extends Wizard implements INewWizard {
	
	/**
	 * Selected compilation units for startup processing,
	 * result of processing selection
	 */
	private Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();
	
	private Map<IJavaProject, Collection<EntityInfo>> project_infos = new HashMap<IJavaProject, Collection<EntityInfo>>();

	
	public NewHibernateMappingFileWizard(){
		setDefaultPageImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.NEW_WIZARD) );
	}
	
	@Override
	public void addPages() {
		super.addPages();
		addPage(new NewHibernateMappingFilePage(project_infos));
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		updateSelectedItems(selection);
		initEntitiesInfo();
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
		if (selectionCU.size() == 0) return;
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
	
	private Map<IJavaProject, Configuration> createConfigurations() {
		ConfigurationActor actor = new ConfigurationActor(selectionCU);
		Map<IJavaProject, Configuration> configs = actor.createConfigurations();
		return configs;
	}	

	protected void updateSelectedItems(ISelection sel) {
		if (sel instanceof TextSelection) {
			String fullyQualifiedName = ""; //$NON-NLS-1$
			IDocument fDocument = null;
			SynchronizableDocument sDocument = null;
			org.eclipse.jdt.core.dom.CompilationUnit resultCU = null;
			Class clazz = sel.getClass();
			Field fd = null;
			try {
				fd = clazz.getDeclaredField("fDocument"); //$NON-NLS-1$
			} catch (NoSuchFieldException e) {
				// just ignore it!
			}
			if (fd != null) {
				try {
					fd.setAccessible(true);
					fDocument = (IDocument)fd.get(sel);
					if (fDocument instanceof SynchronizableDocument) {
						sDocument = (SynchronizableDocument)fDocument;
					}
					if (sDocument != null) {
						ASTParser parser = ASTParser.newParser(AST.JLS3);
						parser.setSource(sDocument.get().toCharArray());
						parser.setResolveBindings(false);
						resultCU = (org.eclipse.jdt.core.dom.CompilationUnit) parser.createAST(null);
					}
					if (resultCU != null && resultCU.types().size() > 0 ) {
						if (resultCU.getPackage() != null) {
							fullyQualifiedName = resultCU.getPackage().getName().getFullyQualifiedName() + "."; //$NON-NLS-1$
						}
						else {
							fullyQualifiedName = ""; //$NON-NLS-1$
						}
						Object tmp = resultCU.types().get(0);
						if (tmp instanceof AbstractTypeDeclaration) {
							fullyQualifiedName += ((AbstractTypeDeclaration)tmp).getName();
						}
						if (!(tmp instanceof TypeDeclaration)) {
							// ignore EnumDeclaration & AnnotationTypeDeclaration
							fullyQualifiedName = ""; //$NON-NLS-1$
						}
					}
				} catch (IllegalArgumentException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("IllegalArgumentException: ", e); //$NON-NLS-1$
				} catch (IllegalAccessException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("IllegalAccessException: ", e); //$NON-NLS-1$
				} catch (SecurityException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("SecurityException: ", e); //$NON-NLS-1$
				}
			}
			if (fullyQualifiedName.length() > 0) {
				ICompilationUnit cu = Utils.findCompilationUnit(fullyQualifiedName);
				selectionCU.add(cu);
			}
		}
		else if (sel instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection)sel;
			Iterator it = treeSelection.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				processJavaElements(obj);
			}
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
					for (int i = 0; i < cus.length; i++) {
						selectionCU.add(cus[i]);
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
				for (int i = 0; i < pfr.length; i++) {
					processJavaElements(pfr[i]);
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
				for (int i = 0; i < cus.length; i++) {
					selectionCU.add(cus[i]);
				}
			}
		}
		else if (obj instanceof PackageFragmentRoot) {
			PackageFragmentRoot packageFragmentRoot = (PackageFragmentRoot)obj;
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
				for (int i = 0; i < je.length; i++) {
					processJavaElements(je[i]);
				}
			}
		}
		else if (obj instanceof JavaElement) {
			JavaElement javaElement = (JavaElement)obj;
			ICompilationUnit cu = javaElement.getCompilationUnit();
			selectionCU.add(cu);
		}
	}
	
}
