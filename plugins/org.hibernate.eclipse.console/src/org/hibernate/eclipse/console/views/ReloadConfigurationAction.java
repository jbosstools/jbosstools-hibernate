package org.hibernate.eclipse.console.views;

import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredViewer;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.HibernateConsoleRuntimeException;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.ConsoleConfigurationBasedAction;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ReloadConfigurationAction extends ConsoleConfigurationBasedAction {

	private StructuredViewer viewer;

	protected ReloadConfigurationAction(StructuredViewer sv) {
		super("Rebuild configuration");
		setEnabledWhenNoSessionFactory(true);
		viewer = sv;
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.RELOAD) );
	}

	protected void doRun() {
		for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
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
						"Exception while connecting/starting Hibernate", he);
			} catch (UnsupportedClassVersionError ucve) {
				HibernateConsolePlugin
						.getDefault()
						.showError(
								viewer.getControl().getShell(),
								"Starting Hibernate resulted in a UnsupportedClassVersionError.\nThis can occur if you are running eclipse with JDK 1.4 and your domain classes require JDK 1.5. \n\nResolution: Run eclipse with JDK 1.5.",
								ucve);
			}
		}
	}

}
