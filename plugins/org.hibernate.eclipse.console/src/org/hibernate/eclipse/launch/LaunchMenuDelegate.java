package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;
import org.eclipse.jface.action.IAction;

/**
 * This action delegate is responsible for producing the
 * Run > Hibernate Tools sub menu contents, which includes
 * an items to run last tool, favorite tools, and show the
 * external tools launch configuration dialog.
 */
public class LaunchMenuDelegate extends AbstractLaunchToolbarAction {
	
	/**
	 * Creates the action delegate
	 */
	public LaunchMenuDelegate() {
		super("org.hibernate.eclipse.launch.ArtifactLaunchGroup");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction#getOpenDialogAction()
	 */
	protected IAction getOpenDialogAction() {
		IAction action= new OpenHibernateToolsConfigurations();
		action.setActionDefinitionId("org.eclipse.ui.externalTools.commands.OpenExternalToolsConfigurations"); //$NON-NLS-1$
		return action;
	}
}
