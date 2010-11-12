/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.console;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Dmitry Geraskov
 * 
 */
public class ParametersListDialog extends Dialog {

	private ListViewer listViewer;

	private ConsoleQueryParameter cqp;

	private String[] values;

	/**
	 * @param parentShell
	 */
	public ParametersListDialog(Shell parentShell, ConsoleQueryParameter cqp) {
		super(parentShell);
		this.cqp = cqp;
	
	}

	@Override
	protected Control createDialogArea(Composite container) {
		Composite parent = (Composite) super.createDialogArea(container);
		listViewer = new ListViewer(parent);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = convertHeightInCharsToPixels(15);
		gd.widthHint = convertWidthInCharsToPixels(55);
		org.eclipse.swt.widgets.List list = listViewer.getList();
		list.setLayoutData(gd);
		list.setFont(container.getFont());
		/*List<String> input = new ArrayList<String>();
		Object value = cqp.getValue();
		if (value != null) {
			if (cqp.isArrayValue()) {
				Object[] values = (Object[]) value;
				for (Object object : values) {
					input.add(cqp.convertValueToString(object));
				}
			} else {
				input.add(cqp.convertValueToString(value));
			}
		}*/
		if (!cqp.isNull()){
			list.setItems(cqp.getStringValues());
		}
		if (list.getItemCount() > 0){
			list.select(list.getItemCount() - 1);
		}
		addButtons(parent);
		return parent;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Parameter Values"); //$NON-NLS-1$
	}

	private Button buttonAdd;
	private Button buttonRemove;
	private Button buttonModify;

	private void addButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.spacing = 2;

		composite.setLayout(fillLayout);

		buttonAdd = new Button(composite, SWT.PUSH);
		buttonAdd.setText("Add"); //$NON-NLS-1$

		buttonModify = new Button(composite, SWT.PUSH);
		buttonModify.setText("Modify"); //$NON-NLS-1$

		buttonRemove = new Button(composite, SWT.PUSH);
		buttonRemove.setText("Remove"); //$NON-NLS-1$

		buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InputDialog inputDialog = new InputDialog(null, "Add Element", //$NON-NLS-1$
						"New parameter: ", "", null); //$NON-NLS-1$ //$NON-NLS-2$
				if (inputDialog.open() == Window.OK) {
					listViewer.getList().add(inputDialog.getValue());
				}
			}
		});

		buttonModify.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int line = listViewer.getList().getSelectionIndex();
				if (line >=0 ){
					InputDialog inputDialog = new InputDialog(null, "Add Element", //$NON-NLS-1$
							"New parameter: ", listViewer.getList().getItem(line), null); //$NON-NLS-1$
					if (inputDialog.open() == Window.OK) {
						listViewer.getList().setItem(line, inputDialog.getValue());
					}
				}

				
			}
		});

		buttonRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int line = listViewer.getList().getSelectionIndex();
				if (line < 0) {
					return;
				} else {
					listViewer.getList().remove(line);
				}
			}
		});
	}

	@Override
	protected void okPressed() {
		values = listViewer.getList().getItems();
		super.okPressed();
	}

	public String[] getValue() {
		return values;
	}

}
