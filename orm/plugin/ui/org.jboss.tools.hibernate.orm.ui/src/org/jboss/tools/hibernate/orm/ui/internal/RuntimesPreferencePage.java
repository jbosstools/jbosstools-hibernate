package org.jboss.tools.hibernate.orm.ui.internal;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.hibernate.runtime.spi.RuntimeServiceManager;

public class RuntimesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	
	private TableItem[] tableItems = new TableItem[RuntimeServiceManager.getInstance().getAllVersions().length];
	private Combo defaultRuntimeCombo = null;

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		createAllRuntimesLabel(composite);
		createAllRuntimesTable(composite);
		createDefaultRuntimeLabel(composite);
		createDefaultRuntimeCombo(composite);
		refreshPage();
		return composite;
	}
	
	@Override
	public boolean performOk() {
		for (TableItem tableItem : tableItems) {
			RuntimeServiceManager.getInstance().enableService(tableItem.getText(), tableItem.getChecked());
		}
		RuntimeServiceManager.getInstance().setDefaultVersion(defaultRuntimeCombo.getText());
		return super.performOk();
	}
	
	private void createAllRuntimesLabel(Composite parent) {
		Label allRuntimesLabel = new Label(parent, SWT.NONE);
		allRuntimesLabel.setText("Check to enable or uncheck to disable the Hibernate runtime");
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		allRuntimesLabel.setLayoutData(gridData);	
	}
	
	private void createAllRuntimesTable(Composite parent) {
		Table allRuntimesTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		int index = 0;
		for (String version : RuntimeServiceManager.getInstance().getAllVersions()) {
			tableItems[index] = new TableItem(allRuntimesTable, SWT.FILL);
			tableItems[index].setText(version);
			index++;
		}
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		allRuntimesTable.setLayoutData(gridData);	
	}
	
	private void createDefaultRuntimeLabel(Composite parent) {
		Label defaultRuntimeLabel = new Label(parent, SWT.NONE);
		defaultRuntimeLabel.setText("Default Hibernate runtime:");
	}
	
	private void createDefaultRuntimeCombo(Composite parent) {
		defaultRuntimeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
		defaultRuntimeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}
	
	private void refreshPage() {
		refreshAllRuntimesTable();
		refreshDefaultRuntimeCombo();
	}
	
	private void refreshAllRuntimesTable() {
		for (TableItem tableItem : tableItems) {
			tableItem.setChecked(
					RuntimeServiceManager.getInstance().isServiceEnabled(tableItem.getText()));	
		}
	}
	
	private void refreshDefaultRuntimeCombo() {
		for (String version : RuntimeServiceManager.getInstance().getEnabledVersions()) {
			defaultRuntimeCombo.add(version);
		}
		defaultRuntimeCombo.setText(RuntimeServiceManager.getInstance().getDefaultVersion());
	}
	
}
