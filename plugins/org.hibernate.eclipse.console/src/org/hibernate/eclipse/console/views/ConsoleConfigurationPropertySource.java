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
package org.hibernate.eclipse.console.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;

public class ConsoleConfigurationPropertySource implements IPropertySource {

	private ConsoleConfiguration cfg;

	static List<IPropertyDescriptor> pd;
	static {
		ComboBoxPropertyDescriptor modeDescriptor = new ComboBoxPropertyDescriptor(
			"mode",  //$NON-NLS-1$
			HibernateConsoleMessages.ConsoleConfigurationPropertySource_mode,
			ConfigurationMode.values());
		modeDescriptor.setLabelProvider(new LabelProvider(){
			
			@Override
			public String getText(Object element) {
				if (ConfigurationMode.CORE.toString().equals(element)){
					return "Core"; //$NON-NLS-1$
				}
				if (ConfigurationMode.ANNOTATIONS.toString().equals(element)){
					return "Annotations"; //$NON-NLS-1$
				}
				return super.getText(element);
			}		
		});
		
		List<IPropertyDescriptor> l = new ArrayList<IPropertyDescriptor>();
		l.add(new TextPropertyDescriptor("name", HibernateConsoleMessages.ConsoleConfigurationPropertySource_name)); //$NON-NLS-1$
		l.add(modeDescriptor);
		l.add(new PropertyDescriptor("hibernate.cfg.xml", HibernateConsoleMessages.ConsoleConfigurationPropertySource_config_file)); //$NON-NLS-1$
		l.add(new PropertyDescriptor("hibernate.properties", HibernateConsoleMessages.ConsoleConfigurationPropertySource_properties_file)); //$NON-NLS-1$
		l.add(new PropertyDescriptor("mapping.files", HibernateConsoleMessages.ConsoleConfigurationPropertySource_additional_mapping_files)); //$NON-NLS-1$

		pd = l;
	}

	public ConsoleConfigurationPropertySource(ConsoleConfiguration cfg) {
		this.cfg = cfg;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[pd.size() + 2];
		pd.toArray(propertyDescriptors);
		propertyDescriptors[propertyDescriptors.length - 2] = createProjectDescriptor();
		propertyDescriptors[propertyDescriptors.length - 1] = createConnectionDescriptor();
		return propertyDescriptors;
	}

	public Object getPropertyValue(Object id) {
		try {
		if("name".equals(id)) { //$NON-NLS-1$
			return cfg.getName();
		}		
		// TODO: bring back more eclipse friendly file names
		ConsoleConfigurationPreferences preferences = cfg.getPreferences();
		if ("project".equals(id)){ //$NON-NLS-1$
			try {
				ILaunchConfiguration lc = HibernateConsolePlugin.getDefault().findLaunchConfig(cfg.getName());
				if (lc != null){
					String projectName = lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");  //$NON-NLS-1$
					return Arrays.binarySearch(getSortedProjectNames(), projectName);
				} else {
					HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log(e);
			}
		}
		if("mode".equals(id)) { //$NON-NLS-1$
			String[] values = ConfigurationMode.values();
			String value = preferences.getConfigurationMode().toString();
			for (int i = 0; i < values.length; i++) {
				if (value.equals(values[i])){
					return i;
				}
			}
			return new RuntimeException("Unknown ConsoleConfiguration mode: " + value);	//$NON-NLS-1$
		}
		if("connection".equals(id)) { //$NON-NLS-1$
			try {
				ILaunchConfiguration lc = HibernateConsolePlugin.getDefault().findLaunchConfig(cfg.getName());
				if (lc != null){
					String connectionName = lc.getAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME, (String)null);  
					if (connectionName == null){
						connectionName = lc.getAttribute(IConsoleConfigurationLaunchConstants.USE_JPA_PROJECT_PROFILE, Boolean.FALSE.toString());
						if (Boolean.TRUE.toString().equalsIgnoreCase(connectionName)){
							connectionName = HibernateConsoleMessages.ConnectionProfileCtrl_JPAConfiguredConnection;
						} else {
							connectionName = HibernateConsoleMessages.ConnectionProfileCtrl_HibernateConfiguredConnection;
						}
					}
					String[] values = getConnectionNames();
					for (int i = 0; i < values.length; i++) {
						if (values[i].equals(connectionName)){
							return i;
						}
					}
				} else {
					HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log(e);
			}
		}
		if("hibernate.cfg.xml".equals(id)) { //$NON-NLS-1$
			return preferences.getConfigXMLFile();
		}
		if("hibernate.properties".equals(id)) { //$NON-NLS-1$
			return preferences.getPropertyFile();
		}
		if("mapping.files".equals(id)) { //$NON-NLS-1$
			return Integer.valueOf(preferences.getMappingFiles().length);
		}

		return null;
		} catch(RuntimeException e) {
			return HibernateConsoleMessages.ConsoleConfigurationPropertySource_error + e.getMessage();
		}
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		if("name".equals(id) && value instanceof String) { //$NON-NLS-1$
			String newName = (String) value;
			if (LaunchHelper.verifyConfigurationName(newName) != null) {
				return;//just do not change name
			}
			String oldName = cfg.getName();			
			try {
				ILaunchConfiguration lc = HibernateConsolePlugin.getDefault().findLaunchConfig(oldName);
				if (lc != null){
					ILaunchConfigurationWorkingCopy wc = lc.getWorkingCopy();
					wc.rename(newName);
					wc.doSave();
					//find newly created console configuration
					cfg = KnownConfigurations.getInstance().find(newName);
				} else {
					HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + oldName + "\"");//$NON-NLS-1$//$NON-NLS-2$
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log(e);
			}
		} else if ("mode".equals(id) && value instanceof Integer){		//$NON-NLS-1$	
			int index = (Integer) value;
			try {
				ILaunchConfiguration lc = HibernateConsolePlugin.getDefault().findLaunchConfig(cfg.getName());
				if (lc != null){
					ILaunchConfigurationWorkingCopy wc = lc.getWorkingCopy();
					wc.setAttribute("org.hibernate.eclipse.launch.CONFIGURATION_FACTORY", ConfigurationMode.values()[index]);////$NON-NLS-1$
					wc.doSave();
				} else {
					HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
			}			
		}  else if ("project".equals(id) && value instanceof Integer){		//$NON-NLS-1$	
			int index = (Integer) value;
			try {
				ILaunchConfiguration lc = HibernateConsolePlugin.getDefault().findLaunchConfig(cfg.getName());
				if (lc != null){
					ILaunchConfigurationWorkingCopy wc = lc.getWorkingCopy();
					wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, getSortedProjectNames()[index]);
					wc.doSave();
				} else {
					HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
			}
		} else if ("connection".equals(id) && value instanceof Integer){		//$NON-NLS-1$	
			int index = (Integer) value;
			try {
				ILaunchConfiguration lc = HibernateConsolePlugin.getDefault().findLaunchConfig(cfg.getName());
				if (lc != null){
					ILaunchConfigurationWorkingCopy wc = lc.getWorkingCopy();
					if (index == 0){//jpa
						wc.setAttribute(IConsoleConfigurationLaunchConstants.USE_JPA_PROJECT_PROFILE, Boolean.TRUE.toString());
						wc.removeAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME);
					} else if (index == 1){//hibernate
						wc.removeAttribute(IConsoleConfigurationLaunchConstants.USE_JPA_PROJECT_PROFILE);
						wc.removeAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME);
					} else {//connection profile
						String[] values = getConnectionNames();
						wc.setAttribute(IConsoleConfigurationLaunchConstants.CONNECTION_PROFILE_NAME, values[index]);
						wc.removeAttribute(IConsoleConfigurationLaunchConstants.USE_JPA_PROJECT_PROFILE);
					}
					wc.doSave();
				} else {
					HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
			}
		}
	}

	private IPropertyDescriptor createProjectDescriptor(){		
		ComboBoxPropertyDescriptor projectDescriptor = new ComboBoxPropertyDescriptor(
			"project",  //$NON-NLS-1$
			HibernateConsoleMessages.ConsoleConfigurationPropertySource_project,
			getSortedProjectNames());
		projectDescriptor.setValidator(new ICellEditorValidator(){		
			public String isValid(Object value) {				
				if (value instanceof Integer){
					if (((Integer)value).intValue() < 0){
						try {
							ILaunchConfiguration lc = HibernateConsolePlugin.getDefault().findLaunchConfig(cfg.getName());
							if (lc != null){
								String projectName = lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
								if (projectName != null){
									return NLS.bind(HibernateConsoleMessages.ConsoleConfigurationMainTab_the_java_project_does_not_exist, projectName);
								}
							} else {
								HibernateConsolePlugin.getDefault().log("Can't find Console Configuration \"" + cfg.getName() + "\"");//$NON-NLS-1$//$NON-NLS-2$
							}
						} catch (CoreException e) {
							HibernateConsolePlugin.getDefault().log(e);
						}
					}
				}
				return null;
			}
		});
		return projectDescriptor;
	}
	
	private IPropertyDescriptor createConnectionDescriptor(){
		ComboBoxPropertyDescriptor connectionDescriptor = new ComboBoxPropertyDescriptor(
			"connection",  //$NON-NLS-1$
			HibernateConsoleMessages.ConsoleConfigurationPropertySource_connection,
			getConnectionNames());
		return connectionDescriptor;
	}
	
	private String[] getSortedProjectNames(){
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();//get all projects
		String[] projectNames = new String[projects.length];
		for (int i = 0; i < projects.length; i++ ) {
			projectNames[i] = projects[i].getName();
		};		
		Arrays.sort(projectNames);
		return projectNames;
	}
	
	private String[] getConnectionNames(){
		IConnectionProfile[] profiles = ProfileManager.getInstance()
		.getProfilesByCategory("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
		String[] names = new String[profiles.length];
		for (int i = 0; i < profiles.length; i ++){
			names[i] = profiles[i].getName();
		}
		Arrays.sort(names);
		String[] resNames = new String[names.length + 2];
		resNames[0] = HibernateConsoleMessages.ConnectionProfileCtrl_JPAConfiguredConnection;
		resNames[1] = HibernateConsoleMessages.ConnectionProfileCtrl_HibernateConfiguredConnection;		
		System.arraycopy(names, 0, resNames, 2, names.length);
		return resNames;
	}

}
