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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.ui.internal.JptUiMessages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences.ConfigurationMode;
import org.hibernate.eclipse.console.EclipseConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.hibernate.tool.hbm2x.StringUtils;
import org.hibernate.util.StringHelper;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;

/**
 * @author Dmitry Geraskov
 *
 */
public abstract class GenerateInitWizardPage extends WizardPage {
	
	private static final String AUTODETECT = "[Autodetect]";
	
	private DriverClassHelpers helper = new DriverClassHelpers();
	
	private ComboDialogField connectionProfileName;
	
	private StringButtonDialogField schemaName;
	
	private ComboDialogField consoleConfigurationName;
	
	private ComboDialogField dialectName;
	
	private Button selectMethod;
	
	private Group dbGroup;

	private JpaProject jpaProject;
	
	private IDialogFieldListener fieldlistener = new IDialogFieldListener() {
		public void dialogFieldChanged(DialogField field) {
			dialogChanged();
		}
	};
	
	public GenerateInitWizardPage(JpaProject jpaProject){
		super("", Messages.title, null);
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
		
		createChildControls(container);
		
		dialectName = new ComboDialogField(SWT.NONE);
		dialectName.setLabelText(HibernateConsoleMessages.NewConfigurationWizardPage_database_dialect);
		dialectName.setItems(getDialectNames());
		dialectName.selectItem(0);
		dialectName.doFillIntoGrid(container, numColumns);
		
		selectMethod = new Button(container, SWT.CHECK);
		selectMethod.setText("Use Console Configuration");
		selectMethod.setSelection(true);
		//selectMethod.setEnabled(false);
		selectMethod.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);				
			}

			public void widgetSelected(SelectionEvent e) {
				consoleConfigurationName.setEnabled(selectMethod.getSelection());
				connectionProfileName.setEnabled(!selectMethod.getSelection());
				schemaName.setEnabled(!selectMethod.getSelection());
				dialogChanged();				
			}});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		selectMethod.setLayoutData(gd);				
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText(HibernateConsoleMessages.CodeGenerationSettingsTab_console_configuration);
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurationsSortedByName();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);
        consoleConfigurationName.setDialogFieldListener(fieldlistener);
        consoleConfigurationName.doFillIntoGrid(container, numColumns);
        
        createDBGroup(container, numColumns);        
		
		setControl(container);
	}

	/**
	 * @param parent
	 */
	protected abstract void createChildControls(Composite parent);


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
		
		//****************************connection profile*****************
		connectionProfileName = new ComboDialogField(SWT.READ_ONLY);
		connectionProfileName.setLabelText(JptUiMessages.DatabaseReconnectWizardPage_connection);		
				
		connectionProfileName.setItems(dtpConnectionProfileNames());

		String connectionName = getProjectConnectionProfileName();
		if (!StringUtils.isEmpty(connectionName)) {
			connectionProfileName.selectItem(connectionName);
		}
		connectionProfileName.doFillIntoGrid(dbGroup, numColumns);
		connectionProfileName.setDialogFieldListener(fieldlistener);
		connectionProfileName.setEnabled(!selectMethod.getSelection());		
		//****************************schema*****************
		schemaName = new StringButtonDialogField(new IStringButtonAdapter(){
			public void changeControlPressed(DialogField field) {
				// TODO Auto-generated method stub
				
			}});
		schemaName.setLabelText(JptUiMessages.DatabaseReconnectWizardPage_schema);
		schemaName.setButtonLabel("Refresh");
		Control[] controls = schemaName.doFillIntoGrid(dbGroup, numColumns);
		// Hack to tell the text field to stretch!
		( (GridData)controls[1].getLayoutData() ).grabExcessHorizontalSpace = true;		
		schemaName.setEnabled(!selectMethod.getSelection());
	}

	
	protected void dialogChanged() {		
		
		if (selectMethod.getSelection() && (StringHelper.isEmpty(getConfigurationName()))){
			setPageComplete(false);
			setErrorMessage("Please, select console configuration");
			return;
		}
		if (!selectMethod.getSelection() && (StringHelper.isEmpty(getConnectionProfileName()))){
			setPageComplete(false);
			setErrorMessage("Please, select connection profile");
			return;
		}
		
		setPageComplete(true);
	}
	
	private String[] dtpConnectionProfileNames() {
		List<String> list = new ArrayList<String>();

		IConnectionProfile[] cps = ProfileManager.getInstance().getProfiles();
		for (int i = 0; i < cps.length; i++) {
			list.add(cps[i].getName());			
		}
		Collections.sort(list);
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	private String getProjectConnectionProfileName() {
		return jpaProject.getDataSource().getConnectionProfileName();
	}
	
	public String getConfigurationName() {
		if (selectMethod.getSelection())
			return consoleConfigurationName.getText();
		return createConsoleConfiguration();
	}
	
	public String getConnectionProfileName() {
		return connectionProfileName.getText();
	}
	
	private String createConsoleConfiguration(){		
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

		String dialect = determineDialect();
		IPath propPath = null;
		if (dialect != null) {
			OutputStream out = null;
			try{
				String propName = HibernateConsoleMessages.AddConfigurationAction_hibernate + ".properties"; 
				propPath = HibernateJptUIPlugin.getDefault().getStateLocation().append(propName);
				File file = propPath.toFile();
				file.createNewFile();
				out = new FileOutputStream(file);
				Properties p = new Properties();
				p.setProperty(BasicHibernateProperties.HIBERNATE_DIALECT, determineDialect());
				p.store(out, null);
			} catch(IOException e){
				
			} finally {
				if (out != null){
					try {
						out.close();
					} catch (IOException e) {
					}
				}
			}
		}
		
		String ccName = launchManager.generateUniqueLaunchConfigurationNameFrom(HibernateConsoleMessages.AddConfigurationAction_hibernate);
		ConsoleConfigurationPreferences prefs = new EclipseConsoleConfigurationPreferences(ccName, 
				ConfigurationMode.JPA, jpaProject.getName(), true, 
				null, null, propPath, 
				new IPath[0], new IPath[0], null, null,
				getConnectionProfileName(), true);
		
		ConsoleConfiguration cc = new ConsoleConfiguration(prefs);
		KnownConfigurations.getInstance().addConfiguration(cc, false);
		
		return ccName;		
	}
	
	public boolean isTemporaryConfiguration(){
		return !selectMethod.getSelection();
	}
	
	public JpaProject getJpaProject(){
		return jpaProject;
	}
	
	public void setWarningMessage(String warning){
		setMessage(warning, WARNING); 
	}
	
	protected String getDefaultOutput(){
		try{
			if (getJpaProject() == null) return "";
			if (getJpaProject().getJavaProject() == null) return "";
			if (!getJpaProject().getJavaProject().exists()) return "";
			IPackageFragmentRoot[] roots = getJpaProject().getJavaProject().getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				IPackageFragmentRoot root = roots[i];
				if (root.getClass() == PackageFragmentRoot.class) {
					if (root.exists()) return root.getResource().getFullPath().toOSString();					
				}							
			}
			return getJpaProject().getJavaProject().getResource().getFullPath().toOSString();
		} catch(JavaModelException e){
			HibernateJptUIPlugin.logException(e);
			return "";
		}
	}
	
	private String[] getDialectNames(){
		String[] dialectNames1 = helper.getDialectNames();
		String[] dialectNames2 = new String[dialectNames1.length + 1];
		dialectNames2[0] = AUTODETECT;
		System.arraycopy(dialectNames1, 0, dialectNames2, 1, dialectNames1.length);
		return dialectNames2;
	}
	
	private String determineDialect() {
		if (!AUTODETECT.equals(dialectName.getText())) 
			return helper.getDialectClass(dialectName.getText());
		if (!selectMethod.getSelection()){
			IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(getConnectionProfileName());
			String driver = profile.getProperties(profile.getProviderId()).getProperty("org.eclipse.datatools.connectivity.db.driverClass");
			return helper.getDialect(driver);
		}
		return null;
	}
}
