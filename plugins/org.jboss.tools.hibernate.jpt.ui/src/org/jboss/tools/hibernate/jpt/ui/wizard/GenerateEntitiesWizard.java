/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jpt.jpa.ui.JptJpaUiMessages;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.context.AddGeneratedClassesJob;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;
import org.jboss.tools.hibernate.jpt.ui.internal.platform.HibernateJpaPlatformUi;

/**
 * @author Dmitry Geraskov
 *
 */
public class GenerateEntitiesWizard extends Wizard {


	private HibernateJpaProject jpaProject;
	
	private GenerateEntitiesWizardPage initPage;
	
	public GenerateEntitiesWizard( HibernateJpaProject jpaProject, IStructuredSelection selection) {
		super();
		this.jpaProject = jpaProject;
		this.setWindowTitle( JptJpaUiMessages.GENERATE_ENTITIES_WIZARD_GENERATE_ENTITIES);
	}
	
	@Override
	public void addPages() {
		super.addPages();
		initPage = new GenerateEntitiesWizardPage(jpaProject);
		addPage(initPage);
	}
	
	@Override
	public boolean performFinish() {
		String projectName = jpaProject.getName();
		ILaunchConfigurationWorkingCopy wc = HibernateJpaPlatformUi.createDefaultLaunchConfig(projectName);
		if (wc != null) {
			// SHOULD PRESENT THE CONFIGURATION!!!
			String concoleConfigurationName = initPage.getConfigurationName();			
			wc.setAttribute(HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, concoleConfigurationName);

			wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, initPage.getOutputDir());

			wc.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, initPage.getPackageName());
			wc.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, true);

			wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
			wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);
			wc.setAttribute("hibernate.temp.use_jdbc_metadata_defaults", true); //$NON-NLS-1$

			wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + '.' + HibernateJpaPlatformUi.exporter_id + ".extension_id",  //$NON-NLS-1$
						HibernateLaunchConstants.ATTR_PREFIX + "hbm2java"); //$NON-NLS-1$
			try {
				NewJavaFilesListener rcl = new NewJavaFilesListener(jpaProject.getJavaProject());;
				if (!jpaProject.discoversAnnotatedClasses()){
					ResourcesPlugin.getWorkspace().addResourceChangeListener(rcl);
				}				
				wc.launch(ILaunchManager.RUN_MODE, null);
				if (!jpaProject.discoversAnnotatedClasses()){
					ResourcesPlugin.getWorkspace().removeResourceChangeListener(rcl);
					if (rcl.generatedJavaFiles.size() > 0){
						AddGeneratedClassesJob job = new AddGeneratedClassesJob(jpaProject, rcl.generatedJavaFiles);
						job.schedule();
					}
				}
			} catch (CoreException e) {
				HibernateJptUIPlugin.logException(e);
			} finally{
				if (initPage.isTemporaryConfiguration()){
					KnownConfigurations.getInstance().removeConfiguration(KnownConfigurations.getInstance().find(concoleConfigurationName), false);				
				}
			}
		}
		return true;
	}

}

class NewJavaFilesListener implements IResourceChangeListener {

	List<IResource> generatedJavaFiles = new LinkedList<IResource>();
	private List<IPackageFragmentRoot> sourceRoots = new LinkedList<IPackageFragmentRoot>();

	/**
	 * @param projectName
	 */
	public NewJavaFilesListener(IJavaProject project) {
		try {
			IPackageFragmentRoot[] allPackageFragmentRoots = project.getAllPackageFragmentRoots();
			for (int j = 0; j < allPackageFragmentRoots.length; j++) {
				if (!allPackageFragmentRoots[j].isArchive()){
					sourceRoots.add(allPackageFragmentRoots[j]);
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		if (delta != null){
			try {
				delta.accept(new IResourceDeltaVisitor() {
					
					@Override
					public boolean visit(IResourceDelta delta) throws CoreException {
						IResource resource = delta.getResource();
						int type = resource.getType();
						//we also have to consider projects we dependent on here!!!
						if (type == IResource.ROOT
								|| type == IResource.PROJECT
								|| type == IResource.FOLDER)
							return true;

						if (resource instanceof IFile && delta.getKind() == IResourceDelta.ADDED) {
							// see if this is it
							IFile candidate = (IFile) resource;
							if (isJavaSourceFile(candidate)) {
								generatedJavaFiles.add(candidate);
							}
						}
						return false;
					}

					private boolean isJavaSourceFile(IResource candidate) {
						if (candidate.getName().endsWith(".java")){ //$NON-NLS-1$
							while (candidate.getParent() != null){
								for (IPackageFragmentRoot root : sourceRoots) {
									if (root.getResource().equals(candidate.getParent())){
										return true;
									}
								}
								candidate = candidate.getParent();
							}
						}
						return false;
					}
				});
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
}
