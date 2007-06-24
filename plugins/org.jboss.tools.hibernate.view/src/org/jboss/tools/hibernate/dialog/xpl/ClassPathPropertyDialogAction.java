/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Exadel, Inc.
 *     Red Hat, Inc.
 *******************************************************************************/
package org.jboss.tools.hibernate.dialog.xpl;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.ui.internal.dialogs.PropertyPageContributorManager;
import org.eclipse.ui.internal.dialogs.PropertyPageManager;
import org.eclipse.ui.internal.dialogs.PropertyPageNode;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * This class shows Property Dialog with one page (by Default
 *         this is ClassPath Page)
 *         
 * @author sushko 
 */
public class ClassPathPropertyDialogAction extends PropertyDialogAction {

	private Shell shell;

	private PropertyPageNode showedNode = null; // node that will be shown

	private boolean showAllNodes = true;

	/**
	 * This method finds PropertyPageNode from pageManager by it's labelText
	 * 
	 * @param pageManager
	 * @param nodeText
	 */
	public PropertyPageNode findPropertyPageNode(
			PropertyPageManager pageManager, String nodeText) {
		PropertyPageNode result = null;
		List list = pageManager.getElements(PreferenceManager.PRE_ORDER);
		for (int i = 0; i < list.size(); i++) {
			PropertyPageNode pn = (PropertyPageNode) list.get(i);
			if (pn.getLabelText().equals(nodeText)) {
				result = pn;
				break;
			}
		}
		return result;
	}

	/**
	 * constructor ClassPathPropertyDialogAction
	 * 
	 * @param shell
	 * @param provider
	 */
	public ClassPathPropertyDialogAction(Shell shell,
			ISelectionProvider provider) {
		super(shell, provider);
		this.shell = shell;
	}

	/**
	 * Returns the name of the given element.
	 * 
	 * @param element
	 *            the element
	 * @return the name of the element
	 */
	private String getName(IAdaptable element) {
		IWorkbenchAdapter adapter = (IWorkbenchAdapter) element
				.getAdapter(IWorkbenchAdapter.class);
		if (adapter != null) {
			return adapter.getLabel(element);
		} else {
			return "";//$NON-NLS-1$
		}
	}

	/**
	 * non-standart implementation of the method run to show specific
	 * PropertyPageNode
	 * 
	 * @param showedNodeText
	 */
	public void run(String showedNodeText) {

		PropertyPageManager pageManager = new PropertyPageManager();
		String title = "Class Path Property";//$NON-NLS-1$
		// get selection
		IAdaptable element = (IAdaptable) getStructuredSelection()
				.getFirstElement();
		if (element == null)
			return;
		// load pages for the selection
		// fill the manager with contributions from the matching contributors
		PropertyPageContributorManager.getManager().contribute(pageManager,
				element);
		// testing if there are pages in the manager
		Iterator pages = pageManager.getElements(PreferenceManager.PRE_ORDER)
				.iterator();

		String name = getName(element);
		if (!pages.hasNext()) {
			// MessageDialog
			// .openInformation(
			// shell,
			// WorkbenchMessages
			// .getString("PropertyDialog.messageTitle"), //$NON-NLS-1$
			// WorkbenchMessages
			// .format(
			// "PropertyDialog.noPropertyMessage", new Object[]{name}));
			// //$NON-NLS-1$
			return;
		} else
		// title = WorkbenchMessages.format(
		// "PropertyDialog.propertyMessage", new Object[]{name}); //$NON-NLS-1$
		if (showedNodeText != null) {
			showedNode = findPropertyPageNode(pageManager, showedNodeText);
		}
		if (showedNode != null) {
			pageManager.removeAll();
			// Add node to show
			pageManager.addToRoot(showedNode);
		}
		// <yan> 20051028
		IDialogSettings javaSettings = JavaPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings pageSettings = javaSettings
				.getSection("BuildPathsPropertyPage");
		if (pageSettings != null) {
			pageSettings.put("pageIndex", 2);
		}
		// </yan>
		PropertyDialog propertyDialog = new PropertyDialog(shell, pageManager,
				getStructuredSelection());
		propertyDialog.create();
		propertyDialog.getShell().setText(title);
		WorkbenchHelpSystem.getInstance().setHelp(propertyDialog.getShell(),
				IWorkbenchHelpContextIds.PROPERTY_DIALOG);
		propertyDialog.open();
	}
}
