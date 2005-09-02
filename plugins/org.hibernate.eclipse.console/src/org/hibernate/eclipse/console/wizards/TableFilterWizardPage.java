package org.hibernate.eclipse.console.wizards;

import java.util.List;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.impl.ReverseEngineeringDefinitionImpl;


public class TableFilterWizardPage extends WizardPage {
	// TODO: clean this up to use a shared wizard model
	
	ComboDialogField consoleConfigurationName;
	private TableFilterView tfc;
	private final String selectedConfiguratonName;
	
	protected TableFilterWizardPage(String pageName, String selectedConfiguratonName) {		
		super( pageName );
		this.selectedConfiguratonName = selectedConfiguratonName;
		setTitle("Configure Table filters");
		setDescription("Specify which catalog/schema/tables should be included or excluded from the reverse engineering.");
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite container = new Composite(parent, SWT.NULL);
		//container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));
        
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText("Console &configuration:");
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurations();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);
		
		consoleConfigurationName.doFillIntoGrid(container, 3);
		
		TableFilterComposite tfc = createTableFilterPart( container );
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan=3;
		tfc.setLayoutData(gd);
						
		setControl(container);

		if(selectedConfiguratonName!=null) {
			consoleConfigurationName.setText(selectedConfiguratonName);			
		}
	}

	private TableFilterComposite createTableFilterPart(Composite container) {
		tfc = new TableFilterView(new ReverseEngineeringDefinitionImpl(), container, SWT.NULL){
		
			protected String getConsoleConfigurationName() {
				return consoleConfigurationName.getText();
			}
		
		}; 
		return tfc;
	}

	public ITableFilter[] getTableFilters() {
		return tfc.getTableFilterList();
	}
	
}
