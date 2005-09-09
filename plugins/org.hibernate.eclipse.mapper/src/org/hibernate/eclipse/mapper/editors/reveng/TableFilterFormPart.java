package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.wizards.TableFilterView;

public class TableFilterFormPart extends RevEngSectionPart {

	private TableFilterView composite;
	private final ConsoleConfigNamePart configNamePart;


	public TableFilterFormPart(Composite body, FormToolkit toolkit, ConsoleConfigNamePart configNamePart) {
		super(body,toolkit);
		this.configNamePart = configNamePart;
	}

	protected String getSectionTitle() {
		return "Table filters";
	}
	
	protected String getSectionDescription() {
		return "Table filters defines which tables/views are included when performing reverse engineering.";
	}
	
	public Control createClient(FormToolkit toolkit) {
		Section section = getSection();
		
		Composite sectionClient = toolkit.createComposite(section);		
		sectionClient.setLayout(new GridLayout());
		
		composite = new TableFilterView(sectionClient, SWT.NULL) {

			protected String getConsoleConfigurationName() {
				return configNamePart.getConsoleConfigName();
			}			
		};
		GridData data = new GridData();
		data.heightHint = 400; // makes the table stay reasonable small when large list available TODO: make it relative
		composite.setLayoutData(data);
			
		adaptRecursively( toolkit, composite );

		return sectionClient;
	}

	public boolean setFormInput(IReverseEngineeringDefinition reveng) {
		composite.setModel(reveng);
		return true;
	}
	
	public void dispose() {
		composite.dispose();
	}
}
