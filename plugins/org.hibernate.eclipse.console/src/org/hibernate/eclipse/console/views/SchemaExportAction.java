/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.views;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osgi.util.NLS;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.ConsoleConfigurationBasedAction;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author max
 *
 */
public class SchemaExportAction extends ConsoleConfigurationBasedAction {

	public static final String SCHEMAEXPORT_ACTIONID = "actionid.schemaexport"; //$NON-NLS-1$

	private StructuredViewer viewer;

	/**
	 * @param text
	 */
	protected SchemaExportAction(String text) {
		super( text );
		setId(SCHEMAEXPORT_ACTIONID);
	}

	/**
	 * @param selectionProvider
	 */
	public SchemaExportAction(StructuredViewer selectionProvider) {
		super( HibernateConsoleMessages.SchemaExportAction_run_schemaexport );
		this.viewer = selectionProvider;
		setId(SCHEMAEXPORT_ACTIONID);
	}

	public void doRun() {
		for (Iterator<?> i = getSelectedNonResources().iterator(); i.hasNext();) {
			try {
				Object node = i.next();
				if ( node instanceof ConsoleConfiguration ) {
				final ConsoleConfiguration config = (ConsoleConfiguration) node;
				config.execute( new Command() {
					@SuppressWarnings("unchecked")
					public Object execute() {
						String out = NLS.bind(HibernateConsoleMessages.SchemaExportAction_sure_run_schemaexport, config.getName());
						if ( config.getConfiguration() != null
								&& MessageDialog.openConfirm( viewer.getControl().getShell(),
										HibernateConsoleMessages.SchemaExportAction_run_schemaexport,
										out ) ) {
							SchemaExport export = new SchemaExport( config
									.getConfiguration() );
							export.create( false, true );
							if ( !export.getExceptions().isEmpty() ) {
								Iterator<Throwable> iterator = export.getExceptions().iterator();
								int cnt = 1;
								while ( iterator.hasNext() ) {
									Throwable element = iterator.next();
									String outStr = NLS.bind(HibernateConsoleMessages.SchemaExportAction_errornum_while_performing_schemaexport, cnt++);
									HibernateConsolePlugin.getDefault().logErrorMessage(outStr, element );
								}
								HibernateConsolePlugin.getDefault().showError(viewer.getControl().getShell(),
															NLS.bind(HibernateConsoleMessages.SchemaExportAction_error_while_performing_schemaexport, cnt - 1),
															(Throwable)null );
							}
						}
						return null;
					}
				} );
				viewer.refresh( node ); // todo: should we do it here or should
				// the view just react to config being
				// build ?
				}
			}
			catch (HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(
						viewer.getControl().getShell(),
						HibernateConsoleMessages.SchemaExportAction_exception_running_schemaexport, he );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hibernate.eclipse.console.actions.SessionFactoryBasedAction#updateState(org.hibernate.console.ConsoleConfiguration)
	 */
	protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
		return consoleConfiguration.hasConfiguration();
	}
}
