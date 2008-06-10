package org.hibernate.eclipse.console.utils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

public class LaunchHelper {

	static public ILaunchConfiguration findLaunchConfigurationByName(String launchConfigurationTypeId, String name) throws CoreException {
		Assert.isNotNull(launchConfigurationTypeId, HibernateConsoleMessages.LaunchHelper_launch_cfg_type_cannot_be_null);
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();


		ILaunchConfigurationType launchConfigurationType = launchManager
				.getLaunchConfigurationType(launchConfigurationTypeId);

		ILaunchConfiguration[] launchConfigurations = launchManager
				.getLaunchConfigurations(launchConfigurationType);

		for (int i = 0; i < launchConfigurations.length; i++) { // can't believe
			// there is no
			// look up by
			// name API
			ILaunchConfiguration launchConfiguration = launchConfigurations[i];
			if (launchConfiguration.getName().equals(name)) {
				return launchConfiguration;
			}
		}
		return null;
	}
}