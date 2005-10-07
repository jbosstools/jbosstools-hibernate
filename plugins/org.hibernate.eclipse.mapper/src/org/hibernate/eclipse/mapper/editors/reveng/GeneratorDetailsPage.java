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
import org.hibernate.eclipse.console.model.IRevEngGenerator;

public class GeneratorDetailsPage extends RevEngDetailsPage implements IDetailsPage, PropertyChangeListener {

	//private Label label;
	private FormTextEntry nameEntry;
	
	private IRevEngGenerator generator;
	
	protected void buildContents(FormToolkit toolkit, Section section, Composite client) {
		section.setText("Id Generator details");
		section.setDescription("Set the properties of the selected generator.");
				
		nameEntry = new FormTextEntry(client, toolkit, "Class:", SWT.NULL);
		nameEntry.setFormEntryListener(new FormTextEntryListenerAdapter() {
			public void textValueChanged(FormTextEntry entry) {
				generator.setGeneratorClassName(entry.getValue());
			}
		});
			
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IRevEngGenerator newTable = (IRevEngGenerator) ((IStructuredSelection)selection).getFirstElement();
		if(generator!=null) {
			generator.removePropertyChangeListener(this);			
		}
		if(newTable!=null) {
			newTable.addPropertyChangeListener(this);
		}
		generator = newTable;
		
		update();
	}

	private void update() {
		nameEntry.setValue(generator.getGeneratorClassName());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();		
	}

}
