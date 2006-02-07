/*
 * Created on 2004-10-29 by max
 * 
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
