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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationCreationWizard;

/**
 * 
 * Action that creates a ConsoleConfiguration
 * @author max
 *
 */
public class AddConfigurationAction extends Action {

	private final IViewPart part;

	public AddConfigurationAction(IViewPart part) {
		this.part = part;
		setText("Add Configuration");
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.ADD) );
	}

	public void run() {
		doAddConfiguration();
	}
	
	protected void doAddConfiguration() {
		ConsoleConfigurationCreationWizard wizard = new ConsoleConfigurationCreationWizard();
		wizard.init(PlatformUI.getWorkbench(), null); // initializes the wizard
		WizardDialog dialog = new WizardDialog(part.getSite().getShell(), wizard);
		dialog.open(); // This opens a dialog
		
	}
}
