/*
 * Created on 08-Dec-2004
 *
 */
package org.hibernate.eclipse.console.views;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.SessionFactoryBasedAction;
import org.hibernate.console.node.BaseNode;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author max
 * 
 */
public class SchemaExportAction extends SessionFactoryBasedAction {

	private StructuredViewer viewer;

	/**
	 * @param text
	 */
	protected SchemaExportAction(String text) {
		super(text);
	}

	/**
	 * @param selectionProvider
	 */
	public SchemaExportAction(StructuredViewer selectionProvider) {
		super("Run SchemaExport");
		this.viewer = selectionProvider;
	}

	public void run() {
		for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
        	try {
            BaseNode node = ((BaseNode) i.next());
			final ConsoleConfiguration config = node.getConsoleConfiguration();
			config.execute(new org.hibernate.console.ConsoleConfiguration.Command() {
				public Object execute() {
					if (config.getConfiguration() != null) {
						new SchemaExport(config.getConfiguration())
								.create(true, true);
					}
					return null;
				}
			});
            viewer.refresh(node); // todo: should we do it here or should the view just react to config being build ?
        	} catch(HibernateException he) {
        		 IStatus warning = new Status(IStatus.WARNING, 
        		 	      HibernateConsolePlugin.ID, 1, he.getClass().getName() + ":" + he.getMessage() , he);
        		 	   ErrorDialog.openError(viewer.getControl().getShell(), 
        		 	      "Exception while running SchemaExport", null, warning);
        	}
        }
	}

		/* (non-Javadoc)
		 * @see org.hibernate.eclipse.console.actions.SessionFactoryBasedAction#updateState(org.hibernate.console.ConsoleConfiguration)
		 */
		protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
			return consoleConfiguration.hasConfiguration();
		}
}
