package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.workbench.LazyDatabaseSchema;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class TypeMappingFormPart extends RevEngSectionPart {

	private TypeMappingView composite;
	private final ReverseEngineeringEditor configNamePart;

	public TypeMappingFormPart(Composite parent, IManagedForm form, ReverseEngineeringEditor configNamePart) {
		super(parent, form);
		this.configNamePart=configNamePart;
	}

	protected String getSectionDescription() {
		return "Type mappings allows you to define which Hibernate type to use for specific JDBC types.";
	}

	protected String getSectionTitle() {
		return "Type mappings";
	}

	public Control createClient(IManagedForm form) {
		FormToolkit toolkit = form.getToolkit();
		composite = new TypeMappingView(getSection(), SWT.NULL) {
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
				
		GridData gd = new GridData(SWT.FILL,SWT.FILL);
		gd.heightHint = 400;
		composite.setLayoutData(gd);
		
		adaptRecursively( toolkit, composite);

		return composite;
	}

	public boolean setFormInput(IReverseEngineeringDefinition reveng) {
		composite.setModel(reveng);
		return true;
	}
}
