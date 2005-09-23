package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;
import org.hibernate.util.StringHelper;

public class ConsoleConfigNamePart extends RevEngSectionPart {

	private CCombo text;
	private ReverseEngineeringEditor re;
	
	
	public ConsoleConfigNamePart(Composite parent, IManagedForm form, ReverseEngineeringEditor re) {
		super(parent,form);
		this.re = re;
	}

	public boolean setFormInput(IReverseEngineeringDefinition def) {
		if(StringHelper.isEmpty(text.getText())) {
			String initialConfg = "";
			try {
				if (re.getHibernateNature()!=null) {
					initialConfg = re.getHibernateNature().getDefaultConsoleConfigurationName();
				}
			} catch (CoreException e) {
				MapperPlugin.getDefault().getLogger().logException("Problem when trying to Hibernate Project information",e);
			}
			
			text.setText(initialConfg);
		}
		return false;
	}
	
	public void dispose() {
		
	}
	
	Control createClient(IManagedForm form) {
		FormToolkit toolkit = form.getToolkit();
		Composite composite = toolkit.createComposite(getSection());
		composite.setLayout(new GridLayout());
		text = new CCombo(composite, SWT.FLAT);
		text.setEditable(false);
		adaptRecursively(toolkit, text);
		
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
