package org.jboss.tools.hibernate.search;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.logging.xpl.EclipseLogger;
import org.jboss.tools.hibernate.search.toolkit.IndexToolkitView;
import org.osgi.framework.BundleContext;

public class HibernateSearchConsolePlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "org.jboss.tools.hibernate.search";

	private static HibernateSearchConsolePlugin plugin;
	
	public IViewPart showIndexToolkitView(ConsoleConfiguration cc) {
		try {
			IWorkbenchPage page = getActiveWorkbenchWindow().getActivePage();
			IndexToolkitView indexToolkitView = (IndexToolkitView)page.showView(IndexToolkitView.INDEX_TOOLKIT_VIEW_ID);
			indexToolkitView.setInitialConsoleConfig(cc);
			return indexToolkitView;
		} catch (PartInitException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static HibernateSearchConsolePlugin getDefault() {
		if (plugin == null) {
			return new HibernateSearchConsolePlugin();
		}
		return plugin;
	}
	
	public void log(Throwable e) {
		getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, 150, "Hibernate Search Internal Error", e) );
	}

}
