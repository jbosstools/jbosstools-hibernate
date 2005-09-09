package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class RevEngOverviewPage extends EditorPart {

	private ManagedForm managedForm;
	private final ReverseEngineeringEditor reditor;
	private ConsoleConfigNamePart configNamePart;
	
	public RevEngOverviewPage(ReverseEngineeringEditor reditor) {
		this.reditor = reditor;		
	}
	
	public void doSave(IProgressMonitor monitor) {
		
	}

	public void doSaveAs() {

	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		//TODO:setup connection to input		
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void createPartControl(Composite parent) {
		managedForm = new ManagedForm(parent);
		ScrolledForm form = managedForm.getForm();
		form.setText("Overview");
		
		/*TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;*/
		ColumnLayout layout = new ColumnLayout();
		
		form.getBody().setLayout(layout);
		
		createNotificationCounterSection();
		createConsoleConfigName();
		createTableFilterSection();
		createTypeMappingSection();
		
		managedForm.setInput(reditor.getReverseEngineeringDefinition());
		
		//updateTableFiltersTable(reditor.getReverseEngineeringDefinition());
	}

	private void createTypeMappingSection() {
		Composite parent = managedForm.getForm().getBody();
		
		TypeMappingFormPart part = new TypeMappingFormPart(parent, managedForm.getToolkit());
		managedForm.addPart(part);
	}

	private void createConsoleConfigName() {
		Composite parent = managedForm.getForm().getBody();
		
		configNamePart = new ConsoleConfigNamePart(parent, managedForm.getToolkit());
		
		managedForm.addPart(configNamePart);
			
	}
	
	private void createNotificationCounterSection() {
		/*Composite parent = managedForm.getForm().getBody();
		
		CounterFormPart part = new CounterFormPart(parent, managedForm.getToolkit());
		
		managedForm.addPart(part);
		*/
	}
	private void createTableFilterSection() {
		TableFilterFormPart part = new TableFilterFormPart(managedForm.getForm().getBody(), managedForm.getToolkit(), configNamePart);
		managedForm.addPart(part);		
	}

	

	public void setFocus() {
		managedForm.getForm().setFocus();
	}
	
	public void dispose() {
		managedForm.dispose();
		super.dispose();
	}

	
}
