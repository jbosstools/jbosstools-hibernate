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
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author max
 *
 */
public class DeleteConfigurationAction extends SelectionListenerAction {

	public static final String DELETECONFIG_ACTIONID = "actionid.deleteconfig"; //$NON-NLS-1$

	private StructuredViewer part;

	public DeleteConfigurationAction(StructuredViewer selectionProvider) {
		super(HibernateConsoleMessages.DeleteConfigurationAction_delete_config);
		setEnabled(false);
		this.part = selectionProvider;
		setId(DELETECONFIG_ACTIONID);
	}

	public void run() {
		List selectedNonResources = getSelectedNonResources();
		String question =  HibernateConsoleMessages.DeleteConfigurationAction_do_you_wish_del_selected_config;
		String title = HibernateConsoleMessages.DeleteConfigurationAction_delete_console_config;
		if (selectedNonResources.size() > 1){
			question += HibernateConsoleMessages.DeleteConfigurationAction_str_1;
			title += HibernateConsoleMessages.DeleteConfigurationAction_str_2;
		}
		question += HibernateConsoleMessages.DeleteConfigurationAction_str_3;

		if( MessageDialog.openConfirm( null, title, question)) {
			Iterator iter = selectedNonResources.iterator();
			while (iter.hasNext() ) {
				ConsoleConfiguration element = (ConsoleConfiguration) iter.next();
				KnownConfigurations.getInstance().removeConfiguration(element, false);
			}

			part.refresh();
		}
	}

	protected boolean updateSelection(IStructuredSelection selection) {
		if(!selection.isEmpty() ) {
			Iterator iter = getSelectedNonResources().iterator();
			while (iter.hasNext() ) {
				Object element = iter.next();
				if(element instanceof ConsoleConfiguration) {
					return true;
				}
			}
		}
		return false;
	}
}
