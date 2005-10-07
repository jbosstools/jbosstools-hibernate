package org.hibernate.eclipse.mapper.editors.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.console.model.IRevEngParameter;
import org.hibernate.eclipse.console.model.IRevEngPrimaryKey;

public class PrimaryKeyDetailsPage extends RevEngDetailsPage implements
		IDetailsPage, PropertyChangeListener {


	//	private Label label;
	
	private IRevEngPrimaryKey primaryKey;
	
	protected void buildContents(FormToolkit toolkit, Section section, Composite client) {
		section.setText("Primary key details");
		section.setDescription("A primary key can define a generator strategy and alternative columns");
				
		Button createGenerator = toolkit.createButton(client, "Add generator", SWT.NONE);
		createGenerator.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				handleAddGenerator();
			}		
		});
		
		Button createColumn = toolkit.createButton(client, "Add column", SWT.NONE);
		createColumn.addSelectionListener(new SelectionAdapter() {
		
			public void widgetSelected(SelectionEvent e) {
				handleAddColumn();
			}		
		});
	}

	protected void handleAddColumn() {
		primaryKey.addColumn();		
	}

	protected void handleAddGenerator() {
		if(primaryKey.getGenerator()==null) {
			primaryKey.addGenerator();
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IRevEngPrimaryKey newParam = (IRevEngPrimaryKey) ((IStructuredSelection)selection).getFirstElement();
		if(primaryKey!=null) {
			primaryKey.removePropertyChangeListener(this);			
		}
		if(newParam!=null) {
			newParam.addPropertyChangeListener(this);
		}
		
		primaryKey = newParam;
		
		update();
	}

	private void update() {
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();		
	}

}
