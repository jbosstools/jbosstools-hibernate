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
package org.jboss.tools.hibernate.jpt.core.internal;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jpt.common.core.resource.xml.JptXmlResource;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.internal.resource.persistence.PersistenceXmlResourceProvider;
import org.eclipse.jpt.jpa.core.resource.persistence.PersistenceFactory;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlProperty;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent.Type;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.hibernate.console.ConnectionProfileUtil;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;
import org.hibernate.eclipse.nature.HibernateNature;
import org.hibernate.eclipse.utils.HibernateEclipseUtils;
import org.jboss.tools.hibernate.runtime.spi.IService;

/**
 * @author Dmitry Geraskov
 *
 */
public class JPAPostInstallFasetListener implements IFacetedProjectListener {

	public void handleEvent(IFacetedProjectEvent event) {
		if (event.getType() == Type.POST_INSTALL){
			IProject project = event.getProject().getProject();
			IProjectFacetActionEvent pEvent = (IProjectFacetActionEvent)event;
			if (pEvent.getProjectFacet().getId().equals(JpaProject.FACET_ID)) {
				String platformId = HibernateEclipseUtils.getJpaPlatformID(project);
				if (platformId != null && 
						(platformId.equals(HibernateJpaPlatform.HIBERNATE_PLATFORM_ID) || 
								platformId.equals(HibernateJpaPlatform.HIBERNATE2_0_PLATFORM_ID) ||
								platformId.equals(HibernateJpaPlatform.HIBERNATE2_1_PLATFORM_ID) ||
								platformId.equals(HibernateJpaPlatform.HIBERNATE2_2_PLATFORM_ID))) {
					if (checkPreConditions(project)) {
						exportConnectionProfilePropertiesToPersistenceXml(project);
						buildConsoleConfiguration(project, platformId);
					}
				}
			}
		}
	}

	private void exportConnectionProfilePropertiesToPersistenceXml(IProject project) {
		PersistenceXmlResourceProvider defaultXmlResourceProvider = PersistenceXmlResourceProvider.getDefaultXmlResourceProvider(project);
		final JptXmlResource resource = defaultXmlResourceProvider.getXmlResource();
		Properties propsToAdd = getConnectionProperties(project);
		if (propsToAdd.isEmpty() || resource == null) return;
		
		XmlPersistence persistence = (XmlPersistence) resource.getRootObject();					
		
		if (persistence.getPersistenceUnits().size() > 0) {
			XmlPersistenceUnit persistenceUnit = persistence.getPersistenceUnits().get(0);
			if (persistenceUnit.getProperties() == null) {
				persistenceUnit.setProperties(PersistenceFactory.eINSTANCE.createXmlProperties());
				for (Entry<Object, Object> entry : propsToAdd.entrySet()) {
					XmlProperty prop = PersistenceFactory.eINSTANCE.createXmlProperty();
					prop.setName((String)entry.getKey());
					prop.setValue((String)entry.getValue());
					persistenceUnit.getProperties().getProperties().add(prop);
				}
			}
		}
		resource.save();
	}
	
	public Properties getConnectionProperties(IProject project){
		IJavaProject javaProject = JavaCore.create(project);
		IService service = null;
		if (javaProject != null) {
			HibernateNature hibNat = HibernateNature.getHibernateNature(javaProject);
			if (hibNat != null) {
				ConsoleConfiguration cc = hibNat.getDefaultConsoleConfiguration();
				if (cc != null) {
					service = cc.getHibernateExtension().getHibernateService();
				}
			}
		}
		String cpName = HibernateEclipseUtils.getConnectionProfileName(project);
		if (cpName != null && service != null){
			return ConnectionProfileUtil.getHibernateConnectionProperties(
					service, ProfileManager.getInstance().getProfileByName(cpName));
		}
		return new Properties();
	}

	/**
	 * 
	 * @param project
	 * @return true if need to create new ConsoleConfiguration, false - otherwise.
	 */
	protected boolean checkPreConditions(IProject project){
		try {
			ILaunchConfiguration lc = getLaunchConfiguration(project);
			if (lc != null && lc.exists()){
				ProjectUtils.toggleHibernateOnProject(project, true, lc.getName());
				return false;
			}
		} catch (CoreException e) {
			HibernateJptPlugin.logException(e);
		}
		return true;
	}

	private ILaunchConfiguration getLaunchConfiguration(IProject project) throws CoreException{
		List<ILaunchConfiguration> configs = new ArrayList<ILaunchConfiguration>();
		ILaunchConfiguration[] lcs = LaunchHelper.findHibernateLaunchConfigs();
		for (int i = 0; i < lcs.length; i++){
			ILaunchConfiguration lc = lcs[i];
			if (project.getName().equals(
					lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null))){//lc uses this project
				if (project.getName().equals(lc.getName())) return lc;
				configs.add(lc);
			}
		}
		//select best launch configuration "projectName (1)"
		Pattern p = Pattern.compile(project.getName() + " \\(\\d+\\)"); //$NON-NLS-1$
		for (int i = 0; i < configs.size(); i++) {
			ILaunchConfiguration lc = configs.get(i);
			if (p.matcher(lc.getName()).matches()) return lc;
		}
		return configs.size() > 0 ? configs.get(0) : null;
	}

	protected void buildConsoleConfiguration(IProject project, String platformId){
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType lct = LaunchHelper.getHibernateLaunchConfigsType();
		String launchName = lm.generateLaunchConfigurationName(project.getName());
		ILaunchConfigurationWorkingCopy wc;
		try {
			wc = lct.newInstance(null, launchName);
			wc.setAttribute(LaunchConfiguration.ATTR_MAPPED_RESOURCE_PATHS,
					Collections.singletonList(new Path(project.getName()).makeAbsolute().toString()));
			wc.setAttribute(LaunchConfiguration.ATTR_MAPPED_RESOURCE_TYPES, Collections.singletonList(Integer.toString(IResource.PROJECT)));
			wc.setAttribute(IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, ConfigurationMode.JPA.toString());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true );
			wc.setAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, (List<String>)null);
			wc.setAttribute(IConsoleConfigurationLaunchConstants.USE_JPA_PROJECT_PROFILE, Boolean.toString(true));
			String hibernateVersion = lookupHibernateVersion(project);
			if  (HibernateJpaPlatform.HIBERNATE2_1_PLATFORM_ID.equals(platformId) || HibernateJpaPlatform.HIBERNATE2_2_PLATFORM_ID.equals(platformId)) {
				wc.setAttribute(IConsoleConfigurationLaunchConstants.PERSISTENCE_UNIT_NAME, getPersistenceUnitName(project));
			}
			if (hibernateVersion != null) {
				wc.setAttribute(IConsoleConfigurationLaunchConstants.HIBERNATE_VERSION, hibernateVersion);
			} else {
				if (HibernateJpaPlatform.HIBERNATE2_2_PLATFORM_ID.equals(platformId)) {
					wc.setAttribute(IConsoleConfigurationLaunchConstants.HIBERNATE_VERSION, "5.2"); //$NON-NLS-1$
				} else if (HibernateJpaPlatform.HIBERNATE2_1_PLATFORM_ID.equals(platformId)) {
					wc.setAttribute(IConsoleConfigurationLaunchConstants.HIBERNATE_VERSION, "4.3"); //$NON-NLS-1$
				} else if (HibernateJpaPlatform.HIBERNATE2_0_PLATFORM_ID.equals(platformId)) {
					wc.setAttribute(IConsoleConfigurationLaunchConstants.HIBERNATE_VERSION, "4.0"); //$NON-NLS-1$
				} else {
					wc.setAttribute(IConsoleConfigurationLaunchConstants.HIBERNATE_VERSION, "3.6"); //$NON-NLS-1$
				}
			}

			wc.doSave();
			ProjectUtils.toggleHibernateOnProject(project, true, launchName);
		} catch (CoreException e) {
			HibernateJptPlugin.logException(e);
		}
	}
	
	private String getPersistenceUnitName(IProject project) {
		String result = null;
		PersistenceXmlResourceProvider defaultXmlResourceProvider = PersistenceXmlResourceProvider.getDefaultXmlResourceProvider(project);
		final JptXmlResource resource = defaultXmlResourceProvider.getXmlResource();
		XmlPersistence persistence = (XmlPersistence) resource.getRootObject();							
		if (persistence.getPersistenceUnits().size() > 0) {
			XmlPersistenceUnit persistenceUnit = persistence.getPersistenceUnits().get(0);
			result = persistenceUnit.getName();
		}
		return result;
	}
	
	private String lookupHibernateVersion(IProject project) {
		String result = null;
		try {
			IJavaProject javaProject = JavaCore.create(project);
			IType type = javaProject.findType("org.hibernate.Version"); //$NON-NLS-1$
			if (type != null) {
				IJavaElement element = type.getPackageFragment();
				IPackageFragmentRoot fragmentRoot = null;
				while (!(element instanceof IPackageFragmentRoot)) {
					element = element.getParent();
				}
				fragmentRoot = (IPackageFragmentRoot)element; 
				URL url = fragmentRoot.getResolvedClasspathEntry().getPath().toFile().toURI().toURL();
				URLClassLoader loader = new URLClassLoader(new URL[] { url });
				Class<?> versionClass = loader.loadClass("org.hibernate.Version"); //$NON-NLS-1$
				Method getVersionString = versionClass.getMethod("getVersionString", new Class<?>[] {}); //$NON-NLS-1$
				result = (String)getVersionString.invoke(null, new Object[] {});
				int endIndex = result.indexOf('.', result.indexOf('.') + 1);
				result = result.substring(0, endIndex);
				loader.close();
			}
		} catch (Exception e) {
			HibernateJptPlugin.logException(e);
		}
		return result;
	}

}
