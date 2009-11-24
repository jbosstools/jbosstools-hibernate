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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.model.ITableFilter;
import org.hibernate.eclipse.console.model.impl.ReverseEngineeringDefinitionImpl;
import org.hibernate.eclipse.console.utils.LaunchHelper;


@SuppressWarnings("restriction")
public class TableFilterWizardPage extends WizardPage {
	// TODO: clean this up to use a shared wizard model

	ComboDialogField consoleConfigurationName;
	private TableFilterView tfc;
	private final String selectedConfiguratonName;

	protected TableFilterWizardPage(String pageName, String selectedConfiguratonName) {
		super( pageName );
		this.selectedConfiguratonName = selectedConfiguratonName;
		setTitle(HibernateConsoleMessages.TableFilterWizardPage_configure_table_filters);
		setDescription(HibernateConsoleMessages.TableFilterWizardPage_specify_which_catalog);
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		Composite container = new Composite(sc, SWT.NULL);
        sc.setContent(container);
		//container.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));

		GridLayout layout = new GridLayout();

		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;

		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText(HibernateConsoleMessages.TableFilterWizardPage_console_configuration);
		ConsoleConfiguration[] cfg = LaunchHelper.findFilteredSortedConsoleConfigs();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);

		consoleConfigurationName.doFillIntoGrid(container, 3);

		IDialogFieldListener fieldlistener = new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				dialogChanged();
			}
		};
		consoleConfigurationName.setDialogFieldListener(fieldlistener);

		TreeToTableComposite tfc = createTableFilterPart( container );
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan=3;
		tfc.setLayoutData(gd);

		sc.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(sc);

		if(selectedConfiguratonName!=null) {
			consoleConfigurationName.setText(selectedConfiguratonName);
		}
		dialogChanged();
	}

    private void updateWarningStatus(String message) {
        setMessage(message, IMessageProvider.WARNING);
    }

    /**
     * Ensures that contents is ok.
     */
    private void dialogChanged() {
    	//updateButtons();

    	if (hasDuplicates()) {
        	updateWarningStatus(HibernateConsoleMessages.TableFilterWizardPage_table_filters_contains_duplicates);
            return;
        }

    	updateWarningStatus(null);
    }

    /**
     * Updates buttons state.
     */
    private void updateButtons() {
    	String strConsoleConfig = consoleConfigurationName.getText();
    	boolean enabled = true;
    	if (null == strConsoleConfig || 0 == strConsoleConfig.length()) {
    		enabled = false;
    	}
    	
    	// FIXME Commented to fix compilation error under Eclipse 3.4
    	// tfc.setRefreshEnabled(enabled);
    }

    protected boolean hasDuplicates() {
    	boolean res = false;
    	ITableFilter[] filters = getTableFilters();
    	for (int i = 1; i < filters.length; i++) {
        	for (int j = 0; j < i; j++) {
        		if (filters[i].getExclude().equals(filters[j].getExclude()) &&
        			filters[i].getMatchCatalog().equals(filters[j].getMatchCatalog()) &&
        			filters[i].getMatchSchema().equals(filters[j].getMatchSchema()) &&
        			filters[i].getMatchName().equals(filters[j].getMatchName())) {
        			res = true;
        			break;
        		}
        	}
    	}
    	return res;
    }

	private TreeToTableComposite createTableFilterPart(Composite container) {
		tfc = new TableFilterView(container, SWT.NULL){

			protected String getConsoleConfigurationName() {
				return consoleConfigurationName.getText();
			}

		};

		IReverseEngineeringDefinition model = new ReverseEngineeringDefinitionImpl();
		model.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				dialogChanged();
			}
		});
		tfc.setModel(model);
		return tfc;
	}

	public ITableFilter[] getTableFilters() {
		return tfc.getTableFilterList();
	}

}
