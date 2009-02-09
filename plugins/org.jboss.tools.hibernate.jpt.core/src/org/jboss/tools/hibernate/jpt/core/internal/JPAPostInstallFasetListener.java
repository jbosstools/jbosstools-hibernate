package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jpt.core.JptCorePlugin;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectListener;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent.Type;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.ICodeGenerationLaunchConstants;
import org.hibernate.eclipse.launch.IConsoleConfigurationLaunchConstants;

public class JPAPostInstallFasetListener implements IFacetedProjectListener {

	public void handleEvent(IFacetedProjectEvent event) {
		if (event.getType() == Type.POST_INSTALL){
			IProject project = event.getProject().getProject();
			IProjectFacetActionEvent pEvent = (IProjectFacetActionEvent)event;
			if (pEvent.getProjectFacet().getId().equals(JptCorePlugin.FACET_ID)
					&& HibernatePlatform.ID.equals(JptCorePlugin.getJpaPlatformId(project))){
				if (checkPreConditions(project)){
					buildConsoleConfiguration(project);
				}				
			}
		}		
	}
	
	/**
	 * 
	 * @param project
	 * @return true if need to create new ConsoleConfiguration, false - otherwise.
	 */
	protected boolean checkPreConditions(IProject project){
		try {
			ILaunchConfiguration lc = getLaunchConfiguration(project);
			if (lc != null){
				ProjectUtils.toggleHibernateOnProject(project, true, lc.getName());
				return false;
			}				
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private ILaunchConfiguration getLaunchConfiguration(IProject project) throws CoreException{
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();			
		ILaunchConfigurationType lct = lm.getLaunchConfigurationType(ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID);
		List<ILaunchConfiguration> configs = new ArrayList<ILaunchConfiguration>();
		for (int i = 0; i < lm.getLaunchConfigurations(lct).length; i++){
			ILaunchConfiguration lc = lm.getLaunchConfigurations(lct)[i];
			if (project.getName().equals(
					lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""))){//lc uses this project					
				if (project.getName().equals(lc.getName())) return lc;
				configs.add(lc);				
			}
		}
		//select best launch configuration "projectName (1)"
		Pattern p = Pattern.compile(project.getName() + " \\(\\d+\\)");
		for (int i = 0; i < configs.size(); i++) {
			ILaunchConfiguration lc = configs.get(i);
			if (p.matcher(lc.getName()).matches()) return lc; 
		}
		return configs.size() > 0 ? configs.get(0) : null;
	}
	
	protected void buildConsoleConfiguration(IProject project){
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType lct = lm.getLaunchConfigurationType(ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID);
		String launchName = lm.generateUniqueLaunchConfigurationNameFrom(project.getName());
		ILaunchConfigurationWorkingCopy wc;
		try {
			wc = lct.newInstance(null, launchName);
			wc.setAttribute(IConsoleConfigurationLaunchConstants.CONFIGURATION_FACTORY, ConfigurationMode.JPA.toString());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH, true );
			wc.setAttribute(IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, (List<String>)null);
			wc.setAttribute(IConsoleConfigurationLaunchConstants.USE_JPA_PROJECT_PROFILE, "true");//$NON-NLS-1$
			
			wc.doSave();
			ProjectUtils.toggleHibernateOnProject(project, true, launchName);
		} catch (CoreException e) {
			e.printStackTrace();
		}		
	}

}
