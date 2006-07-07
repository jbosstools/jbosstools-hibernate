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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationCreationWizard;

/**
 * @author max
 *
 */
public class EditConsoleConfiguration extends ConsoleConfigurationBasedAction {

	ConsoleConfiguration cfg = null;
	
	/**
	 * @param text
	 */
	public EditConsoleConfiguration() {
		super("Edit Configuration");
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
					HibernateConsolePlugin.getDefault().showError(null, "Exception while trying to edit configuration", he);
				}
			} 
		} else {
			try {
				edit(cfg);    	
			} catch(HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(null, "Exception while trying to edit configuration", he);
			}        
		}
	}

	private void edit(final ConsoleConfiguration config) {
		ConsoleConfigurationCreationWizard wizard = new ConsoleConfigurationCreationWizard();
		wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(config) );
		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		
		WizardDialog dialog = new WizardDialog(win.getShell(), wizard);
		dialog.open(); // This opens a dialog
	}

	protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
		return true;
	}
}
