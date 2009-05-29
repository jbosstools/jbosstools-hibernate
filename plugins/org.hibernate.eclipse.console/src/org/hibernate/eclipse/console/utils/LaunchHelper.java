package org.hibernate.eclipse.console.utils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.AddConfigurationAction;
import org.hibernate.eclipse.launch.ICodeGenerationLaunchConstants;

public class LaunchHelper {
	
	public static ILaunchConfiguration findHibernateLaunchConfig(String name) throws CoreException {
		return findLaunchConfigurationByName(
			ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID, name);
	}
	
	public static ILaunchConfigurationType getHibernateLaunchConfigsType(){
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurationType(
			ICodeGenerationLaunchConstants.CONSOLE_CONFIGURATION_LAUNCH_TYPE_ID);
	}
	
	public static ILaunchConfiguration[] findHibernateLaunchConfigs() throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurations(getHibernateLaunchConfigsType());
	}

	public static ILaunchConfiguration findLaunchConfigurationByName(String launchConfigurationTypeId, String name) throws CoreException {
		Assert.isNotNull(launchConfigurationTypeId, HibernateConsoleMessages.LaunchHelper_launch_cfg_type_cannot_be_null);
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

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
	
	public static String verifyConfigurationName(String currentName) {
		if (currentName == null || currentName.length() < 1) {
			return HibernateConsoleMessages.ConsoleConfigurationWizardPage_name_must_specified;
		}
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			String[] badnames = new String[] { "aux", "clock$", "com1", "com2", "com3", "com4", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
					"com5", "com6", "com7", "com8", "com9", "con", "lpt1", "lpt2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
					"lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9", "nul", "prn" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			for (int i = 0; i < badnames.length; i++) {
				if (currentName.equals(badnames[i])) {
					return NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_bad_name, currentName);
				}
			}
		}
		// See if name contains any characters that we deem illegal.
		// '@' and '&' are disallowed because they corrupt menu items.
		char[] disallowedChars = new char[] { '@', '&', '\\', '/', ':', '*', '?', '"', '<', '>', '|', '\0' };
		for (int i = 0; i < disallowedChars.length; i++) {
			char c = disallowedChars[i];
			if (currentName.indexOf(c) > -1) {
				return NLS.bind(HibernateConsoleMessages.ConsoleConfigurationWizardPage_bad_char, c);
			}
		}

		if(existingLaunchConfiguration(currentName)) {
			return HibernateConsoleMessages.ConsoleConfigurationWizardPage_config_name_already_exist;
		}
		return null;
	}
	
	public static boolean existingLaunchConfiguration(String name) {
		try {
			ILaunchConfiguration config = findHibernateLaunchConfig(name);
			if(config != null && !config.getAttribute(AddConfigurationAction.TEMPORARY_CONFIG_FLAG, false)) {
				if (name.equalsIgnoreCase(config.getName())) {
					return true;
				}	
			}
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(e.getMessage(), e);
		}
		return false;
	}
}