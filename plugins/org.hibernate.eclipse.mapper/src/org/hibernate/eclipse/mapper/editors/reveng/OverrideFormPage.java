package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class OverrideFormPage extends FormPage {

	public OverrideFormPage(FormEditor editor) {
		super(editor, OverrideFormPage.class.getName(), "Overrides");		
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.getForm().setText("Specific overrides");
		
		
	}
}
