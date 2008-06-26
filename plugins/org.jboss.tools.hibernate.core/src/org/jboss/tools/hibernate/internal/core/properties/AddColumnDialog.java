/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.properties;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.DataFormatException;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;

/**
 * @author kaa
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddColumnDialog extends Dialog {
	public static final String BUNDLE_NAME = "properties"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(AddColumnDialog.class.getPackage().getName() + "." + BUNDLE_NAME); 
	private IDatabaseTable table;
	private Text Newtext,Lenghttext;
    private Combo Newtype;
	private String title,columnname;
	private CLabel message;
	/**
	 * @param parentShell
	 */
	public AddColumnDialog(Shell parentShell, IDatabaseTable table) {
		super(parentShell);
		this.setTitle(BUNDLE.getString("AddColumnDialog.title"));
		this.table = table;
	}

	protected Control createDialogArea(Composite parent) {
		Composite root = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		root.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 130;
		gd.widthHint = 225;
		root.setLayoutData(gd);

		message = new CLabel(root, SWT.NULL);
		message.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 3,
				1));
		message.setText("");

		Label label2 = new Label(root, SWT.NULL);
		label2.setText(BUNDLE.getString("AddColumnDialog.columnname"));
		label2
				.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 3,
						1));

		Newtext = new Text(root, SWT.BORDER | SWT.SINGLE);
		Newtext.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 3,
				1));
		Newtext.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.widget;
				String newtxt = text.getText().trim();
				if (newtxt.equals("")) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setErrorMessage(null);
				} else if (table.isColumnExists(newtxt)) {
					getButton(IDialogConstants.OK_ID).setEnabled(false);
					setErrorMessage(BUNDLE
							.getString("AddColumnDialog.existcolumnname"));
				} else {
					getButton(IDialogConstants.OK_ID).setEnabled(true);
					setErrorMessage(null);
				}
			}
		});

		Label label3 = new Label(root, SWT.NULL);
		label3.setText(BUNDLE.getString("AddColumnDialog.sqltype"));//select hibern type
		label3.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1,
				1));

		Label label4 = new Label(root, SWT.NULL);
		label4.setText(BUNDLE.getString("AddColumnDialog.len"));
		label4
				.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 2,
						1));

		Newtype = new Combo(root, SWT.READ_ONLY);
		Newtype.setItems(OrmConfiguration.ID_DATATYPES);
		Newtype.setText(OrmConfiguration.DEFAULT_ID_DATATYPE);
		Newtype.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1,
				1));

		Lenghttext = new Text(root, SWT.BORDER | SWT.SINGLE);
		Lenghttext.setText("0");
		GridData gd1 = new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1);
		Lenghttext.setLayoutData(gd1);

		return root;
	}

	protected void okPressed() {
		columnname = Newtext.getText().trim();
		if (columnname.length() > 0) {
			int z;
			try {
				z = StringConverter.asInt(Lenghttext.getText());//Lenghttext.getText().
			} catch (DataFormatException e) {
				z = 0;
			}
			Column newelem = new Column();
			newelem.setName(columnname);
			newelem.setLength(z);
			newelem.setSqlTypeName(TypeUtils.SQLTypeToName(Type.getType(
					Newtype.getText()).getSqlType()));
			((Table) table).addColumn(newelem);
		}
		setReturnCode(OK);
		close();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setErrorMessage(String message) {
		if (message != null) {
			this.message.setText(message);
			this.message.setImage(JFaceResources
					.getImage(Dialog.DLG_IMG_MESSAGE_ERROR));
		} else {
			this.message.setText("");
			this.message.setImage(null);
		}
	}

	public String getColumnName() {
		return columnname;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null)
			shell.setText(title);
	}

	/**
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		this.getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
 
}
