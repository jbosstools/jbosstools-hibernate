package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.RefreshTab;

public class LaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public LaunchConfigurationTabGroup() {
		super();		
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new CodeGenerationSettings(),
				new ExporterSettings(),
				//new ExporterProvidersTab(),
				new RefreshTab(),
				new CommonTab()
			};
			
			this.setTabs(tabs);
	}

}
