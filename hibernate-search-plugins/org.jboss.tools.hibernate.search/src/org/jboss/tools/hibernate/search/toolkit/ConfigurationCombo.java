package org.jboss.tools.hibernate.search.toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.utils.LaunchHelper;

public class ConfigurationCombo {
	
	protected Combo comboControl;
	private String consoleConfigName;

	public ConfigurationCombo(Composite parent, String consoleConfigName) {
		this.consoleConfigName = consoleConfigName;
		this.comboControl = new Combo(parent, SWT.READ_ONLY);
		populateComboBox();
	}

	protected void populateComboBox() {
		ConsoleConfiguration[] configurations = LaunchHelper.findFilteredSortedConsoleConfigs();
		final String[] names = new String[configurations.length];
		for (int i = 0; i < configurations.length; i++) {
			names[i] = configurations[i].getName();
		}
		comboControl.setItems(names);
		if (this.consoleConfigName != null) {
			comboControl.setText(this.consoleConfigName);
		} else {
			comboControl.select(0);
		}
	}
	
	public String getConsoleConfigSelected() {
		return comboControl.getText();
	}
	
	public void setConsoleConfigSelected(String consoleConfigName) {
		comboControl.setText(consoleConfigName);
	}
	
	public void addModifyListener(ModifyListener modifyListener) {
		comboControl.addModifyListener(modifyListener);
	}
}
