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
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.LaunchHelper;

/**
 * @author max
 *
 */
public class EditConsoleConfiguration extends ConsoleConfigurationBasedAction {

	public static final String EDITCONSOLECONFIG_ACTIONID = "actionid.editconsoleconfig"; //$NON-NLS-1$

	ConsoleConfiguration cfg = null;

	/**
	 * @param text
	 */
	public EditConsoleConfiguration() {
		super(HibernateConsoleMessages.EditConsoleConfiguration_edit_config);
		setId(EDITCONSOLECONFIG_ACTIONID);
	}

	public EditConsoleConfiguration(ConsoleConfiguration configuration) {
		this();
		cfg = configuration;
	}

	protected void doRun() {
		if(cfg==null) {
			for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
				try {
					Object node = i.next();
					if(node instanceof ConsoleConfiguration) {
						final ConsoleConfiguration config = (ConsoleConfiguration) node;
						edit( config );
					}
				} catch(HibernateException he) {
					HibernateConsolePlugin.getDefault().showError(null, HibernateConsoleMessages.EditConsoleConfiguration_exception_while_edit_config, he);
				}
			}
		} else {
			try {
				edit(cfg);
			} catch(HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(null, HibernateConsoleMessages.EditConsoleConfiguration_exception_while_edit_config, he);
			}
		}
	}

	private void edit(final ConsoleConfiguration config) {
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		try {
			ILaunchConfiguration launchConfiguration = LaunchHelper.findHibernateLaunchConfig(config.getName());
			if(launchConfiguration != null) {
				DebugUITools.openLaunchConfigurationPropertiesDialog( win.getShell(), launchConfiguration, "org.eclipse.debug.ui.launchGroup.run" ); //$NON-NLS-1$
				return;
			}
			String out = NLS.bind(HibernateConsoleMessages.EditConsoleConfiguration_could_not_find_launch_cfg, config.getName());
			HibernateConsolePlugin.getDefault().showError(win.getShell(), out, new IllegalStateException(HibernateConsoleMessages.EditConsoleConfiguration_no_launch_cfg_matched + config.getName()));
		} catch (CoreException ce) {
			HibernateConsolePlugin.getDefault().showError( win.getShell(), HibernateConsoleMessages.EditConsoleConfiguration_problem_adding_console_cfg,  ce);
		}
	}

	protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
		return true;
	}
}
