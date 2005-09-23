package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class RevEngFormEditorPart extends FormPage {

/*	public boolean isSaveAsAllowed() {
		return true;
	}*/

	public RevEngFormEditorPart(FormEditor editor, String id, String title) {
		super( editor, id, title );
		
	}
	public void createPartControl(Composite parent) {
		super.createPartControl( parent );
	}
	protected void createFormContent(IManagedForm managedForm) {
		throw new IllegalStateException("Need to override formcontent in " + this.getClass().getName());
	}
		
	ReverseEngineeringEditor getReverseEngineeringEditor() {
		return (ReverseEngineeringEditor) getEditor();
	}
}
