/*******************************************************************************
 * Copyright (c) 2015-2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.importer;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jpt.common.core.resource.xml.JptXmlResource;
import org.eclipse.jpt.jpa.core.JpaPlatform;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetDataModelProperties;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetInstallDataModelProperties;
import org.eclipse.jpt.jpa.core.internal.facet.JpaFacetInstallDataModelProvider;
import org.eclipse.jpt.jpa.core.internal.resource.persistence.PersistenceXmlResourceProvider;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.common.project.facet.core.internal.JavaFacetUtil;
import org.eclipse.jst.common.project.facet.core.libprov.ILibraryProvider;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryProviderFramework;
import org.eclipse.ui.wizards.datatransfer.ProjectConfigurator;
import org.eclipse.ui.wizards.datatransfer.RecursiveFileFinder;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;

public class JPAProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canConfigure(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		try {
			RecursiveFileFinder finder = new RecursiveFileFinder("persistence.xml", ignoredDirectories); //$NON-NLS-1$
			project.accept(finder);
			return finder.getFile() != null;
		} catch (CoreException ex) {
			return false;
		}
	}

	@Override
	public void configure(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		try {
			IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);

			IProjectFacet JPA_FACET = ProjectFacetsManager.getProjectFacet("jpt.jpa"); //$NON-NLS-1$
			if (!facetedProject.hasProjectFacet(JPA_FACET)) {
				Set<Action> actions = new LinkedHashSet<>(2, 1);

				IProjectFacetVersion javaFv = JavaFacet.FACET.getVersion(JavaFacetUtil.getCompilerLevel(project));
				if(!facetedProject.hasProjectFacet(JavaFacet.FACET)) {
					actions.add(new IFacetedProject.Action(IFacetedProject.Action.Type.INSTALL, javaFv, null));
				} else if(!facetedProject.hasProjectFacet(javaFv)) {
					actions.add(new IFacetedProject.Action(IFacetedProject.Action.Type.VERSION_CHANGE, javaFv, null));
				}

				RecursiveFileFinder finder = new RecursiveFileFinder("persistence.xml", ignoredDirectories); //$NON-NLS-1$
				project.accept(finder);
				PersistenceXmlResourceProvider provider = PersistenceXmlResourceProvider.getXmlResourceProvider(finder.getFile());

				JptXmlResource jpaXmlResource = provider.getXmlResource();

				IProjectFacetVersion version = null;
				if (jpaXmlResource.getVersion() != null) {
					version = JpaProject.FACET.getVersion(jpaXmlResource.getVersion());
				}
				if (version == null) {
					version = JpaProject.FACET.getLatestVersion();
				}

				JpaPlatform platform = null; // use default
				// TODO improve platform detection

				LibraryInstallDelegate libraryDelegate = new LibraryInstallDelegate(facetedProject, version);
				ILibraryProvider libraryProvider = LibraryProviderFramework.getProvider("jpa-no-op-library-provider"); //$NON-NLS-1$
				libraryDelegate.setLibraryProvider(libraryProvider);

				IDataModel dm = DataModelFactory.createDataModel(new JpaFacetInstallDataModelProvider());
				dm.setProperty(IFacetDataModelProperties.FACET_VERSION_STR, version.getVersionString());
				dm.setProperty(JpaFacetDataModelProperties.PLATFORM, platform);
				dm.setProperty(JpaFacetInstallDataModelProperties.DISCOVER_ANNOTATED_CLASSES, true);
				dm.setProperty(JpaFacetInstallDataModelProperties.LIBRARY_PROVIDER_DELEGATE, libraryDelegate);
				actions.add(new IFacetedProject.Action(IFacetedProject.Action.Type.INSTALL, version, dm));

				facetedProject.modify(actions, monitor);
			}
		} catch (Exception ex) {
			HibernateJptUIPlugin.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					HibernateJptUIPlugin.PLUGIN_ID,
					ex.getMessage(),
					ex));
		}
	}

	@Override
	public boolean shouldBeAnEclipseProject(IContainer container, IProgressMonitor monitor) {
		return false; // TODO can we make sure a given dir is actually a JPA project
	}

	@Override
	public Set<IFolder> getFoldersToIgnore(IProject project, IProgressMonitor monitor) {
		return null;
	}

	@Override
	public Set<File> findConfigurableLocations(File root, IProgressMonitor monitor) {
		// No easy way to deduce project roots from jee files...
		return Collections.emptySet();
	}

}
