package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class RevEngTablesPage extends RevEngFormEditorPart {

	public final static String PART_ID = "tables";
	
	public RevEngTablesPage(ReverseEngineeringEditor reditor) {
		super(reditor, PART_ID, "Tables & Columns");			
	}	

	protected void createFormContent(IManagedForm managedForm) {
		
		ScrolledForm form = managedForm.getForm();
				
		GridLayout layout = new GridLayout();
		
		form.getBody().setLayout(layout);
		
		createTablesSection();
		
		getManagedForm().setInput(getReverseEngineeringEditor().getReverseEngineeringDefinition());
				
	}

	
	private void createTablesSection() {
		TablePropertiesBlock block = new TablePropertiesBlock(getReverseEngineeringEditor());
		//getManagedForm().getForm().setText("Tables & Columns");
		block.createContent(getManagedForm());
	}
	
}
