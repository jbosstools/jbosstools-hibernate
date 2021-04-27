package org.jboss.tools.hibernate.orm.ui.internal;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.hibernate.runtime.spi.ServiceLookup;

public class RuntimesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		Label label = new Label(composite, SWT.NONE);
		label.setText("Check to enable or uncheck to disable the Hibernate runtime");
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		for (String version : ServiceLookup.getVersions()) {
			TableItem tableItem = new TableItem(table, SWT.FILL);
			tableItem.setText(version);
		}
		table.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return composite;
	}

}
