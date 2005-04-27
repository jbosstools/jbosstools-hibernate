/*
 * Created on 15-Dec-2004
 *
 */
package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.node.BaseNode;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationCreationWizard;

/**
 * @author max
 *
 */
public class EditConsoleConfiguration extends ConsoleConfigurationBasedAction {

	/**
	 * @param text
	 */
	public EditConsoleConfiguration() {
		super("Edit Configuration");
	}

	protected void doRun() {
		for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
        	try {
        		BaseNode node = ((BaseNode) i.next());
        		final ConsoleConfiguration config = node.getConsoleConfiguration();
        		ConsoleConfigurationCreationWizard wizard = new ConsoleConfigurationCreationWizard();
        		wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(config));
        		IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        		
        		WizardDialog dialog = new WizardDialog(win.getShell(), wizard);
        		dialog.open(); // This opens a dialog
			} catch(HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(null, "Exception while trying to edit configuration", he);
        	}
        }
	}

	protected boolean updateState(ConsoleConfiguration consoleConfiguration) {
		return true;
	}
}
