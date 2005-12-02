package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddPropertyDialog extends Dialog {

	private String name;
	private String value;

	private String title;
	
	private Label nameLabel;
	private Text nameText;
	private Label valueLabel;
	private Text valueText;
	
	private String[] initialValues;

	public AddPropertyDialog(Shell shell, String title, String[] initialValues) {
		super(shell);
		this.title = title;
		this.initialValues= initialValues;
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp= (Composite) super.createDialogArea(parent);
		((GridLayout) comp.getLayout()).numColumns = 2;
		
		nameLabel = new Label(comp, SWT.NONE);
		nameLabel.setText("Name:");
		nameLabel.setFont(comp.getFont());
		
		nameText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		nameText.setText(initialValues[0]);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		nameText.setLayoutData(gd);
		nameText.setFont(comp.getFont());
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		});
		
		valueLabel = new Label(comp, SWT.NONE);
		valueLabel.setText("Value:");
		valueLabel.setFont(comp.getFont());
		
		valueText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		valueText.setText(initialValues[1]);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		valueText.setLayoutData(gd);
		valueText.setFont(comp.getFont());
		valueText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtons();
			}
		});		
		
		Button variablesButton = new Button(comp, SWT.PUSH);
		variablesButton.setText("Add"); //$NON-NLS-1$
		gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 2;
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint, variablesButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		variablesButton.setLayoutData(gd);
		variablesButton.setFont(comp.getFont());
		
		variablesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				getVariable();
			}
		});
		
		return comp;
	}
	
	protected void getVariable() {
		StringVariableSelectionDialog variablesDialog = new StringVariableSelectionDialog(getShell());
		int returnCode = variablesDialog.open();
		if (returnCode == IDialogConstants.OK_ID) {
			String variable = variablesDialog.getVariableExpression();
			if (variable != null) {
				valueText.insert(variable.trim());
			}
		}
	}

	/**
	 * Return the name/value pair entered in this dialog.  If the cancel button was hit,
	 * both will be <code>null</code>.
	 */
	public String[] getNameValuePair() {
		return new String[] {name, value};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			name= nameText.getText();
			value = valueText.getText();
		} else {
			name = null;
			value = null;
		}
		super.buttonPressed(buttonId);
	}
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}
	
	/**
	 * Enable the OK button if valid input
	 */
	protected void updateButtons() {
		String name = nameText.getText().trim();
		String value = valueText.getText().trim();
		getButton(IDialogConstants.OK_ID).setEnabled((name.length() > 0) &&(value.length() > 0));
	}
	
	public void create() {
		super.create();
		updateButtons();
	}
}
