package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.utils.LaunchHelper;
import org.hibernate.eclipse.mapper.MapperMessages;

public class ChooseConsoleConfigurationDialog extends TitleAreaDialog {

	private Combo text;

	String initialDefault;
	String selectedConfigurationName;

	public ChooseConsoleConfigurationDialog(Shell shell, String initialDefault) {
		super(shell);
		this.initialDefault = initialDefault;
	}

    public void prompt() {
    	open();

    	if (getReturnCode() == CANCEL) {
    		selectedConfigurationName = null;
    	}

    	return;
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        setTitle(MapperMessages.ChooseConsoleConfigurationDialog_select_console_configuration);
        //setMessage("");

        if (getTitleImageLabel() != null) {
			getTitleImageLabel().setVisible(false);
		}

        createConsoleConfigBrowseRow(composite);
        Dialog.applyDialogFont(composite);
        return composite;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MapperMessages.ChooseConsoleConfigurationDialog_select_console_configuration);
    }

    protected void okPressed() {
        selectedConfigurationName = text.getText();
        super.okPressed();
    }

    protected void cancelPressed() {
    	selectedConfigurationName = null;
        super.cancelPressed();
    }

    private void createConsoleConfigBrowseRow(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout(3, false);
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        panel.setLayout(layout);
        panel.setLayoutData(new GridData(GridData.FILL_BOTH));
        panel.setFont(parent.getFont());

        Label label = new Label(panel, SWT.NONE);
        label.setText(MapperMessages.ChooseConsoleConfigurationDialog_console_configuration);

        text = new Combo(panel, SWT.BORDER | SWT.LEAD | SWT.DROP_DOWN | SWT.READ_ONLY);

        text.setFocus();
        text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.FILL_HORIZONTAL));
        text.addModifyListener(new ModifyListener(){
        	public void modifyText(ModifyEvent e) {
        		Button okButton = getButton(Window.OK);
        		if(okButton != null && !okButton.isDisposed()) {
        			okButton.setEnabled(!"".equals(text.getText())); //$NON-NLS-1$
        		}
        	}
        });
        setInitialTextValues(text);

    }

    private void setInitialTextValues(Combo text) {
    	ConsoleConfiguration[] recentWorkspaces = LaunchHelper.findFilteredSortedConsoleConfigs();
        for (int i = 0; i < recentWorkspaces.length; ++i) {
				text.add(recentWorkspaces[i].getName());
		}

        text.setText(text.getItemCount() > 0 ? text.getItem(0) : initialDefault);
    }

	public String getSelectedConfigurationName() {
		return selectedConfigurationName;
	}

}
