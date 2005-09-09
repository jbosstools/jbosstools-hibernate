package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;

public class TypeMappingFormPart extends RevEngSectionPart {

	private TypeMappingView composite;

	public TypeMappingFormPart(Composite parent, FormToolkit toolkit) {
		super(parent, toolkit);
	}

	protected String getSectionDescription() {
		return "Type mappings allows you to define which Hibernate type to use for specific JDBC types.";
	}

	protected String getSectionTitle() {
		return "Type mappings";
	}

	public Control createClient(FormToolkit toolkit) {
		Section section = getSection();
		
		Composite sectionClient = toolkit.createComposite(section);		
		sectionClient.setLayout(new GridLayout());
		
		composite = new TypeMappingView(sectionClient, SWT.NULL) {

			protected String getConsoleConfigurationName() {
				return "hibernate-adv-labA";
			}			
		};
		GridData data = new GridData();
		data.heightHint = 400; // makes the table stay reasonable small when large list available TODO: make it relative
		sectionClient.setLayoutData(data);
			
		adaptRecursively( toolkit, composite);

		return sectionClient;
	}

	public boolean setFormInput(IReverseEngineeringDefinition reveng) {
		composite.setModel(reveng);
		return true;
	}
}
