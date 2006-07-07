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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;

/**
 * @author max
 *
 */
public class DeleteConfigurationAction extends SelectionListenerAction {
	
	public DeleteConfigurationAction() {
		super("Delete Configuration");
		setEnabled(false);
	}

	public void run() {
		List selectedNonResources = getSelectedNonResources();
		
		Iterator iter = selectedNonResources.iterator();
		while (iter.hasNext() ) {
			ConsoleConfiguration element = (ConsoleConfiguration) iter.next();
			KnownConfigurations.getInstance().removeConfiguration(element);
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
