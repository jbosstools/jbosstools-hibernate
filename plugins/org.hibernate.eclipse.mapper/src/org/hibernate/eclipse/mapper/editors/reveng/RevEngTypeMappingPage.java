package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class RevEngTypeMappingPage extends RevEngFormEditorPart {
	
	public final static String PART_ID = "typemappings";
	
	public RevEngTypeMappingPage(ReverseEngineeringEditor reditor) {
		super(reditor, PART_ID, "Type Mappings");		
	}	

	public void createFormContent(IManagedForm mform) {
		ScrolledForm form = mform.getForm();
				
		GridLayout layout = new GridLayout();
		
		
		form.getBody().setLayout(layout);
		
		createTypeMappingSection();
		
		getManagedForm().setInput(getReverseEngineeringEditor().getReverseEngineeringDefinition());
				
	}

	private void createTypeMappingSection() {
		Composite parent = getManagedForm().getForm().getBody();
		
		TypeMappingFormPart part = new TypeMappingFormPart(parent, getManagedForm(),getReverseEngineeringEditor());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
		part.getSection().setLayoutData(gd);
		
		getManagedForm().addPart(part);
	}
		
}
