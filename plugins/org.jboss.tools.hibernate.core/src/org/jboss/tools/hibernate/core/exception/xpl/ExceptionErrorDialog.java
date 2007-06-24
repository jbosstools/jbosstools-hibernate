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
package org.jboss.tools.hibernate.core.exception.xpl;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Gavrs
 * 
 */

public class ExceptionErrorDialog extends Dialog {
	private Label dateLabel;

	private Text msgText;

	private Text stackTraceText;

	private SashForm sashForm;

	private Point dialogLocation;

	private Point dialogSize;

	String messageStack, nameException;

	String title;

	public ExceptionErrorDialog(Shell parentShell, String title, String e,
			String str) {
		super(parentShell);
		if (str == null)
			messageStack = ""; //$NON-NLS-1$
		else
			messageStack = str;
		if (e == null)
			e = ""; //$NON-NLS-1$
		else
			nameException = e;
		if (title == null) {
			this.setTitle(Messages.ExceptionErrorDialog_Title);
		} else {
			this.setTitle(title);
		}

	}

	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gd);

		createNameSection(container);
		createSashForm(container);
		createStackSection(getSashForm());
		return container;
	}

	private void createSashForm(Composite parent) {
		sashForm = new SashForm(parent, SWT.VERTICAL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = layout.marginWidth = 0;
		sashForm.setLayout(layout);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createNameSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createTextSection(container);
	}

	private void createTextSection(Composite parent) {
		Composite textContainer = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = layout.marginWidth = 0;
		textContainer.setLayout(layout);
		textContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label = new Label(textContainer, SWT.NONE);
		label.setText(Messages.ExceptionErrorDialog_Message);
		dateLabel = new Label(textContainer, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		dateLabel.setLayoutData(gd);

		msgText = new Text(textContainer, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP
				| SWT.BORDER);
		msgText.setEditable(false);
		gd = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_VERTICAL);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 8;
		gd.grabExcessVerticalSpace = true;
		msgText.setLayoutData(gd);
		msgText.setText(nameException);

	}

	public void setMesageText(String str) {
		msgText.setText(str);
	}

	private void createStackSection(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 6;
		container.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 100;
		container.setLayoutData(gd);

		Label label = new Label(container, SWT.NULL);
		label.setText(Messages.ExceptionErrorDialog_ExceptionStackTrace);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		stackTraceText = new Text(container, SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		stackTraceText.setLayoutData(gd);
		stackTraceText.setEditable(false);
		stackTraceText.setText(messageStack);
	}

	public SashForm getSashForm() {
		return sashForm;
	}

	public void create() {
		super.create();

		if (dialogLocation != null)
			getShell().setLocation(dialogLocation);

		if (dialogSize != null)
			getShell().setSize(dialogSize);
		else
			getShell().setSize(500, 550);

		applyDialogFont(buttonBar);
		getButton(IDialogConstants.OK_ID).setFocus();
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null)
			shell.setText(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
