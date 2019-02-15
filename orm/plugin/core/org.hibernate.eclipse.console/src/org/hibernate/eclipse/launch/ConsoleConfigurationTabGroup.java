package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class ConsoleConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public ConsoleConfigurationTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new ConsoleConfigurationMainTab(),
				new ConsoleConfigurationOptionsTab(),
				new ConsoleConfigurationJavaClasspathTab(),
				new ConsoleConfigurationMappingsTab(),
				new CommonTab(),
			};		
		setTabs(tabs);
	}
	
	
}
