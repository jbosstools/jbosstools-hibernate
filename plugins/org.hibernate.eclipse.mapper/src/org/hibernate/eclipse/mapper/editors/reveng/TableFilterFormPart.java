package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.wizards.TableFilterView;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class TableFilterFormPart extends RevEngSectionPart {

	private TableFilterView composite;
	private final ReverseEngineeringEditor configNamePart;


	public TableFilterFormPart(Composite body, IManagedForm form, ReverseEngineeringEditor configNamePart) {
		super(body,form);
		this.configNamePart = configNamePart;
	}

	protected String getSectionTitle() {
		return "Table filters";
	}
	
	protected String getSectionDescription() {
		return "Table filters defines which tables/views are included when performing reverse engineering.";
	}
	
	public Control createClient(IManagedForm form) {		
		FormToolkit toolkit = form.getToolkit();
		composite = new TableFilterView(getSection(), SWT.NULL) {

			protected void doRefreshTree() {
				LazyDatabaseSchema lazyDatabaseSchema = configNamePart.getLazyDatabaseSchema();
				if(lazyDatabaseSchema!=null) {
					viewer.setInput( lazyDatabaseSchema );
				}
			}
			protected String getConsoleConfigurationName() {
				return configNamePart.getConsoleConfigurationName();
			}			

		};
			
		adaptRecursively( toolkit, composite );

		return composite;
	}

	public boolean setFormInput(IReverseEngineeringDefinition reveng) {
		composite.setModel(reveng);
		return true;
	}
	
	public void dispose() {
		composite.dispose();
	}
}
