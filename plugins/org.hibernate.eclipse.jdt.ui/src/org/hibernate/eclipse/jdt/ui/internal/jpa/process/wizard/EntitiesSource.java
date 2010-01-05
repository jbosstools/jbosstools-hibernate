/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.process.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages;
import org.hibernate.eclipse.jdt.ui.wizards.AddRemoveTableComposite;

/**
 * @author Vitali
 */
public class EntitiesSource extends UserInputWizardPage {

	protected AddRemoveTableComposite addRemoveTableComposite;
	
	protected IStructuredSelection selection;
	
	public EntitiesSource(String name, IStructuredSelection selection) {
		super(name);
		this.selection = selection;
		setDescription(JdtUiMessages.EntitiesSource_description);
	}

	public void createControl(Composite parent) {
		addRemoveTableComposite = new AddRemoveTableComposite(parent, SWT.NONE) {
			@Override
			protected void handleButtonPressed(Button button) {
				super.handleButtonPressed(button);
				itemsChanged();
			}
		};
		addRemoveTableComposite.getTableViewer().setInput(selection.toArray());
		setControl(addRemoveTableComposite);
	}

	public int getProcessDepth() {
		return addRemoveTableComposite.getProcessDepth();
	}
	
	public IStructuredSelection getSelection() {
		TableItem[] items = addRemoveTableComposite.getTableViewer().getTable().getItems();
		Object[] data = new Object[items.length];
		for (int i = 0; i < items.length; i++) {
			data[i] = items[i].getData();
		}
		return new StructuredSelection(data);
	}
	
	protected void itemsChanged(){
		getContainer().updateButtons();
	}

}
