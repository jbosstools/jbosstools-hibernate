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

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleConfigurationListener;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsListener;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.actions.ConsoleConfigurationBasedAction;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * @author max
 *
 */
public class SchemaExportAction extends ConsoleConfigurationBasedAction implements ConsoleConfigurationListener, KnownConfigurationsListener {

	public static final String SCHEMAEXPORT_ACTIONID = "actionid.schemaexport"; //$NON-NLS-1$

	private StructuredViewer viewer;
	
	private HashSet<ConsoleConfiguration> vecMonitorConsoleConfigs = new HashSet<ConsoleConfiguration>();

	/**
	 * @param text
	 */
	protected SchemaExportAction(String text) {
		super( text );
		setId(SCHEMAEXPORT_ACTIONID);
		KnownConfigurations.getInstance().addConsoleConfigurationListener(this);
	}

	/**
	 * @param selectionProvider
	 */
	public SchemaExportAction(StructuredViewer selectionProvider) {
		super( HibernateConsoleMessages.SchemaExportAction_run_schemaexport );
		this.viewer = selectionProvider;
		setId(SCHEMAEXPORT_ACTIONID);
		KnownConfigurations.getInstance().addConsoleConfigurationListener(this);
	}

    /**
     * Convenience method to open a standard warning Yes/No dialog.
     * 
     * @param parent the parent shell of the dialog, or <code>null</code> if none
     * @param title the dialog's title, or <code>null</code> if none
     * @param msg the message
     * @return <code>true</code> if the user presses the Yes button,
     *         <code>false</code> otherwise
     */
	public static boolean openWarningYesNoDlg(Shell parent, String title, String msg) {
		String[] dialogButtonLabels = new String[] { IDialogConstants.YES_LABEL,
			IDialogConstants.NO_LABEL };
		MessageDialog dialog = new MessageDialog(parent, title, null, msg,
			MessageDialog.WARNING, dialogButtonLabels, 0);
		return dialog.open() == 0;
	}
	
	public void doRun() {
		for (Iterator<?> i = getSelectedNonResources().iterator(); i.hasNext();) {
			Object node = i.next();
			if (!(node instanceof ConsoleConfiguration)) {
				continue;
			}
			final ConsoleConfiguration config = (ConsoleConfiguration) node;
			try {
				config.execute( new Command() {
					@SuppressWarnings("unchecked")
					public Object execute() {
						final Configuration cfg = config.getConfiguration();
						if (cfg == null) {
							return null;
						}
						String out = NLS.bind(HibernateConsoleMessages.SchemaExportAction_sure_run_schemaexport, config.getName());
						boolean res = openWarningYesNoDlg(viewer.getControl().getShell(),
							HibernateConsoleMessages.SchemaExportAction_run_schemaexport, out);
						if (!res) {
							return null;
						}
						SchemaExport export = new SchemaExport(cfg);
						export.create(false, true);
						if (!export.getExceptions().isEmpty()) {
							Iterator<Throwable> iterator = export.getExceptions().iterator();
							int cnt = 1;
							while (iterator.hasNext()) {
								Throwable element = iterator.next();
								String outStr = NLS.bind(HibernateConsoleMessages.SchemaExportAction_errornum_while_performing_schemaexport, cnt++);
								HibernateConsolePlugin.getDefault().logErrorMessage(outStr, element);
							}
							HibernateConsolePlugin.getDefault().showError(viewer.getControl().getShell(),
								NLS.bind(HibernateConsoleMessages.SchemaExportAction_error_while_performing_schemaexport, cnt - 1),
								(Throwable)null );
						}
						return null;
					}
				} );
				viewer.refresh( node ); // todo: should we do it here or should
				// the view just react to config being
				// build ?
			}
			catch (HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(
						viewer.getControl().getShell(),
						HibernateConsoleMessages.SchemaExportAction_exception_running_schemaexport, he );
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private HashSet<ConsoleConfiguration> cloneMonitorConsoleConfigs() {
		return (HashSet<ConsoleConfiguration>)vecMonitorConsoleConfigs.clone();
	}
	
	@SuppressWarnings("rawtypes")
	protected void clearCache() {
		final IStructuredSelection selection = getStructuredSelection();
		HashSet<ConsoleConfiguration> vecMonitorConsoleConfigsTmp = 
			cloneMonitorConsoleConfigs();
        for (ConsoleConfiguration consoleConfig : vecMonitorConsoleConfigsTmp) {
        	consoleConfig.removeConsoleConfigurationListener(this);
        }
        vecMonitorConsoleConfigsTmp.clear();
        for (Iterator i = selection.iterator(); i.hasNext(); ) {
        	Object obj = i.next();
        	if (obj instanceof ConsoleConfiguration) {
        		ConsoleConfiguration consoleConfig = (ConsoleConfiguration)obj;
        		vecMonitorConsoleConfigsTmp.add(consoleConfig);
            	consoleConfig.addConsoleConfigurationListener(this);
        	}
        }
        vecMonitorConsoleConfigs = vecMonitorConsoleConfigsTmp;
        super.clearCache();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.hibernate.eclipse.console.actions.SessionFactoryBasedAction#updateState(org.hibernate.console.ConsoleConfiguration)
	 */
	protected boolean updateState(ConsoleConfiguration ccfg) {
		return ccfg.hasConfiguration();
	}

	public void updateEnableState() {
		selectionChanged(getStructuredSelection());
	}

	public void queryPageCreated(QueryPage qp) {
	}

	public void sessionFactoryBuilt(ConsoleConfiguration ccfg, SessionFactory builtSessionFactory) {
	}

	public void sessionFactoryClosing(ConsoleConfiguration ccfg, SessionFactory aboutToCloseFactory) {
	}

	public void configurationBuilt(ConsoleConfiguration ccfg) {
		updateEnableState();
	}

	public void configurationReset(ConsoleConfiguration ccfg) {
		updateEnableState();
	}

	public void configurationAdded(ConsoleConfiguration root) {
	}

	public void configurationRemoved(ConsoleConfiguration ccfg, boolean forUpdate) {
		if (vecMonitorConsoleConfigs.remove(ccfg)) {
			ccfg.removeConsoleConfigurationListener(this);
		}
	}
}
