package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class RevEngTableFilterPage extends RevEngFormEditorPart {
	
	public final static String PART_ID = "tablefilter";
	
	public RevEngTableFilterPage(ReverseEngineeringEditor reditor) {
		super(reditor, PART_ID, "Table filters");
	}	

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
				
		GridLayout layout = new GridLayout();
		
		form.getBody().setLayout(layout);
		
		createTableFilterSection();
		
		getManagedForm().setInput(getReverseEngineeringEditor().getReverseEngineeringDefinition());
				
	}

	
	private void createTableFilterSection() {
		TableFilterFormPart part = new TableFilterFormPart(getManagedForm().getForm().getBody(), getManagedForm(), getReverseEngineeringEditor());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
		//gd.heightHint = 500; // makes the table stay reasonable small when large list available TODO: make it relative
		part.getSection().setLayoutData(gd);
		
		getManagedForm().addPart(part);		
	}
	
}
