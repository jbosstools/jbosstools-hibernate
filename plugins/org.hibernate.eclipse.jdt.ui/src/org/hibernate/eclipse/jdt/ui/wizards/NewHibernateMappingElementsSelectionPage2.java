/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.hibernate.eclipse.jdt.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class NewHibernateMappingElementsSelectionPage2 extends WizardPage {
	
	AddRemoveTableComposite addRemoveTableComposite;
	
	private IStructuredSelection selection;

	public NewHibernateMappingElementsSelectionPage2(String pageName, IStructuredSelection selection) {
		super(pageName);
		this.selection = selection;
		setDescription(JdtUiMessages.NewHibernateMappingElementsSelectionPage2_description);
	}

	public void createControl(Composite parent) {
		addRemoveTableComposite = new AddRemoveTableComposite(parent, SWT.NONE);
		addRemoveTableComposite.getTableViewer().setInput(selection.toArray());
		setControl(addRemoveTableComposite);
	}
	
	public IStructuredSelection getSelection(){
		TableItem[] items = addRemoveTableComposite.getTableViewer().getTable().getItems();
		Object[] data = new Object[items.length];
		for (int i = 0; i < items.length; i++) {
			data[i] = items[i].getData();			
		}
		return new StructuredSelection(data);
	}

}
