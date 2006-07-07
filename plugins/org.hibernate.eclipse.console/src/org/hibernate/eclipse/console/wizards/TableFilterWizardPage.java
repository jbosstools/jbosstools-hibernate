/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.wizards;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.impl.ReverseEngineeringDefinitionImpl;


public class TableFilterWizardPage extends WizardPage {
	// TODO: clean this up to use a shared wizard model
	
	ComboDialogField consoleConfigurationName;
	private TableFilterView tfc;
	private final String selectedConfiguratonName;
	
	protected TableFilterWizardPage(String pageName, String selectedConfiguratonName) {		
		super( pageName );
		this.selectedConfiguratonName = selectedConfiguratonName;
		setTitle("Configure Table filters");
		setDescription("Specify which catalog/schema/tables should be included or excluded from the reverse engineering.");
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite container = new Composite(parent, SWT.NULL);
		//container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));
        
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText("Console &configuration:");
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurations();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);
		
		consoleConfigurationName.doFillIntoGrid(container, 3);
		
		TreeToTableComposite tfc = createTableFilterPart( container );
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan=3;
		tfc.setLayoutData(gd);
						
		setControl(container);

		if(selectedConfiguratonName!=null) {
			consoleConfigurationName.setText(selectedConfiguratonName);			
		}
	}

	private TreeToTableComposite createTableFilterPart(Composite container) {
		tfc = new TableFilterView(container, SWT.NULL){
		
			protected String getConsoleConfigurationName() {
				return consoleConfigurationName.getText();
			}
		
		}; 
		tfc.setModel(new ReverseEngineeringDefinitionImpl());
		return tfc;
	}

	public ITableFilter[] getTableFilters() {
		return tfc.getTableFilterList();
	}
	
}
