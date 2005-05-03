package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

public class PreviewMappingFormPage extends FormPage {

	public PreviewMappingFormPage(FormEditor editor) {
		super(editor, PreviewMappingFormPage.class.getName(), "Preview");
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.getForm().setText("Preview");
	}

}
