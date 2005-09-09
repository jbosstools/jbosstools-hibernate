package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public class ConsoleConfigNamePart extends RevEngSectionPart {

	private CCombo text;

	public ConsoleConfigNamePart(Composite parent, FormToolkit toolkit) {
		super(parent,toolkit);
		
	}

	public boolean setFormInput(IReverseEngineeringDefinition def) {
		return false;
	}
	
	public void dispose() {
		
	}
	
	Control createClient(FormToolkit toolkit) {
		Composite composite = toolkit.createComposite(getSection());
		composite.setLayout(new GridLayout());
		text = new CCombo(composite, SWT.FLAT | SWT.READ_ONLY);
		
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurations();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		text.setItems(names);
	
		
		return composite;
	}
	
	protected String getSectionDescription() {
		return "Select Console configuration to be used for editing the reverse engineering settings";
	}
	
	protected String getSectionTitle() {
		return "Console Configuration";
	}
	
	String getConsoleConfigName() {
		return text.getText();
	}
}
