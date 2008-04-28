package org.hibernate.eclipse.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.internal.debug.ui.classpath.ClasspathModel;
import org.eclipse.jdt.internal.debug.ui.classpath.IClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class ConsoleConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public ConsoleConfigurationTabGroup() {

	}

	
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new ConsoleConfigurationMainTab(),
				new ConsoleConfigurationOptionsTab(),
				new JavaClasspathTab() {
				
					public boolean isShowBootpath() {
						return false;
					}
				
					public boolean isValid(ILaunchConfiguration launchConfig) {						
						if(super.isValid( launchConfig )) {
							setErrorMessage( null );
							setMessage( null );
							IRuntimeClasspathEntry[] entries;
							try {
								entries = JavaRuntime.computeUnresolvedRuntimeClasspath(launchConfig);
								for (int i = 0; i < entries.length; i++) {
									IRuntimeClasspathEntry entry = entries[i];
									if(entry.getClasspathProperty()==IRuntimeClasspathEntry.USER_CLASSES) {
										return true;
									}
								}
								
							}
							catch (CoreException e) {
								HibernateConsolePlugin.getDefault().log( e );
							}
							setErrorMessage( "Classpath must be set or restored to default" );
							return false;													
						} 
						return false;						
					}
					
					public void initializeFrom(ILaunchConfiguration configuration) {
						
						super.initializeFrom( configuration );
					}
					
					public boolean canSave() {
						return super.canSave();
					}
				},
				new ConsoleConfigurationMappingsTab(),
				new CommonTab(),
				
			};
			
			this.setTabs(tabs);		
	}

}
