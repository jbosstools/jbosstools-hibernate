package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.widgets.Combo;

public class ConsoleConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {
	
	private ConsoleConfigurationMainTab ccmt = null;

	public ConsoleConfigurationTabGroup() {
	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ccmt = new ConsoleConfigurationMainTab();
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				ccmt,
				new ConsoleConfigurationOptionsTab(),
				new ConsoleConfigurationJavaClasspathTab(),
				new ConsoleConfigurationMappingsTab(),
				new CommonTab(),
			};		
		setTabs(tabs);
	}
	
	public Combo getHibernateVersionCombo() {
		return ccmt.getHibernateVersionCombo();
	}
	
	
}
