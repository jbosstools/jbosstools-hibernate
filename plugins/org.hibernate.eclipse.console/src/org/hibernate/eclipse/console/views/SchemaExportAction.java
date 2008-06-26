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
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.ConsoleConfigurationBasedAction;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author max
 * 
 */
public class SchemaExportAction extends ConsoleConfigurationBasedAction {

	private StructuredViewer viewer;

	/**
	 * @param text
	 */
	protected SchemaExportAction(String text) {
		super( text );
	}

	/**
	 * @param selectionProvider
	 */
	public SchemaExportAction(StructuredViewer selectionProvider) {
		super( "Run SchemaExport" );
		this.viewer = selectionProvider;
	}

	public void doRun() {
		for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
			try {
				Object node = i.next();
				if ( node instanceof ConsoleConfiguration ) {
				final ConsoleConfiguration config = (ConsoleConfiguration) node;
				config.execute( new Command() {
					public Object execute() {
						if ( config.getConfiguration() != null 
								&& MessageDialog.openConfirm( viewer.getControl().getShell(),
										"Run SchemaExport",
										"Are you sure you want to run SchemaExport on '"
												+ config.getName() + "'?" ) ) {
							SchemaExport export = new SchemaExport( config
									.getConfiguration() );
							export.create( false, true );
							if ( !export.getExceptions().isEmpty() ) {
								Iterator iterator = export.getExceptions()
										.iterator();
								int cnt = 1;
								while ( iterator.hasNext() ) {
									Throwable element = (Throwable) iterator.next();
									HibernateConsolePlugin.getDefault().logErrorMessage("Error #"
															+ cnt++
															+ " while performing SchemaExport",
															element );
								}
								HibernateConsolePlugin.getDefault().showError(viewer.getControl().getShell(),
															cnt
															- 1
															+ " error(s) while performing SchemaExport, see Error Log for details",
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
						"Exception while running SchemaExport", he );
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
