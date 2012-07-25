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
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jpt.jpa.core.JpaFacet;
import org.eclipse.jpt.jpa.core.JptJpaCorePlugin;
import org.eclipse.jpt.jpa.core.internal.resource.persistence.PersistenceXmlResourceProvider;
import org.eclipse.jpt.jpa.core.resource.persistence.PersistenceFactory;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistence;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.jpt.jpa.core.resource.persistence.XmlProperty;
import org.eclipse.jpt.jpa.core.resource.xml.JpaXmlResource;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent.Type;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.hibernate.console.ConnectionProfileUtil;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;

/**
 * @author Dmitry Geraskov
 *
 */
public class JPAPostInstallFasetListener implements IFacetedProjectListener {

	public void handleEvent(IFacetedProjectEvent event) {
		if (event.getType() == Type.POST_INSTALL){
			IProject project = event.getProject().getProject();
			IProjectFacetActionEvent pEvent = (IProjectFacetActionEvent)event;
			if (pEvent.getProjectFacet().getId().equals(JpaFacet.ID)
					&& (HibernateJpaPlatform.HIBERNATE_PLATFORM_ID.equals(JptJpaCorePlugin.getJpaPlatformId(project))
							|| HibernateJpaPlatform.HIBERNATE2_0_PLATFORM_ID.equals(JptJpaCorePlugin.getJpaPlatformId(project)))){
				if (checkPreConditions(project)){
					exportConnectionProfilePropertiesToPersistenceXml(project);
					buildConsoleConfiguration(project);
				}
			}
		}
	}

	private void exportConnectionProfilePropertiesToPersistenceXml(IProject project) {
		PersistenceXmlResourceProvider defaultXmlResourceProvider = PersistenceXmlResourceProvider.getDefaultXmlResourceProvider(project);
		final JpaXmlResource resource = defaultXmlResourceProvider.getXmlResource();
		Properties propsToAdd = getConnectionProperties(project);
		if (propsToAdd.isEmpty() || resource == null) return;
		
		XmlPersistence persistence = (XmlPersistence) resource.getRootObject();					
		XmlPersistenceUnit persistenceUnit;
	
		if (persistence.getPersistenceUnits().size() > 0) {
			persistenceUnit = persistence.getPersistenceUnits().get(0);
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
		String cpName = JptJpaCorePlugin.getConnectionProfileName(project);
		if (cpName != null){
			return ConnectionProfileUtil.getHibernateConnectionProperties(
					ProfileManager.getInstance().getProfileByName(cpName));
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

	protected void buildConsoleConfiguration(IProject project){
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

			wc.doSave();
			ProjectUtils.toggleHibernateOnProject(project, true, launchName);
		} catch (CoreException e) {
			HibernateJptPlugin.logException(e);
		}
	}

}
