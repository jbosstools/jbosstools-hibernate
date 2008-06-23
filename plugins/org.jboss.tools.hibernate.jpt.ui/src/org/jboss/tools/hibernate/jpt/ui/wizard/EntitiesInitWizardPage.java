/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.db.JptDbPlugin;
import org.eclipse.jpt.ui.internal.JptUiMessages;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.StringTools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsoleMessages;

/**
 * @author Dmitry Geraskov
 *
 */
public class EntitiesInitWizardPage extends WizardPage {
	
	private ComboDialogField connectionProfileName;
	
	private StringButtonDialogField schemaName;
	
	private ComboDialogField consoleConfigurationName;
	
	private Group dbGroup;

	private JpaProject jpaProject;
	
	public EntitiesInitWizardPage(JpaProject jpaProject){
		super("", "", null);
		this.jpaProject = jpaProject;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		int numColumns = 3;

		container.setLayout(layout);
		layout.numColumns = numColumns;
		layout.verticalSpacing = 10;
		
		createDBGroup(container, numColumns);
		
		ComboDialogField consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_console_configuration);
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurationsSortedByName();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);
		
		IDialogFieldListener fieldlistener = new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				dialogChanged();
			}
		};

        consoleConfigurationName.setDialogFieldListener(fieldlistener);
        consoleConfigurationName.doFillIntoGrid(container, numColumns);
		
		setControl(container);
		setPageComplete( false);
	}

	/**
	 * @param container
	 * @param colCount
	 */
	private void createDBGroup(Composite container, int numColumns) {
		dbGroup = new Group(container, SWT.FILL);
		dbGroup.setLayout(new FillLayout());
		GridLayout layout = new GridLayout();
		dbGroup.setLayout(layout);
		layout.numColumns = numColumns;
		layout.verticalSpacing = 10;
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		dbGroup.setLayoutData(gd);
		dbGroup.setText(JptUiMessages.DatabaseReconnectWizardPage_database);		
		
		connectionProfileName = new ComboDialogField(SWT.READ_ONLY);
		connectionProfileName.setLabelText(JptUiMessages.DatabaseReconnectWizardPage_connection);		
		
		List<String> list = dtpConnectionProfileNames();
		connectionProfileName.setItems((String[]) list.toArray(new String[list.size()]));

		String connectionName = getProjectConnectionProfileName();
		if (!StringTools.stringIsEmpty(connectionName)) {
			connectionProfileName.selectItem(connectionName);
		}
		connectionProfileName.doFillIntoGrid(dbGroup, numColumns);
		
		schemaName = new StringButtonDialogField(new IStringButtonAdapter(){
			public void changeControlPressed(DialogField field) {
				// TODO Auto-generated method stub
				
			}});
		schemaName.setLabelText(JptUiMessages.DatabaseReconnectWizardPage_schema);
		schemaName.setButtonLabel("Refresh");
		Control[] controls = schemaName.doFillIntoGrid(dbGroup, numColumns);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace = true;
	}

	/**
	 * 
	 */
	protected void dialogChanged() {
		// TODO Auto-generated method stub
		
	}
	
	private List<String> dtpConnectionProfileNames() {
		List<String> list = new ArrayList<String>();
		for (Iterator<String> i = CollectionTools.sort(JptDbPlugin.instance().getConnectionProfileRepository().connectionProfileNames()); i.hasNext();) {
			list.add(i.next());
		}
		return list;
	}
	
	private String getProjectConnectionProfileName() {
		return jpaProject.getDataSource().getConnectionProfileName();
	}

}
