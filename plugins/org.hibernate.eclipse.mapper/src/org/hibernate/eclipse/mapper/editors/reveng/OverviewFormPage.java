package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class OverviewFormPage extends FormPage {

	public OverviewFormPage(FormEditor editor) {
		super(editor, OverviewFormPage.class.getName(), "Overview");
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText("Welcome to Hibernate Reverse Engineering");		
	}
	
	protected void setInput(IEditorInput input) {
		super.setInput(input);
	}
	
	
}
