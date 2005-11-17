package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.actions.OpenLaunchDialogAction;

/**
 * Opens the launch config dialog on the external tools launch group.
 */
public class OpenHibernateToolsConfigurations extends OpenLaunchDialogAction {

	public OpenHibernateToolsConfigurations() {
		super("org.hibernate.eclipse.launch.ArtifactLaunchGroup");
	}
}
