package org.hibernate.eclipse.mapper.editors.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.hibernate.eclipse.mapper.editors.reveng.xpl.FormTextEntry;

public class ParamDetailsPage extends RevEngDetailsPage implements IDetailsPage, PropertyChangeListener {

	//	private Label label;
	private FormTextEntry nameEntry;
	private FormTextEntry valueEntry;
	
	private IRevEngParameter param;
	
	protected void buildContents(FormToolkit toolkit, Section section, Composite client) {
		section.setText("Generator parameter details");
		section.setDescription("Set the properties of the selected parameter.");
				
		nameEntry = new FormTextEntry(client, toolkit, "Name:", SWT.NULL);
		nameEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				param.setName(entry.getValue());
			}
		});
		
		valueEntry = new FormTextEntry(client, toolkit, "Value:", SWT.NULL);
		valueEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				param.setValue(entry.getValue());
			}
		});
		
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IRevEngParameter newParam = (IRevEngParameter) ((IStructuredSelection)selection).getFirstElement();
		if(param!=null) {
			param.removePropertyChangeListener(this);			
		}
		if(newParam!=null) {
			newParam.addPropertyChangeListener(this);
		}
		
		param = newParam;
		
		update();
	}

	private void update() {
		nameEntry.setValue(param.getName());
		valueEntry.setValue(param.getValue());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();		
	}

}
