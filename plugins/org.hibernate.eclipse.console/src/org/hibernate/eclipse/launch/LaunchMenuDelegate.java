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
package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;
import org.eclipse.jface.action.IAction;

/**
 * This action delegate is responsible for producing the
 * Run > Hibernate Tools sub menu contents, which includes
 * an items to run last tool, favorite tools, and show the
 * external tools launch configuration dialog.
 */
public class LaunchMenuDelegate extends AbstractLaunchToolbarAction {
	
	/**
	 * Creates the action delegate
	 */
	public LaunchMenuDelegate() {
		super("org.hibernate.eclipse.launch.CodeGenerationLaunchGroup"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction#getOpenDialogAction()
	 */
	protected IAction getOpenDialogAction() {
		IAction action= new OpenHibernateToolsConfigurations();
		action.setActionDefinitionId("org.eclipse.ui.externalTools.commands.OpenExternalToolsConfigurations"); //$NON-NLS-1$
		return action;
	}
}
