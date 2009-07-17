package org.hibernate.eclipse.console.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.ConsoleConfigurationBasedAction;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ReloadConfigurationAction extends ConsoleConfigurationBasedAction {

	public static final String RELOADCONFIG_ACTIONID = "actionid.reloadconfig"; //$NON-NLS-1$

	private StructuredViewer viewer;

	protected ReloadConfigurationAction(StructuredViewer sv) {
		super(HibernateConsoleMessages.ReloadConfigurationAction_rebuild_configuration);
		setEnabledWhenNoSessionFactory(true);
		viewer = sv;
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.RELOAD) );
		setId(RELOADCONFIG_ACTIONID);
	}

	protected void doRun() {
		for (Iterator<?> i = getSelectedNonResources().iterator(); i.hasNext();) {
			try {
				Object node = i.next();
				if (node instanceof ConsoleConfiguration) {
					ConsoleConfiguration config = (ConsoleConfiguration) node;
					config.reset();
					updateState(config);
					viewer.refresh(node);
				}
			} catch (HibernateConsoleRuntimeException he) {
				HibernateConsolePlugin.getDefault().showError(
						viewer.getControl().getShell(),
						HibernateConsoleMessages.ReloadConfigurationAction_exception_while_start_hibernate, he);
			} catch (UnsupportedClassVersionError ucve) {
				HibernateConsolePlugin
						.getDefault()
						.showError(
								viewer.getControl().getShell(),
								HibernateConsoleMessages.ReloadConfigurationAction_starting_hibernate_resulted_exception,
								ucve);
			}
		}
	}

}
